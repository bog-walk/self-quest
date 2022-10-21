package dev.bogwalk.routes

import dev.bogwalk.databases.DAOFacade
import dev.bogwalk.models.Deck
import io.ktor.http.*
import io.ktor.server.application.*
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
    post<Decks> {
        // 502 Bad Gateway
        val newDeck = dao.addNewDeck(call.receive<Deck>().name) ?: return@post call.respond(HttpStatusCode.BadGateway)
        // 201 Created
        call.respond(HttpStatusCode.Created, newDeck)
    }
    get<Decks.DeckId> { deck ->
        // 404 Not Found
        val result = dao.deck(deck.id_d) ?: return@get call.respond(HttpStatusCode.NotFound)
        call.respond(result)
    }
    // Should this also return the updated Deck or keep using the updated Deck client-side
    put<Decks.DeckId> { deck ->
        val toUpdate = call.receive<Deck>()
        if (dao.editDeck(deck.id_d, toUpdate.name, toUpdate.size)) {
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