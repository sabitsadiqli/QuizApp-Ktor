package com.quiz

import com.quiz.db.QuestionTable
import com.quiz.db.QuizResultTable
import db.user.UserTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val db = Database.connect(
            "jdbc:h2:./quizdb;MODE=MySQL;DB_CLOSE_DELAY=-1;",
            driver = "org.h2.Driver",
            user = "root",
            password = ""
        )
        transaction(db) {
            SchemaUtils.create(QuestionTable)
            SchemaUtils.create(QuizResultTable)
            SchemaUtils.create(UserTable)
        }
    }
}