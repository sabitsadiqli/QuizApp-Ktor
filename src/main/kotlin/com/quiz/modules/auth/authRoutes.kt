package com.quiz.modules.auth

import com.quiz.facade.AuthFacade
import com.quiz.error_handle.ApiException
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.quiz.user.User


fun Route.authRoutes(authFacade: AuthFacade) {
    post("/auth/login") {
        val request = call.receive<User>()

        if (request.userId.isBlank() || request.password.isBlank()) {
            throw ApiException(HttpStatusCode.BadRequest, "İstifadəçi ID və şifrə boş ola bilməz.")
        }

        val isValidUser = authFacade.login(request.userId, request.password)
        if (isValidUser != null) {
            call.respond(isValidUser)
        } else {
            throw ApiException(HttpStatusCode.BadRequest, "İstifadəçi ID və ya şifrə yanlışdır.")
        }
    }

    post("auth/register") {
        val user = call.receive<User>()
        val existingUser = authFacade.getUserByUserId(user.userId)

        if (existingUser != null) {
            throw ApiException(
                HttpStatusCode.Conflict,
                "Bu userId artıq mövcuddur. Zəhmət olmasa fərqli bir userId seçin."
            )
        }

        if (user.userId.isBlank() || user.password.isBlank()) {
            throw ApiException(HttpStatusCode.BadRequest, "İstifadəçi ID və şifrə boş ola bilməz.")
        }

        val isRegistered = authFacade.register(user.userId, user.password)
        call.respond(HttpStatusCode.Created, isRegistered)
    }
}