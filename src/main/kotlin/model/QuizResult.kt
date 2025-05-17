package model

import kotlinx.serialization.Serializable

@Serializable
data class QuizResult(
    val userId: String,
    val level: Int,
    val correctCount: Int,
    val totalQuestions: Int,
    val percentage: Double
)