package com.quiz.db

import org.jetbrains.exposed.dao.id.IntIdTable

object QuizResultTable : IntIdTable("quiz_results") {
    val level = integer("level")
    val totalQuestions = integer("total_questions")
    val correctCount = integer("correct_count")
    val percentage = double("percentage")
}