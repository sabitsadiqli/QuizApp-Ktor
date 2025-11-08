package com.quiz

import com.quiz.di.authModule
import com.quiz.error_handle.ApiException
import com.quiz.error_handle.ErrorResponse
import com.quiz.facade.AuthFacade
import com.quiz.facade.QuizFacade
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import com.quiz.modules.auth.authRoutes
import com.quiz.modules.quiz.quizRoutes

fun main() {
    embeddedServer(Netty, port = 8080) {
        module()
    }.start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()
    val authFacade: AuthFacade by inject()
    val quizFacade: QuizFacade by inject()
    install(ContentNegotiation) {
        json()
    }

    install(Koin) {
        slf4jLogger()
        modules(authModule)
    }

    install(StatusPages) {
        exception<ApiException> { call, cause ->
            val errorResponse = ErrorResponse(
                code = cause.statusCode.value,
                description = cause.description,
                message = cause.message
            )
            call.respond(cause.statusCode, errorResponse)
        }

        status(HttpStatusCode.NotFound) { call, _ ->
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(404, "Not Found", "Səhifə tapılmadı.")
            )
        }

        status(HttpStatusCode.Unauthorized) { call, _ ->
            call.respond(
                HttpStatusCode.Unauthorized,
                ErrorResponse(401, "Unauthorized", "Giriş icazəniz yoxdur.")
            )
        }
    }

    routing {
        authRoutes(authFacade)
        quizRoutes(quizFacade)
    }
}

