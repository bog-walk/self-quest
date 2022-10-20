package dev.bogwalk.plugins

import dev.bogwalk.databases.DAOFacade
import dev.bogwalk.databases.DAOFacadeImpl
import dev.bogwalk.databases.DatabaseFactoryImpl
import io.ktor.server.application.*

fun Application.configureDatabase(): DAOFacade {
    val db = DatabaseFactoryImpl.apply { init(environment.config) }
    db.connect()

    return DAOFacadeImpl(db)
}