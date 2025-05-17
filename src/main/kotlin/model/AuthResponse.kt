package model

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String
)