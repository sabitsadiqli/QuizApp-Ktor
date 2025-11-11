package com.quiz.modules.quiz

import com.quiz.facade.QuizFacade
import com.quiz.modules.quiz.model.FullQuestionCreateRequest
import com.quiz.modules.quiz.model.QuestionCreateRequest
import com.quiz.modules.quiz.model.QuizCreateRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put

fun Route.adminQuizRoutes(quizFacade: QuizFacade) {

    post("/admin/quiz") {
        val request = call.receive<QuizCreateRequest>()
        val newQuiz = quizFacade.addQuizToCategory(request.categoryId, request.title)
        call.respond(HttpStatusCode.Created, newQuiz)
    }

    post("/admin/question") {
        val request = call.receive<FullQuestionCreateRequest>()
        val newQuestion = quizFacade.addQuestionWithCategoryAndQuiz(
            categoryId = request.categoryId,
            categoryName = request.categoryName,
            quizId = request.quizId,
            quizTitle = request.quizTitle,
            question = request.question
        )
        call.respond(HttpStatusCode.Created, newQuestion)
    }

    // Edit existing question
    put("/admin/question/{questionId}") {
        val questionId = call.parameters["questionId"]?.toInt() ?: error("Missing questionId")
        val request = call.receive<QuestionCreateRequest>()
        val updatedQuestion = quizFacade.editQuestion(questionId, request)
        call.respond(HttpStatusCode.OK, updatedQuestion)
    }

    // Get all questions for a quiz
    get("/admin/quiz/{quizId}/questions") {
        val quizId = call.parameters["quizId"]?.toInt() ?: error("Missing quizId")
        val questions = quizFacade.getAllQuestionsByQuiz(quizId)
        call.respond(questions)
    }
}
