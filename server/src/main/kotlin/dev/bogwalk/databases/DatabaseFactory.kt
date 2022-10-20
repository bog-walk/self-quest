package dev.bogwalk.databases

import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

interface DatabaseFactory {
    fun createHikariDataSource(): HikariDataSource

    fun connect() {
        Database.connect(createHikariDataSource())
        SchemaDefinition.createSchema()
    }
    suspend fun <T> query(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}