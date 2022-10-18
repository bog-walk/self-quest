package dev.bogwalk.routes

import dev.bogwalk.models.Question
import dev.bogwalk.models.questionStorage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Route.questionRouting() {
    route("/question") {
        get {
            if (questionStorage.isNotEmpty()) {
                // 200 OK
                call.respond(questionStorage)
            } else {
                call.respondText(
                    // 404 Not Found
                    "No questions found", status = HttpStatusCode.NotFound
                )
            }
        }
        get("/{id?}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                // 400 Bad Request
                "Missing id", status = HttpStatusCode.BadRequest
            )
            application.environment.log.info("Wrong ID = $id")
            val question = questionStorage.find { it.id == id } ?: return@get call.respondText(
                "Invalid id", status = HttpStatusCode.NotFound
            )
            call.respond(question)
        }
        post {
            val formParameters = call.receiveParameters()
            val question = Question(
                id = formParameters.getOrFail("id"),
                content = formParameters.getOrFail("content"),
                optionalAnswers = formParameters.getOrFail("answers").split(" "),
                expectedAnswer = formParameters.getOrFail("expected")
            )
            questionStorage.add(question)
            call.respondText(
                // 201 Created
                "New question added", status = HttpStatusCode.Created
            )
        }
        get("/{id?}/edit") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing id", status = HttpStatusCode.BadRequest
            )
            val index = questionStorage.indexOfFirst { it.id == id }
            if (index == -1) {
                return@get call.respondText(
                    "Invalid id", status = HttpStatusCode.NotFound
                )
            } else {
                val formParameters = call.receiveParameters()
                questionStorage[index] = questionStorage[index].copy(
                    content = formParameters.getOrFail("content")
                )
                call.respondText(
                    // 202 Accepted
                    "Question updated", status = HttpStatusCode.Accepted
                )
            }
        }
        delete("/{id?}") {
            val id = call.parameters["id"] ?: return@delete call.respondText(
                "Missing id", status = HttpStatusCode.BadRequest
            )
            if (questionStorage.removeIf { it.id == id }) {
                call.respondText(
                    "Question deleted", status = HttpStatusCode.Accepted
                )
            } else {
                call.respondText(
                    "Question not found", status = HttpStatusCode.NotFound
                )
            }
        }
    }
}