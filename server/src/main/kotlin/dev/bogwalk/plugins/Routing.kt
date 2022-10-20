package dev.bogwalk.plugins

import dev.bogwalk.databases.DAOFacade
import dev.bogwalk.routes.deckRouting
import dev.bogwalk.routes.questionRouting
import io.ktor.server.application.*
import io.ktor.server.resources.Resources
import io.ktor.server.routing.*

fun Application.configureRouting(dao: DAOFacade) {
    install(Resources)

    routing {
        deckRouting(dao)
        questionRouting(dao)
    }
}