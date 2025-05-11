package com.quiz

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.QuizResult
import model.QuizSubmissionRequest

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
    post("/quiz/submit") {
        val submission = call.receive<QuizSubmissionRequest>()
        val result = facade.submitQuiz(submission.level, submission.answers)
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