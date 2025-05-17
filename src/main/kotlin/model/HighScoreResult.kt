package model

import kotlinx.serialization.Serializable

@Serializable
data class HighScoreResult(
    val userId: String,
    val level: Int,
    val highestScore: Double
)