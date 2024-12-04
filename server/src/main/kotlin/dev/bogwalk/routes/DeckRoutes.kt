package dev.bogwalk.routes

import dev.bogwalk.databases.DAOFacade
import dev.bogwalk.models.Deck
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.deckRouting(dao: DAOFacade) {
    get<Decks> {
        call.respond(dao.allDecks())  // 200 OK = default status
    }
    // only used for tests - possible to extract?
    get<Decks.DeckId> { deck ->
        // 404 Not Found
        val result = dao.deck(deck.id_d) ?: return@get call.respond(HttpStatusCode.NotFound)
        call.respond(result)
    }
    post<Decks> {
        val newDeck = dao.addNewDeck(call.receive())
        // 201 Created
        call.respond(HttpStatusCode.Created, newDeck)
    }
    put<Decks.DeckId> { deck ->
        val toUpdate = call.receive<Deck>()
        if (dao.editDeck(deck.id_d, toUpdate.name)) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }
    delete<Decks.DeckId> { deck ->
        if (dao.deleteDeck(deck.id_d)) {
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }
}