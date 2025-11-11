package com.quiz.modules.quiz

import com.quiz.db.GradeTable
import com.quiz.db.QuestionTable
import com.quiz.db.QuizTable
import com.quiz.db.UserQuizProgressTable
import com.quiz.modules.quiz.model.Category
import com.quiz.modules.quiz.model.QuestionCreateRequest
import com.quiz.modules.quiz.model.QuestionDTO
import com.quiz.modules.quiz.model.QuizWithStatus
import com.quiz.user.UserTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

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


    override fun getAllCategories(): List<Category> = transaction {
        GradeTable.selectAll().map { row ->
            Category(
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
                    image = row[QuestionTable.image],
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

    //Add quiz
    override fun addQuizToCategory(categoryId: Int, title: String): QuizWithStatus = transaction {
        val newQuizId = QuizTable.insertAndGetId {
            it[grade] = EntityID(categoryId, GradeTable)
            it[QuizTable.title] = title
            it[isLocked] = true // new quizzes are locked by default
        }.value

        QuizWithStatus(
            id = newQuizId,
            title = title,
            isUnlocked = true,
            isCompleted = false,
        )
    }

    override fun addQuestionWithCategoryAndQuiz(
        categoryId: Int?,
        categoryName: String?,
        quizId: Int?,
        quizTitle: String?,
        question: QuestionCreateRequest
    ): QuestionDTO = transaction {
        val finalCategoryId = GradeTable.insertAndGetId {
            it[name] =
                categoryName ?: throw IllegalArgumentException("Category name required when creating new category")
            it[isLocked] = false
        }.value

        val finalQuizId = quizId ?: QuizTable.insertAndGetId {
            it[grade] = EntityID(finalCategoryId, GradeTable)
            it[title] = quizTitle ?: throw IllegalArgumentException("Quiz title required when creating new quiz")
            it[isLocked] = true
        }.value

        val newQuestionId = QuestionTable.insertAndGetId {
            it[quiz] = EntityID(finalQuizId, QuizTable)
            it[questionText] = question.questionText
            it[optionA] = question.options[0]
            it[optionB] = question.options[1]
            it[optionC] = question.options[2]
            it[optionD] = question.options[3]
            it[image] = question.image ?: ""
            it[correctAnswerIndex] = question.correctAnswerIndex
        }.value

        QuestionDTO(
            id = newQuestionId,
            questionText = question.questionText,
            options = question.options,
            image = question.image,
            correctAnswerIndex = question.correctAnswerIndex
        )
    }


    override fun editQuestion(questionId: Int, updatedQuestion: QuestionCreateRequest): QuestionDTO = transaction {

        QuestionTable.update({ QuestionTable.id eq questionId }) {
            it[quiz] = EntityID(updatedQuestion.quizId, QuizTable)
            it[questionText] = updatedQuestion.questionText
            it[optionA] = updatedQuestion.options[0]
            it[optionB] = updatedQuestion.options[1]
            it[optionC] = updatedQuestion.options[2]
            it[optionD] = updatedQuestion.options[3]
            it[image] = updatedQuestion.image
            it[correctAnswerIndex] = updatedQuestion.correctAnswerIndex
        }

        QuestionTable.select { QuestionTable.id eq questionId }
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
                    image = row[QuestionTable.image],
                    correctAnswerIndex = row[QuestionTable.correctAnswerIndex]
                )
            }.first()
    }

    override fun getAllQuestionsByQuiz(quizId: Int): List<QuestionDTO> = transaction {
        QuestionTable.select { QuestionTable.quiz eq EntityID(quizId, QuizTable) }
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
                    image = row[QuestionTable.image],
                    correctAnswerIndex = row[QuestionTable.correctAnswerIndex]
                )
            }
    }


}