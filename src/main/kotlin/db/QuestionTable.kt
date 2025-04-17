package com.quiz.db

import org.jetbrains.exposed.sql.Table

object QuestionTable : Table("questions") {
    val id = integer("id").autoIncrement()
    val question = varchar("question", 255)
    val optionA = varchar("optionA", 255)
    val optionB = varchar("optionB", 255)
    val optionC = varchar("optionC", 255)
    val optionD = varchar("optionD", 255)
    val correctIndex = integer("correctIndex")
    val level = integer("level")

    override val primaryKey = PrimaryKey(id)
}