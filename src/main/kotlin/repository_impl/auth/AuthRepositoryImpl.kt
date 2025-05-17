package repository_impl.auth

import db.RefreshTokenTable
import db.user.UserTable
import model.user.User
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import repository.auth.AuthRepository

class AuthRepositoryImpl : AuthRepository {
    override suspend fun validateUserCredentials(userId: String, password: String): Boolean = transaction {
       UserTable.select { (UserTable.userId eq userId) and (UserTable.password eq password) }
            .count() > 0
    }

    override suspend fun saveRefreshToken(userId: String, refreshToken: String): Unit = transaction {
        val existing = RefreshTokenTable.select { RefreshTokenTable.userId eq userId }.singleOrNull()
        if (existing != null) {
            RefreshTokenTable.update({ RefreshTokenTable.userId eq userId }) {
                it[RefreshTokenTable.token] = refreshToken
            }
        } else {
            RefreshTokenTable.insert {
                it[RefreshTokenTable.userId] = userId
                it[RefreshTokenTable.token] = refreshToken
            }
        }
    }


    override suspend fun getRefreshToken(userId: String): String? = transaction {
        RefreshTokenTable.select { RefreshTokenTable.userId eq userId }
            .map { it[RefreshTokenTable.token] }
            .singleOrNull()
    }

    override suspend fun register(userId: String, password: String): Boolean {
        return try {
            transaction {
                UserTable.insert {
                    it[UserTable.userId] = userId
                    it[UserTable.password] = password
                }
            }
            true
        } catch (e: Exception) {
            false
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
                    )
                }
                .singleOrNull()
        }
    }
}