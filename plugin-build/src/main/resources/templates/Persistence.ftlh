package ${packageName}

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction


interface Persistence {
    fun <T> execute(block: () -> T): T

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
            jdbcUrl: String = "jdbc:postgresql://$host:$port/${r"${database}"}"
        ): Persistence {
            val hikariConfig = HikariConfig().apply {
                this.jdbcUrl = jdbcUrl
                this.username = username
                this.password = password
                this.driverClassName = driverClassName
            }
            val dataSource = HikariDataSource(hikariConfig)
            Database.connect(dataSource)
            return PersistenceDefault()
        }
    }

    override fun <T> execute(block: () -> T): T {
        return transaction {
            block()
        }
    }

    override fun isConnected(): Boolean =
        try {
            transaction {
                PersonEntity.all()
            }
            true
        } catch (_: Throwable) {
            false
        }
}