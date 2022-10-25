package dev.bogwalk.routes

import dev.bogwalk.databases.DAOFacade
import dev.bogwalk.models.Question
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
    get<Decks.DeckId.Questions> {
        val deckId = call.parameters.getOrFail("id_d").toInt()
        call.respond(dao.allQuestions(deckId))
    }
    get<Decks.DeckId.Questions.QuestionId> { question ->
        val result = dao.question(question.id_q, question.parent.parent.id_d) ?: return@get call.respond(HttpStatusCode.NotFound)
        call.respond(result)
    }
    post<Decks.DeckId.Questions> {
        val deckId = call.parameters.getOrFail("id_d").toInt()
        val toAdd = call.receive<Question>()
        val newQuestion = dao.addNewQuestion(
            deckId, toAdd.content,
            toAdd.optionalAnswer1, toAdd.optionalAnswer2, toAdd.optionalAnswer3, toAdd.optionalAnswer4,
            toAdd.expectedAnswer
        ) ?: return@post call.respond(HttpStatusCode.BadGateway)
        // 201 Created
        call.respond(HttpStatusCode.Created, newQuestion)
    }
    put<Decks.DeckId.Questions.QuestionId> { question ->
        val toUpdate = call.receive<Question>()
        if (dao.editQuestion(
                question.parent.parent.id_d, question.id_q, toUpdate.content,
                toUpdate.optionalAnswer1, toUpdate.optionalAnswer2, toUpdate.optionalAnswer3, toUpdate.optionalAnswer4,
                toUpdate.expectedAnswer
            )
        ) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }
    delete<Decks.DeckId.Questions.QuestionId> { question ->
        if (dao.deleteQuestion(question.id_q, question.parent.parent.id_d)) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }
}