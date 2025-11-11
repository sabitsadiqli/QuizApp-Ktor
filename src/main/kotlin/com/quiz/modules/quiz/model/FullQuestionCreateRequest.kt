package com.quiz.modules.quiz.model

data class FullQuestionCreateRequest(
    val categoryId: Int? = null,        // if null → new category will be created
    val categoryName: String? = null,   // required if categoryId is null
    val quizId: Int? = null,            // if null → new quiz will be created
    val quizTitle: String? = null,      // required if quizId is null
    val question: QuestionCreateRequest // question details
)