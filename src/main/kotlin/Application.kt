package com.quiz

import AuthFacade
import di.quizModule
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import kotlin.time.Duration.Companion.seconds

fun Application.main() {
    DatabaseFactory.init()
    val quizFacade: QuizFacade by inject()
    val authFacade: AuthFacade by inject()

    io.ktor.server.engine.embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            json()
        }

        install(Koin) {
            slf4jLogger()
            modules(quizModule)
        }
        install(WebSockets) {
            pingPeriod = 15.seconds
            timeout = 15.seconds
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }

        install(Authentication) {
            jwt("auth-jwt") {
                realm = "quizApp"
                verifier(authFacade.verifier)
                validate {
                    val userId = it.payload.getClaim("userId").asString()
                    if (userId != null) JWTPrincipal(it.payload) else null
                }
            }
        }

        routing {
            quizRoutes(quizFacade)
            authRoutes(authFacade)
        }
    }.start(wait = true)
}

