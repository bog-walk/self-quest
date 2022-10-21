package dev.bogwalk.databases

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class DatabaseFactoryTestImpl : DatabaseFactory {
    private val driver = "org.h2.Driver"
    private val url = "jdbc:h2:mem:;DATABASE_TO_UPPER=false"

    private lateinit var dataSource: HikariDataSource

    override fun createHikariDataSource(): HikariDataSource {
        dataSource = HikariDataSource(HikariConfig().apply {
            driverClassName = driver
            jdbcUrl = url
            maximumPoolSize = 2
            minimumIdle = 1
            validate()
        })
        return dataSource
    }

    override suspend fun <T> query(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) {
            addLogger(StdOutSqlLogger)
            block()
        }

    fun close() {
        dataSource.close()
    }
}