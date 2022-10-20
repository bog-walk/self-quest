package dev.bogwalk.databases

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

class DatabaseFactoryTestImpl : DatabaseFactory {
    private val driver = "org.h2.Driver"
    private val url = "jdbc:h2:mem:;DATABASE_TO_UPPER=false"

    private lateinit var dataSource: HikariDataSource

    override fun createHikariDataSource(): HikariDataSource {
        dataSource = HikariDataSource(HikariConfig().apply {
            driverClassName = driver
            jdbcUrl = url
            maximumPoolSize = 2
            validate()
        })
        return dataSource
    }

    fun close() {
        dataSource.close()
    }
}