package repository_impl.quiz

import com.quiz.db.QuestionTable
import com.quiz.db.QuizResultTable
import com.quiz.model.AllLevelsQuestions
import com.quiz.model.LevelQuestionGroup
import com.quiz.model.Question
import model.HighScoreResult
import repository.quiz_repository.QuestionRepository
import model.QuizResult
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class QuestionRepositoryImpl : QuestionRepository {

    override suspend fun getQuestions(userId: String): AllLevelsQuestions = newSuspendedTransaction {
        val allQuestions = QuestionTable.selectAll().map {
            Question(
                question = it[QuestionTable.question],
                options = listOf(
                    it[QuestionTable.optionA],
                    it[QuestionTable.optionB],
                    it[QuestionTable.optionC],
                    it[QuestionTable.optionD]
                ),
                correctAnswerIndex = it[QuestionTable.correctIndex]
            ) to it[QuestionTable.level]
        }

        val grouped = allQuestions.groupBy { it.second }.map { (level, items) ->
            LevelQuestionGroup(
                level = level,
                questions = items.map { it.first },
                isUnlocked = false
            )
        }.sortedBy { it.level }

        val userResults = QuizResultTable.select {
            QuizResultTable.userId eq userId
        }.associate {
            it[QuizResultTable.level] to it[QuizResultTable.percentage]
        }

        val unlockedLevels = mutableSetOf(1)

        grouped.forEach { group ->
            val previousLevel = group.level - 1
            val previousResult = userResults[previousLevel]
            if (previousResult != null && previousResult >= 60.0) {
                unlockedLevels.add(group.level)
            }
        }

        val updatedGroups = grouped.map {
            it.copy(isUnlocked = unlockedLevels.contains(it.level))
        }

        AllLevelsQuestions(levels = updatedGroups)
    }

    override suspend fun getQuestionByLevel(level: Int): LevelQuestionGroup = newSuspendedTransaction {
        val questions = QuestionTable.select { QuestionTable.level eq level }.map {
            Question(
                question = it[QuestionTable.question],
                options = listOf(
                    it[QuestionTable.optionA],
                    it[QuestionTable.optionB],
                    it[QuestionTable.optionC],
                    it[QuestionTable.optionD]
                ),
                correctAnswerIndex = it[QuestionTable.correctIndex]
            )
        }

        LevelQuestionGroup(level = level, questions = questions)
    }

    override suspend fun calculateResult(userId: String,level: Int, answers: List<Int>): QuizResult {
        val leveledQuestionsGroup = getQuestionByLevel(level)
        val correctAnswers = leveledQuestionsGroup.questions.map { it.correctAnswerIndex }

        val correctCount = answers.zip(correctAnswers).count { (given, correct) -> given == correct }

        return QuizResult(
            userId = userId,
            level = level,
            correctCount = correctCount,
            totalQuestions = leveledQuestionsGroup.questions.size,
            percentage = if (leveledQuestionsGroup.questions.isNotEmpty())
                (correctCount.toDouble() * 100) / leveledQuestionsGroup.questions.size
            else 0.0
        )
    }

    override suspend fun insertResult(result: QuizResult) {
        transaction {
            QuizResultTable.insert {
                it[level] = result.level
                it[totalQuestions] = result.totalQuestions
                it[correctCount] = result.correctCount
                it[percentage] = result.percentage
                it[userId] = result.userId
            }
        }
    }

    override suspend fun getAllResults(): List<QuizResult> = transaction {
        QuizResultTable.selectAll().map {
            QuizResult(
                level = it[QuizResultTable.level],
                totalQuestions = it[QuizResultTable.totalQuestions],
                correctCount = it[QuizResultTable.correctCount],
                percentage = it[QuizResultTable.percentage],
                userId = it[QuizResultTable.userId],
            )
        }
    }

    override suspend fun unlockNextLevel(currentLevel: Int): Boolean = newSuspendedTransaction {
        val result = QuizResultTable
            .select { QuizResultTable.level eq currentLevel }
            .firstOrNull()

        val percentage = result?.get(QuizResultTable.percentage) ?: return@newSuspendedTransaction false
        return@newSuspendedTransaction percentage >= 60.0
    }

    override suspend fun getAllUsersHighestScores(): List<HighScoreResult> = transaction {
        QuizResultTable
            .slice(QuizResultTable.userId, QuizResultTable.level, QuizResultTable.percentage.max())
            .selectAll()
            .groupBy(QuizResultTable.userId, QuizResultTable.level)
            .map {
                HighScoreResult(
                    userId = it[QuizResultTable.userId],
                    level = it[QuizResultTable.level],
                    highestScore = it[QuizResultTable.percentage.max()]?.toDouble() ?: 0.0
                )
            }
    }
}