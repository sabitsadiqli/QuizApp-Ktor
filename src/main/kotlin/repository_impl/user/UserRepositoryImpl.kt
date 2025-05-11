package repository_impl.user

import com.quiz.repository.user_repository.UserRepository
import db.user.UserTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepositoryImpl : UserRepository {
    override suspend fun register(userId: String, password: String): Boolean {
        return try {
            transaction {
                UserTable.insert {
                    it[UserTable.userId] = userId
                    it[UserTable.password] = password // sadə halda plain saxlayırıq (hash olmalı realda)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun login(userId: String, password: String): Boolean {
        return transaction {
            UserTable.select {
                (UserTable.userId eq userId) and (UserTable.password eq password)
            }.count() > 0
        }
    }
}
