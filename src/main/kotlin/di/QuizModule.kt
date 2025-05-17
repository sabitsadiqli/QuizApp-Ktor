package di

import AuthFacade
import repository.quiz_repository.QuestionRepository
import repository_impl.quiz.QuestionRepositoryImpl
import com.quiz.QuizFacade
import org.koin.dsl.module
import repository.auth.AuthRepository
import repository_impl.auth.AuthRepositoryImpl

val quizModule = module {
    single<QuestionRepository> { QuestionRepositoryImpl() }
    single { QuizFacade(get()) }

    //Auth
    single<AuthRepository> { AuthRepositoryImpl() }
    single { AuthFacade(get()) }
}