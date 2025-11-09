package com.quiz.modules.quiz.model

@kotlinx.serialization.Serializable
data class QuizCreateRequest(
    val categoryId: Int,
    val title: String
)