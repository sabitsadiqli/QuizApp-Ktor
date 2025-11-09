package com.quiz.modules.quiz

import com.quiz.AppConfig
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.*
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import java.io.File

fun Route.imageUploadRoutes() {

    post("/admin/upload-image") {
        val multipart = call.receiveMultipart()
        var fileName: String? = null

        multipart.forEachPart { part ->
            if (part is PartData.FileItem) {
                val uploadDir = File(AppConfig.UPLOAD_DIR)
                if (!uploadDir.exists()) uploadDir.mkdirs()

                fileName = part.originalFileName ?: "image_${System.currentTimeMillis()}.png"
                val fileBytes = part.streamProvider().readBytes()
                File(uploadDir, fileName!!).writeBytes(fileBytes)
            }
            part.dispose()
        }

        if (fileName == null) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "No image uploaded"))
            return@post
        }

        val imageUrl = "${AppConfig.BASE_URL}/${AppConfig.UPLOAD_DIR}/$fileName"
        call.respond(mapOf("imageUrl" to imageUrl))
    }
}
