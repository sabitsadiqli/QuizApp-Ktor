package com.quiz

import com.quiz.repository.user_repository.UserRepository

class UserFacade(private val repository: UserRepository) {
    suspend fun login(userId: String, password: String): Boolean {
        return repository.login(userId,password)
    }

    suspend fun register(userId: String, password: String): Boolean {
        return repository.register(userId,password)
    }
}