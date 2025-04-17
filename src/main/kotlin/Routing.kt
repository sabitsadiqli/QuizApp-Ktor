package com.quiz

import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.quizRoutes(facade: QuizFacade) {
    route("/quiz") {
        get {
            val data = facade.getAllQuestions()
            call.respond(data)
        }
    }
    route("/quizByLevel") {
        get {
            val level = call.request.queryParameters["level"]?.toIntOrNull() ?: 1
            call.respond(facade.getQuestionsByLevel(level))
        }
    }
}