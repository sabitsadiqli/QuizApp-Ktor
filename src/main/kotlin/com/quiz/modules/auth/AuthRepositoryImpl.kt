package com.quiz.modules.auth

import com.quiz.user.RegisterResponse
import com.quiz.user.User
import com.quiz.user.UserTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class AuthRepositoryImpl : AuthRepository {
    override suspend fun validateUserCredentials(userId: String, password: String): User = transaction {
        val row = UserTable.select {
            (UserTable.userId eq userId) and (UserTable.password eq password)
        }.singleOrNull() ?: error("Invalid credentials")

        User(
            id = row[UserTable.id].value,
            userId = row[UserTable.userId],
            password = row[UserTable.password],
            isAdmin = row[UserTable.isAdmin]
        )
    }


    override suspend fun register(userId: String, password: String,isAdmin: Boolean): RegisterResponse {
        return try {
            transaction {
                UserTable.insert {
                    it[UserTable.userId] = userId
                    it[UserTable.password] = password
                    it[UserTable.isAdmin] = isAdmin
                }
            }

            RegisterResponse(message = "User registered successfully",isRegistered = true)
        } catch (e: Exception) {
            RegisterResponse(message = e.localizedMessage ?: "Unknown error",isRegistered = false)
        }
    }

    override suspend fun getUserByUserId(userId: String): User? {
        return transaction {
            UserTable.select { UserTable.userId eq userId }
                .mapNotNull { row ->
                    User(
                        id = row[UserTable.id].value,
                        userId = row[UserTable.userId],
                        password = row[UserTable.password],
                        isAdmin = row[UserTable.isAdmin],
                    )
                }
                .singleOrNull()
        }
    }
}