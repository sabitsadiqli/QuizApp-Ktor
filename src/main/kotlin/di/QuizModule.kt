package com.quiz.di

import com.quiz.repository.quiz_repository.QuestionRepository
import com.quiz.repository_impl.quiz.QuestionRepositoryImpl
import com.quiz.QuizFacade
import com.quiz.UserFacade
import com.quiz.repository.user_repository.UserRepository
import org.koin.dsl.module
import repository_impl.user.UserRepositoryImpl

val quizModule = module {
    single<QuestionRepository> { QuestionRepositoryImpl() }
    single { QuizFacade(get()) }

    //user
    single<UserRepository> { UserRepositoryImpl() }
    single { UserFacade(get()) }
}