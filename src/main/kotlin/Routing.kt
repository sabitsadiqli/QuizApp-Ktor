package com.quiz

import AuthFacade
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.QuizResult
import model.QuizSubmissionRequest
import model.RefreshRequest
import model.user.User

fun Route.quizRoutes(facade: QuizFacade) {
    route("/quiz") {
        authenticate("auth-jwt") {
            get {
                val principal = call.principal<JWTPrincipal>()
                println("Authenticated user ${principal!!.payload.getClaim("id").asString()}")
                println("principal user ${principal.payload}")
                val userId = principal.getClaim("userId", String::class)
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)

                val data = facade.getAllQuestions(userId)
                call.respond(data)
            }
        }

        get("/highscores") {
            val data = facade.getAllUsersHighestScores()
            call.respond(data)
        }
    }


    route("/quizByLevel") {
        get {
            val level = call.request.queryParameters["level"]?.toIntOrNull() ?: 1
            call.respond(facade.getQuestionsByLevel(level))
        }
    }
    post("/quiz/submit") {
        val submission = call.receive<QuizSubmissionRequest>()
        val result = facade.submitQuiz(submission.userId,submission.level, submission.answers)
        call.respond(result)
    }
    post("/quiz/result") {
        val result = call.receive<QuizResult>()
        facade.submitQuizResult(result)
        call.respond(HttpStatusCode.Created, mapOf("message" to "Result inserted"))
    }
    get("/quiz/result") {
        val results = facade.fetchAllResults()
        call.respond(results)
    }
}

fun Route.authRoutes(authFacade: AuthFacade) {

    post("/auth/login") {
        val request = call.receive<User>()
        val tokens = authFacade.login(request.userId, request.password)

        if (tokens != null) {
            call.respond(mapOf("accessToken" to tokens.first, "refreshToken" to tokens.second))
        } else {
            call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
        }
    }

    post("auth/register"){
        val request = call.receive<User>()
        val existingUser = authFacade.getUserByUserId(request.userId)

        if (existingUser != null) {
            call.respond(HttpStatusCode.Conflict, "Bu userId artıq mövcuddur.")
            return@post
        }

        val user = authFacade.register(request.userId, request.password)
        call.respond(HttpStatusCode.Created, user)
    }

    post("/auth/refresh") {
        val request = call.receive<RefreshRequest>() // contains userId + refreshToken
        val newAccessToken = authFacade.refreshAccessToken(request.userId, request.refreshToken)

        if (newAccessToken != null) {
            call.respond(mapOf("accessToken" to newAccessToken))
        } else {
            call.respond(HttpStatusCode.Forbidden, "Invalid refresh token")
        }
    }
}