package ch.rethinc.migrations

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import javax.sql.DataSource

fun main() {
    val dataSource = try {
        getDataSource()
    } catch (t: Throwable) {
        throw IllegalArgumentException("Could not connect to database", t)
    }

    val flyway = Flyway.configure().locations("classpath:db/migration").dataSource(dataSource).load()
    flyway.migrate()
}

fun getDataSource(): DataSource {
    val tries = 2
    val secondInMs = 1000L

    var currentTry = 1
    var lastThrowable: Throwable? = null
    while (currentTry <= tries) {
        try {
            return createDataSource(
                host = Environemnt.dbHost,
                port = Environemnt.dbPort,
                database = Environemnt.dbDatabase,
                username = Environemnt.dbUser,
                password = Environemnt.dbPassword
            )
        } catch (t: Throwable) {
            lastThrowable = t
        }
        Thread.sleep(10 * secondInMs)
        currentTry += 1
    }
    throw lastThrowable ?: Exception("Could not connect to database")
}

fun createDataSource(
    host: String,
    port: Int,
    database: String,
    username: String,
    password: String
): DataSource {
    val hikariConfig = HikariConfig().apply {
        this.jdbcUrl = "jdbc:postgresql://$host:$port/${database}"
        this.username = username
        this.password = password
        this.driverClassName = "org.postgresql.Driver"
    }
    return HikariDataSource(hikariConfig)
}