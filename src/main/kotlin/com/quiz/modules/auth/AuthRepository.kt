package com.quiz.modules.auth

import com.quiz.user.RegisterResponse
import com.quiz.user.User

interface AuthRepository {
    suspend fun validateUserCredentials(userId: String, password: String): Boolean
    suspend fun register(userId: String, password: String,isAdmin: Boolean = false): RegisterResponse
    suspend fun getUserByUserId(userId: String): User?

}