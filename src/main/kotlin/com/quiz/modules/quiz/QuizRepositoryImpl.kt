package com.quiz.modules.quiz

import com.quiz.db.GradeTable
import com.quiz.db.QuestionTable
import com.quiz.db.QuizTable
import com.quiz.db.UserQuizProgressTable
import com.quiz.modules.quiz.model.Grade
import com.quiz.modules.quiz.model.QuestionDTO
import com.quiz.modules.quiz.model.QuizWithStatus
import com.quiz.user.UserTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class QuizRepositoryImpl : QuizRepository {
    override fun unlockInitialQuizzesForUser(userId: String) = transaction {
        val userDbId = UserTable.select { UserTable.userId eq userId }
            .singleOrNull()?.get(UserTable.id)?.value ?: return@transaction

        val firstQuizzes = QuizTable
            .slice(QuizTable.id, QuizTable.grade)
            .selectAll()
            .groupBy { it[QuizTable.grade] }
            .map { it.value.first()[QuizTable.id].value }

        firstQuizzes.forEach { quizId ->
            UserQuizProgressTable.insertIgnore {
                it[user] = EntityID(userDbId, UserTable)
                it[quiz] = EntityID(quizId, QuizTable)
                it[isUnlocked] = true
            }
        }
    }


    override fun getAllGrades(): List<Grade> = transaction {
        GradeTable.selectAll().map { row ->
            Grade(
                id = row[GradeTable.id].value,
                name = row[GradeTable.name],
                isLocked = row[GradeTable.isLocked],
            )
        }
    }

    override fun getQuizzesForGrade(userId: String, gradeId: Int): List<QuizWithStatus> {
        val userDbId = transaction {
            UserTable.select { UserTable.userId eq userId }
                .singleOrNull()?.get(UserTable.id)?.value
        } ?: return emptyList()

        return transaction {
            (QuizTable innerJoin GradeTable)
                .select { QuizTable.grade eq gradeId }
                .map { quizRow ->
                    val quizId = quizRow[QuizTable.id].value
                    val progress = UserQuizProgressTable
                        .select {
                            (UserQuizProgressTable.user eq EntityID(userDbId, UserTable)) and
                                    (UserQuizProgressTable.quiz eq quizId)
                        }
                        .singleOrNull()

                    QuizWithStatus(
                        id = quizId,
                        title = quizRow[QuizTable.title],
                        isUnlocked = progress?.get(UserQuizProgressTable.isUnlocked) ?: false,
                        isCompleted = progress?.get(UserQuizProgressTable.isCompleted) ?: false
                    )
                }
        }
    }

    override fun getQuestionsByQuiz(quizId: Int): List<QuestionDTO> = transaction {
        QuestionTable
            .select { QuestionTable.quiz eq EntityID(quizId, QuizTable) }
            .map { row ->
                QuestionDTO(
                    id = row[QuestionTable.id].value,
                    questionText = row[QuestionTable.questionText],
                    options = listOf(
                        row[QuestionTable.optionA],
                        row[QuestionTable.optionB],
                        row[QuestionTable.optionC],
                        row[QuestionTable.optionD]
                    ),
                    correctAnswerIndex = row[QuestionTable.correctAnswerIndex]
                )
            }
    }


    override fun completeQuiz(userId: String, quizId: Int) {
        val userDbId = transaction {
            UserTable.select { UserTable.userId eq userId }
                .singleOrNull()?.get(UserTable.id)?.value
        } ?: return

        transaction {
            UserQuizProgressTable.update({
                (UserQuizProgressTable.user eq EntityID(userDbId, UserTable)) and
                        (UserQuizProgressTable.quiz eq quizId)
            }) {
                it[isCompleted] = true
                it[isUnlocked] = false
            }
        }
    }
}