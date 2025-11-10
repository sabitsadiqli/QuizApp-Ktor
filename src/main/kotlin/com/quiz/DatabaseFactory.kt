package com.quiz

import com.quiz.db.GradeTable
import com.quiz.db.QuestionTable
import com.quiz.db.QuizTable
import com.quiz.db.UserQuizProgressTable
import com.quiz.user.UserTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val dbUrl = System.getenv("DATABASE_URL")
            ?: throw IllegalArgumentException("DATABASE_URL is not set")
        val dbUser = System.getenv("DB_USER")
            ?: throw IllegalArgumentException("DB_USER is not set")
        val dbPassword = System.getenv("DB_PASSWORD")
            ?: throw IllegalArgumentException("DB_PASSWORD is not set")

        val db = Database.connect(
            url = dbUrl,
            driver = "org.postgresql.Driver",
            user = dbUser,
            password = dbPassword
        )

        transaction(db) {
            SchemaUtils.create(
                UserTable,
                GradeTable,
                QuizTable,
                QuestionTable,
                UserQuizProgressTable
            )
        }
    }
}
