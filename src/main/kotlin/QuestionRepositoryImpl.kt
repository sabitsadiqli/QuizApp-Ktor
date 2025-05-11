package com.quiz

import com.quiz.db.QuestionTable
import com.quiz.db.QuizResultTable
import com.quiz.model.AllLevelsQuestions
import com.quiz.model.LevelQuestionGroup
import com.quiz.model.Question
import model.QuizResult
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class QuestionRepositoryImpl : QuestionRepository {

    override suspend fun getQuestions(): AllLevelsQuestions = newSuspendedTransaction {
        val questions = QuestionTable.selectAll().map {
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

        val grouped = questions.groupBy { it.second }.map { (level, items) ->
            LevelQuestionGroup(
                level = level,
                questions = items.map { it.first }
            )
        }

        AllLevelsQuestions(levels = grouped.sortedBy { it.level })
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

        LevelQuestionGroup(level = level,questions = questions)
    }

    override suspend fun calculateResult(level: Int, answers: List<Int>): QuizResult {
        val leveledQuestionsGroup = getQuestionByLevel(level)
        val correctAnswers = leveledQuestionsGroup.questions.map { it.correctAnswerIndex }

        val correctCount = answers.zip(correctAnswers).count { (given, correct) -> given == correct }

        return QuizResult(
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
                }
            }
    }

    override suspend fun getAllResults(): List<QuizResult> = transaction {
        QuizResultTable.selectAll().map {
            QuizResult(
                level = it[QuizResultTable.level],
                totalQuestions = it[QuizResultTable.totalQuestions],
                correctCount = it[QuizResultTable.correctCount],
                percentage = it[QuizResultTable.percentage]
            )
        }
    }
}
