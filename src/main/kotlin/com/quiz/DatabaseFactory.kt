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
        val db = Database.connect(
            url = System.getenv("DATABASE_URL") ?: "jdbc:postgresql://localhost:5432/quizdb",
            driver = "org.postgresql.Driver",
            user = System.getenv("DB_USER") ?: "postgres",
            password = System.getenv("DB_PASSWORD") ?: "postgres"
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
