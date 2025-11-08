package com.quiz.user

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int? = null,
    val userId: String,
    val password: String,
    val isAdmin: Boolean = false,
)
