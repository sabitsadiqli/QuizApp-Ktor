package com.quiz.modules.quiz

import com.quiz.facade.QuizFacade
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Route.quizRoutes(quizFacade: QuizFacade) {
    get("/grades") {
        call.respond(quizFacade.getAllGrades())
    }

    get("/grades/{gradeId}/quizzes") {
        val userId = call.request.queryParameters["userId"] ?: error("Missing userId")
        val gradeId = call.parameters["gradeId"]?.toInt() ?: error("Missing gradeId")

        call.respond(quizFacade.getQuizzesForGrade(userId, gradeId))
    }

    get("/quiz/{quizId}/questions") {
        val quizId = call.parameters["quizId"]?.toInt() ?: error("Missing quizId")

        val questions = quizFacade.getQuestionsByQuiz(quizId)
        call.respond(questions)
    }

    post("/quiz/{quizId}/complete") {
        val userId = call.request.queryParameters["userId"] ?: error("Missing userId")
        val quizId = call.parameters["quizId"]?.toInt() ?: error("Missing quizId")

        quizFacade.completeQuiz(userId, quizId)
        call.respond(HttpStatusCode.OK.value)
    }
}
