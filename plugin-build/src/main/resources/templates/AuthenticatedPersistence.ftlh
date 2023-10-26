package ${packageName}

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction


interface AuthenticatedPersistence {
    fun <T> execute(userId: String, block: () -> T): T

    fun isConnected(): Boolean
}

class AuthenticatedPersistenceDefault : AuthenticatedPersistence {
    companion object {
        fun create(
            host: String,
            port: Int,
            database: String,
            username: String,
            password: String,
            driverClassName: String = "org.postgresql.Driver",
            jdbcUrl: String = "jdbc:postgresql://$host:$port/${r"${database}"}"
        ): AuthenticatedPersistence {
            val hikariConfig = HikariConfig().apply {
                this.jdbcUrl = jdbcUrl
                this.username = username
                this.password = password
                this.driverClassName = driverClassName
            }
            val dataSource = HikariDataSource(hikariConfig)
            Database.connect(dataSource)
            return AuthenticatedPersistenceDefault()
        }
    }

    override fun <T> execute(userId: String, block: () -> T): T {
        return transaction {
            this.exec("""SET LOCAL SESSION AUTHORIZATION "$userId";""")
            block()
        }
    }

    override fun isConnected(): Boolean =
        try {
            transaction {
                exec("SELECT 1")
            }
            true
        } catch (_: Throwable) {
            false
        }
}