package com.quiz.error_handle

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val code: Int,
    val description: String,
    val message: String
)
