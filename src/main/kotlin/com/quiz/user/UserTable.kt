package com.quiz.user

import org.jetbrains.exposed.dao.id.IntIdTable

object UserTable : IntIdTable("users") {
    val userId = varchar("user_id", 50).uniqueIndex()
    val password = varchar("password", 100)
    val isAdmin = bool("is_admin").default(false)
}
