package dev.bogwalk.routes

import dev.bogwalk.databases.DAOFacade
import dev.bogwalk.models.Question
import dev.bogwalk.models.Review
import io.ktor.http.*
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
    // only used for tests - possible to extract?
    get<Decks.DeckId.Questions.QuestionId> { question ->
        val result = dao.question(question.id_q) ?: return@get call.respond(HttpStatusCode.NotFound)
        call.respond(result)
    }
    post<Decks.DeckId.Questions> {
        val deckId = call.parameters.getOrFail("id_d").toInt()
        val toAdd = call.receive<Question>()
        val newQuestion = dao.addNewQuestion(
            deckId, toAdd.content,
            toAdd.optionalAnswers[0], toAdd.optionalAnswers[1], toAdd.optionalAnswers[2], toAdd.optionalAnswers[3],
            toAdd.expectedAnswer
        )
        // 201 Created
        call.respond(HttpStatusCode.Created, newQuestion)
    }
    put<Decks.DeckId.Questions.QuestionId> { question ->
        val toUpdate = call.receive<Question>()
        if (dao.editQuestion(
                question.id_q, toUpdate.content,
                toUpdate.optionalAnswers[0], toUpdate.optionalAnswers[1], toUpdate.optionalAnswers[2], toUpdate.optionalAnswers[3],
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