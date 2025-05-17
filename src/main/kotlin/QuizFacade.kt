package com.quiz

import com.quiz.model.AllLevelsQuestions
import com.quiz.model.LevelQuestionGroup
import model.HighScoreResult
import repository.quiz_repository.QuestionRepository
import model.QuizResult


class QuizFacade(private val repository: QuestionRepository) {

    suspend fun getAllQuestions(userId: String): AllLevelsQuestions {
        return repository.getQuestions(userId)
    }

    suspend fun getQuestionsByLevel(level: Int): LevelQuestionGroup {
        return repository.getQuestionByLevel(level)
    }

    suspend fun submitQuiz(userId: String,level: Int, answers: List<Int>): QuizResult {
        return repository.calculateResult(userId = userId,level = level, answers =  answers)
    }

    suspend fun submitQuizResult(result: QuizResult) {
        repository.insertResult(result)
    }

    suspend fun fetchAllResults():List<QuizResult> {
        return repository.getAllResults()
    }

    suspend fun getAllUsersHighestScores():List<HighScoreResult> {
        return repository.getAllUsersHighestScores()
    }

    suspend fun unlockNextLevel(currentLevel: Int, successRate: Double): Boolean {
        if (successRate >= 60.0) {
            return repository.unlockNextLevel(currentLevel)
        }
        return false
    }
}
