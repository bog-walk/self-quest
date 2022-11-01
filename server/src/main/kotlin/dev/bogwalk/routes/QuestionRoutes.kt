package dev.bogwalk.routes

import dev.bogwalk.databases.DAOFacade
import dev.bogwalk.models.Question
import dev.bogwalk.models.Review
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
        val result = dao.question(question.id_q) ?: return@get call.respond(HttpStatusCode.NotFound)
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
                question.id_q, toUpdate.content,
                toUpdate.optionalAnswer1, toUpdate.optionalAnswer2, toUpdate.optionalAnswer3, toUpdate.optionalAnswer4,
                toUpdate.expectedAnswer
            )
        ) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }
    put<Decks.DeckId.Questions.QuestionId.QReview> { review ->
        val toUpdate = call.receiveNullable<Review?>()
        if (dao.editReview(review.parent.id_q, toUpdate?.content ?: "", toUpdate?.references ?: emptyList())) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }
    delete<Decks.DeckId.Questions.QuestionId.QReview> { review ->
        if (dao.deleteReview(review.parent.id_q)) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }
    delete<Decks.DeckId.Questions.QuestionId> { question ->
        if (dao.deleteQuestion(question.id_q)) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }
}