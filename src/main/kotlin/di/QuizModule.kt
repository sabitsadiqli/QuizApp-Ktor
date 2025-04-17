package com.quiz.di

import com.quiz.QuestionRepository
import com.quiz.QuestionRepositoryImpl
import com.quiz.QuizFacade
import org.koin.dsl.module

val quizModule = module {
    single<QuestionRepository> { QuestionRepositoryImpl() }
    single { QuizFacade(get()) }
}