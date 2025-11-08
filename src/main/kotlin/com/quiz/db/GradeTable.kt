package com.quiz.db

import com.quiz.user.UserTable
import org.jetbrains.exposed.dao.id.IntIdTable

object GradeTable : IntIdTable("grades") {
    val name = varchar("name", 50) // e.g. "Grade 1", "Grade 2", etc.
    val isLocked = bool("is_locked").default(false)
}

object QuizTable : IntIdTable("quizzes") {
    val grade = reference("grade_id", GradeTable)
    val title = varchar("title", 100) // e.g. "Quiz 1", "Quiz 2"
    val isLocked = bool("is_locked").default(false)
}

object QuestionTable : IntIdTable("questions") {
    val quiz = reference("quiz_id", QuizTable)
    val questionText = text("question_text")
    val optionA = varchar("option_a", 255)
    val optionB = varchar("option_b", 255)
    val optionC = varchar("option_c", 255)
    val optionD = varchar("option_d", 255)
    val correctAnswerIndex = integer("correct_answer_index")
}

object UserQuizProgressTable : IntIdTable("user_quiz_progress") {
    val user = reference("user_id", UserTable)
    val quiz = reference("quiz_id", QuizTable)
    val isCompleted = bool("is_completed").default(false)
    val isUnlocked = bool("is_unlocked").default(false)
}
