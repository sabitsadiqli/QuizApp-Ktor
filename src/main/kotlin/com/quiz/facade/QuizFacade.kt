package com.quiz.facade

import com.quiz.modules.quiz.QuizRepository
import com.quiz.modules.quiz.model.Category
import com.quiz.modules.quiz.model.QuestionCreateRequest
import com.quiz.modules.quiz.model.QuestionDTO
import com.quiz.modules.quiz.model.QuizWithStatus

class QuizFacade(private val repository: QuizRepository) {
    fun getAllCategories(): List<Category>{
        return repository.getAllCategories()
    }
    fun getQuizForCategory(userId: String, gradeId: Int): List<QuizWithStatus> {
        return repository.getQuizzesForGrade(userId, gradeId)
    }

    fun getQuestionsByQuiz(quizId: Int): List<QuestionDTO> {
        return repository.getQuestionsByQuiz(quizId)
    }

    fun completeQuiz(userId: String, quizId: Int) {
        return repository.completeQuiz(userId, quizId)
    }

    fun addQuizToCategory(categoryId: Int, title: String): QuizWithStatus =
        repository.addQuizToCategory(categoryId, title)

    fun addQuestionToQuiz(quizId: Int, question: QuestionCreateRequest): QuestionDTO =
        repository.addQuestionToQuiz(quizId, question)

    fun editQuestion(questionId: Int, updatedQuestion: QuestionCreateRequest): QuestionDTO =
        repository.editQuestion(questionId, updatedQuestion)

    fun getAllQuestionsByQuiz(quizId: Int): List<QuestionDTO> =
        repository.getAllQuestionsByQuiz(quizId)
}