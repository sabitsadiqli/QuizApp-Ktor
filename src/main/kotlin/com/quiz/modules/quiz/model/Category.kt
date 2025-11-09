package com.quiz.modules.quiz.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Int,
    val name: String,
    val isLocked: Boolean = false,
)