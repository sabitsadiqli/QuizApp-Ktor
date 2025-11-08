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
            "jdbc:h2:./quizdb;MODE=MySQL;DB_CLOSE_DELAY=-1;",
            driver = "org.h2.Driver",
            user = "root",
            password = ""
        )
        transaction(db) {
            SchemaUtils.create(UserTable)
            SchemaUtils.create(GradeTable)
            SchemaUtils.create(QuizTable)
            SchemaUtils.create(QuestionTable)
            SchemaUtils.create(UserQuizProgressTable)
        }
    }
}