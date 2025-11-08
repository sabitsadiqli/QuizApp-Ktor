package com.quiz.facade

import com.quiz.modules.quiz.QuizRepository
import com.quiz.modules.quiz.model.Grade
import com.quiz.modules.quiz.model.QuestionDTO
import com.quiz.modules.quiz.model.QuizWithStatus

class QuizFacade(private val repository: QuizRepository) {
    fun getAllGrades(): List<Grade>{
        return repository.getAllGrades()
    }
    fun getQuizzesForGrade(userId: String, gradeId: Int): List<QuizWithStatus> {
        return repository.getQuizzesForGrade(userId, gradeId)
    }

    fun getQuestionsByQuiz(quizId: Int): List<QuestionDTO> {
        return repository.getQuestionsByQuiz(quizId)
    }

    fun completeQuiz(userId: String, quizId: Int) {
        return repository.completeQuiz(userId, quizId)
    }
}