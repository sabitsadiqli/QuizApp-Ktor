package com.quiz

import com.quiz.model.AllLevelsQuestions
import com.quiz.model.LevelQuestionGroup
import model.QuizResult

interface QuestionRepository {
    suspend fun getQuestions(): AllLevelsQuestions
    suspend fun getQuestionByLevel(level: Int): LevelQuestionGroup
    suspend fun calculateResult(level: Int, answers: List<Int>): QuizResult
    suspend fun insertResult(result: QuizResult)
    suspend fun getAllResults(): List<QuizResult>
}