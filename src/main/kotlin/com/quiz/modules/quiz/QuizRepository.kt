package com.quiz.modules.quiz

import com.quiz.modules.quiz.model.Category
import com.quiz.modules.quiz.model.QuestionCreateRequest
import com.quiz.modules.quiz.model.QuestionDTO
import com.quiz.modules.quiz.model.QuizWithStatus

interface QuizRepository {
    fun unlockInitialQuizzesForUser(userId: String)
    fun getAllCategories(): List<Category>
    fun getQuizzesForGrade(userId: String, gradeId: Int): List<QuizWithStatus>
    fun getQuestionsByQuiz(quizId: Int): List<QuestionDTO>
    fun completeQuiz(userId: String, quizId: Int)
    fun addQuizToCategory(categoryId: Int, title: String): QuizWithStatus
    fun addQuestionWithCategoryAndQuiz(
        categoryId: Int?,
        categoryName: String?,
        quizId: Int?,
        quizTitle: String?,
        question: QuestionCreateRequest
    ): QuestionDTO

    fun editQuestion(questionId: Int, updatedQuestion: QuestionCreateRequest): QuestionDTO
    fun getAllQuestionsByQuiz(quizId: Int): List<QuestionDTO>
}