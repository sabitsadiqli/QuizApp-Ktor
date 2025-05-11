package model

import kotlinx.serialization.Serializable

@Serializable
data class QuizSubmissionRequest(
    val level: Int,
    val answers: List<Int>
)