package com.quiz.repository.user_repository

interface UserRepository {
    suspend fun register(userId: String, password: String): Boolean
    suspend fun login(userId: String, password: String): Boolean
}