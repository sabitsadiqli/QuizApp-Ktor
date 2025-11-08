package com.quiz.user

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val message: String,
    val isRegistered: Boolean,
)
