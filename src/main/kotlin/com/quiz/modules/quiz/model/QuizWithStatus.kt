package com.quiz.modules.quiz.model

import kotlinx.serialization.Serializable

@Serializable
data class QuizWithStatus(
    val id: Int,
    val title: String,
    val isUnlocked: Boolean,
    val isCompleted: Boolean
)