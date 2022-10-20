package dev.bogwalk.routes

import dev.bogwalk.databases.DAOFacade
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Route.questionRouting(dao: DAOFacade) {
    get<Decks.Id.Questions> {
        val deckId = call.parameters.getOrFail("id").toInt()
        call.respond(dao.allQuestions(deckId))
    }
    post<Decks.Id.Questions> {
        val deckId = call.parameters.getOrFail("id").toInt()
        val formParameters = call.receiveParameters()
        val newQuestion = dao.addNewQuestion(
            deckId,
            formParameters.getOrFail("content"),
            formParameters.getOrFail("options").split(" "),
            formParameters.getOrFail("correct")
        )
        call.respondRedirect("${Routes.ALL_DECKS}/$deckId/${Routes.ALL_QUESTIONS}/${newQuestion?.id}")
    }
    get<Decks.Id.Questions.Id> { question ->
        val result = dao.question(question.id.toInt()) ?: return@get call.respondText(
            "Invalid id", status = HttpStatusCode.NotFound
        )
        call.respond(result)
    }
    put<Decks.Id.Questions.Id> { question ->
        val deckId = call.parameters.getOrFail("id").toInt()
        val formParameters = call.receiveParameters()
        val result = dao.editQuestion(
            question.id.toInt(),
            formParameters.getOrFail("content"),
            formParameters.getOrFail("options").split(" "),
            formParameters.getOrFail("correct")
        )
        if (result) {
            call.respondRedirect("${Routes.ALL_DECKS}/$deckId/${Routes.ALL_QUESTIONS}/${question.id}")
        } else {
            call.respondText(
                "Invalid id", status = HttpStatusCode.NotFound
            )
        }
    }
    delete<Decks.Id.Questions.Id> { question ->
        val deckId = call.parameters.getOrFail("id").toInt()
        if (dao.deleteQuestion(question.id.toInt())) {
            call.respondRedirect("${Routes.ALL_DECKS}/$deckId/${Routes.ALL_QUESTIONS}")
        } else {
            call.respondText(
                "Question not found", status = HttpStatusCode.NotFound
            )
        }
    }
}