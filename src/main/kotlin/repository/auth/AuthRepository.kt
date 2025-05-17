package repository.auth

import model.user.User

interface AuthRepository {
    suspend fun validateUserCredentials(userId: String, password: String): Boolean
    suspend fun saveRefreshToken(userId: String, refreshToken: String)
    suspend fun getRefreshToken(userId: String): String?
    suspend fun register(userId: String, password: String): Boolean
    suspend fun getUserByUserId(userId: String): User?
}