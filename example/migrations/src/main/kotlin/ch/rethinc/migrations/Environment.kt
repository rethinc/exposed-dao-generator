package ch.rethinc.migrations

object Environemnt {
    val dbHost
        get() = System.getenv("DB_HOST")
    val dbPort
        get() = System.getenv("DB_PORT").toInt()
    val dbDatabase
        get() = System.getenv("DB_DATABASE")
    val dbUser
        get() = System.getenv("DB_USER")
    val dbPassword
        get() = System.getenv("DB_PASSWORD")
}