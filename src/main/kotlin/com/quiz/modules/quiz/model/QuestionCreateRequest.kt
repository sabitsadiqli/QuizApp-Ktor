package com.quiz.modules.quiz.model

@kotlinx.serialization.Serializable
data class QuestionCreateRequest(
    val quizId: Int,
    val questionText: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val image: String? = null
)