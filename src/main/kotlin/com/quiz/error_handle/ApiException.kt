package com.quiz.error_handle

import io.ktor.http.HttpStatusCode

open class ApiException(
    val statusCode: HttpStatusCode,
    override val message: String,
    val description: String = statusCode.description
) : RuntimeException(message)

class BadRequestException(message: String) :
    ApiException(HttpStatusCode.BadRequest, message)

class UnauthorizedException(message: String) :
    ApiException(HttpStatusCode.Unauthorized, message)

class NotFoundException(message: String) :
    ApiException(HttpStatusCode.NotFound, message)

class ConflictException(message: String) :
    ApiException(HttpStatusCode.Conflict, message)
