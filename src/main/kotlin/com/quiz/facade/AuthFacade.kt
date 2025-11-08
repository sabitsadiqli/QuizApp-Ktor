package com.quiz.facade

import com.quiz.user.RegisterResponse
import com.quiz.user.User
import com.quiz.modules.auth.AuthRepository
import com.quiz.modules.quiz.QuizRepository

class AuthFacade(
    private val repository: AuthRepository,
    private val quizRepository: QuizRepository,
) {

    suspend fun login(userId: String, password: String): Boolean? {
        return repository.validateUserCredentials(userId, password)
    }

    suspend fun register(userId: String, password: String): RegisterResponse {
        val response = repository.register(userId, password)

        if (response.isRegistered) {
            val newUser = repository.getUserByUserId(userId)
            newUser?.userId?.let { userDbId ->
                quizRepository.unlockInitialQuizzesForUser(userDbId)
            }
        }

        return response
    }

    suspend fun getUserByUserId(userId: String): User? {
        return repository.getUserByUserId(userId)
    }
}