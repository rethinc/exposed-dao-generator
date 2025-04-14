package ${packageName}

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*


interface Persistence {
    fun <T> execute(block: Transaction.() -> T): T

    fun <T> execute(userId: UUID, block: Transaction.() -> T): T

    fun isConnected(): Boolean
}

class PersistenceDefault : Persistence {
    companion object {
        fun create(
            host: String,
            port: Int,
            database: String,
            username: String,
            password: String,
            driverClassName: String = "org.postgresql.Driver",
            jdbcUrl: String = "jdbc:postgresql://$host:$port/${r"${database}"}",
            maximumPoolSize: Int = 10,
        ): Persistence {
            val hikariConfig = HikariConfig().apply {
                this.jdbcUrl = jdbcUrl
                this.username = username
                this.password = password
                this.driverClassName = driverClassName
                this.maximumPoolSize = maximumPoolSize
            }
            val dataSource = HikariDataSource(hikariConfig)
            Database.connect(dataSource)
            return PersistenceDefault()
        }
    }

    override fun <T> execute(block: Transaction.() -> T): T {
        return transaction {
            this.block()
        }
    }

    override fun <T> execute(userId: UUID, block: Transaction.() -> T): T {
        return transaction {
            this.exec("SET LOCAL persistence.authenticated_user_id = '$userId';")
            this.block()
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