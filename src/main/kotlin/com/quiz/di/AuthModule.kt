package com.quiz.di

import com.quiz.facade.AuthFacade
import com.quiz.facade.QuizFacade
import org.koin.dsl.module
import com.quiz.modules.auth.AuthRepository
import com.quiz.modules.auth.AuthRepositoryImpl
import com.quiz.modules.quiz.QuizRepository
import com.quiz.modules.quiz.QuizRepositoryImpl

val authModule = module {
    // Repository
    single<AuthRepository> { AuthRepositoryImpl() }
    single<QuizRepository> { QuizRepositoryImpl() }

    // Facade
    single { AuthFacade(get(),get()) }
    single { QuizFacade(get()) }
}