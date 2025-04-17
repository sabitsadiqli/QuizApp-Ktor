package com.quiz

import com.quiz.model.AllLevelsQuestions
import com.quiz.model.LevelQuestionGroup

interface QuestionRepository {
    suspend fun getQuestions(): AllLevelsQuestions
    suspend fun getQuestionByLevel(level: Int): LevelQuestionGroup
}