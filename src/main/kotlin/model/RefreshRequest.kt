package model

import kotlinx.serialization.Serializable

@Serializable
data class RefreshRequest(
    val userId: String,
    val refreshToken: String
)
