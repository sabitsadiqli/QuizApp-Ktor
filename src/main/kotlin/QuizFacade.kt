package com.quiz

import com.quiz.model.AllLevelsQuestions
import com.quiz.model.LevelQuestionGroup


class QuizFacade(private val repository: QuestionRepository) {

    suspend fun getAllQuestions(): AllLevelsQuestions {
        return repository.getQuestions()
    }

    suspend fun getQuestionsByLevel(level: Int): LevelQuestionGroup {
        return repository.getQuestionByLevel(level)
    }
}
