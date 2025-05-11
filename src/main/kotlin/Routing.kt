package com.quiz

import com.quiz.repository.user_repository.UserRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.QuizResult
import model.QuizSubmissionRequest
import model.user.User

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

fun Route.userRoutes(userFacade: UserFacade) {

    post("/register") {
        val request = call.receive<User>()
        val success = userFacade.register(request.userId, request.password)
        call.respond(HttpStatusCode.OK, mapOf("success" to success))
    }

    post("/login") {
        val request = call.receive<User>()
        val success = userFacade.login(request.userId, request.password)
        if (success) {
            call.respond(HttpStatusCode.OK, mapOf("success" to true))
        } else {
            call.respond(HttpStatusCode.Unauthorized, mapOf("success" to false))
        }
    }
}