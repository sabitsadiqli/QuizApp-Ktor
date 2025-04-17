package com.quiz

import com.quiz.di.quizModule
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.main() {
    DatabaseFactory.init()
    val facade: QuizFacade by inject()

    io.ktor.server.engine.embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            json()
        }

        install(Koin) {
            slf4jLogger()
            modules(quizModule)
        }

        routing {
            quizRoutes(facade)
        }
    }.start(wait = true)
}