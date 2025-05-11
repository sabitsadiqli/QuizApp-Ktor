package com.quiz

import com.quiz.model.AllLevelsQuestions
import com.quiz.model.LevelQuestionGroup
import com.quiz.repository.quiz_repository.QuestionRepository
import model.QuizResult


class QuizFacade(private val repository: QuestionRepository) {

    suspend fun getAllQuestions(): AllLevelsQuestions {
        return repository.getQuestions()
    }

    suspend fun getQuestionsByLevel(level: Int): LevelQuestionGroup {
        return repository.getQuestionByLevel(level)
    }

    suspend fun submitQuiz(level: Int, answers: List<Int>): QuizResult {
        return repository.calculateResult(level, answers)
    }

    suspend fun submitQuizResult(result: QuizResult) {
        repository.insertResult(result)
    }

    suspend fun fetchAllResults():List<QuizResult> {
        return repository.getAllResults()
    }
}
