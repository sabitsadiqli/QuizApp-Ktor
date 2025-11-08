package com.quiz.modules.quiz

import com.quiz.modules.quiz.model.Grade
import com.quiz.modules.quiz.model.QuestionDTO
import com.quiz.modules.quiz.model.QuizWithStatus

interface QuizRepository {
    fun unlockInitialQuizzesForUser(userId: String)
    fun getAllGrades(): List<Grade>
    fun getQuizzesForGrade(userId: String, gradeId: Int): List<QuizWithStatus>
    fun getQuestionsByQuiz(quizId: Int): List<QuestionDTO>
    fun completeQuiz(userId: String, quizId: Int)
}