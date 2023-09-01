package ch.rethinc.exposed.generator

data class DatabaseConfiguration(
    val databaseImage: String,
    val databaseDriver: String,
    val databaseHost: String,
    val databasePort: Int,
    val databaseName: String,
    val databaseUser: String,
    val databasePassword: String,
    val migrationImage: String,
    val migrationTimeoutMinutes: Long
)
