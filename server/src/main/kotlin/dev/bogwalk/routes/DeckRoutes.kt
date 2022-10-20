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

fun Route.deckRouting(dao: DAOFacade) {
    get<Decks> {
        call.respond(dao.allDecks())
    }
    post<Decks> {
        val formParameters = call.receiveParameters()
        val name = formParameters.getOrFail("name")
        val newDeck = dao.addNewDeck(name)
        call.respondRedirect("${Routes.ALL_DECKS}/${newDeck?.id}")
    }
    get<Decks.Id> { deck ->
        val result = dao.deck(deck.id.toInt()) ?: return@get call.respondText(
            "Invalid id", status = HttpStatusCode.NotFound
        )
        call.respond(result)
    }
    put<Decks.Id> { deck ->
        val formParameters = call.receiveParameters()
        val newName = formParameters.getOrFail("name")
        if (dao.editDeck(deck.id.toInt(), newName)) {
            call.respondRedirect("${Routes.ALL_DECKS}/${deck.id}")
        } else {
            call.respondText(
                "Invalid id", status = HttpStatusCode.NotFound
            )
        }
    }
    delete<Decks.Id> { deck ->
        if (dao.deleteDeck(deck.id.toInt())) {
            call.respondRedirect(Routes.ALL_DECKS)
        } else {
            call.respondText(
                "Deck not found", status = HttpStatusCode.NotFound
            )
        }
    }
}