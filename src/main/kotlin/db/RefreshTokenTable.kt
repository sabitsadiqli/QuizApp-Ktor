package db

import org.jetbrains.exposed.sql.Table

object RefreshTokenTable : Table("refresh_tokens") {
    val userId = varchar("user_id", 50)
    val token = varchar("token", 255).uniqueIndex()
}
