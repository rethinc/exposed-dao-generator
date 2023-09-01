plugins {
    kotlin("jvm") version "1.7.20"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.flywaydb:flyway-core:9.8.3")
    implementation("org.postgresql:postgresql:42.5.1")
}

application {
    mainClass.set("MainKt")
}

tasks.jar {
    archiveFileName.set("migrations.jar")
    manifest {
        attributes["Main-Class"] = "ch.rethinc.migrations.MainKt"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}
