package com.quiz.modules.quiz.model

import kotlinx.serialization.Serializable

@Serializable
data class QuestionDTO(
    val id: Int,
    val questionText: String,
    val options: List<String>,
    val image: String? = null,
    val correctAnswerIndex: Int
)