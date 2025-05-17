package com.quiz.model

import kotlinx.serialization.Serializable

@Serializable
data class AllLevelsQuestions(
    val levels: List<LevelQuestionGroup>
)

@Serializable
data class LevelQuestionGroup(
    val level: Int,
    val isUnlocked: Boolean = false,
    val questions: List<Question>
)

@Serializable
data class Question(
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int
)