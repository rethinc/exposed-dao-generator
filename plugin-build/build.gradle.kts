import org.jetbrains.kotlin.fir.declarations.builder.buildScript

plugins {
    kotlin("jvm") version "1.9.0"
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.2.1"
}

group = "ch.rethinc"
version = "1.2.0"

repositories {
    mavenCentral()
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
}

dependencies {
    implementation(gradleApi())
    implementation("org.freemarker:freemarker:2.3.31")
    implementation("org.jetbrains.kotlin:kotlin-compiler:1.7.20")
    implementation("com.github.docker-java:docker-java-core:3.3.3")
    implementation("com.github.docker-java:docker-java-transport-httpclient5:3.3.3")
    implementation("gradle.plugin.com.jetbrains.exposed.gradle:plugin:0.2.1")
    implementation("us.fatehi:schemacrawler:16.15.7")
    implementation("com.squareup:kotlinpoet:1.10.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

gradlePlugin {
    plugins {
        create("exposedDaoGenerator") {
            id = "ch.rethinc.exposed.generator"
            implementationClass = "ch.rethinc.exposed.generator.ExposedDaoGeneratorPlugin"
            website = "https://github.com/rethinc/exposed-dao-generator"
            vcsUrl = "https://github.com/rethinc/exposed-dao-generator"
            displayName = "Exposed DAO Generator"
            description = "Generates Data Access Objects from a database with JetBrains Exposed"
            tags = listOf("exposed", "dao", "generator", "persistence")
        }
    }
}
