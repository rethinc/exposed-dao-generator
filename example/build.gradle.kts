import ch.rethinc.exposed.generator.GenerateDaosTask
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    mavenCentral()
}


buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-compiler:1.7.20")
    }
}


plugins {
    kotlin("jvm") version "1.7.20"
    id("com.bmuschko.docker-remote-api") version "9.0.1"
    id("ch.rethinc.exposed.generator")
}

val exposedVersion = "0.40.1"
dependencies {
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.postgresql:postgresql:42.5.1")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
}


tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

sourceSets.main {
    java.srcDirs(File("$buildDir/tables"))
}

val buildMigrationsImage by tasks.registering(DockerBuildImage::class) {
    inputDir.set(File("${rootDir.path}/migrations"))
    dockerFile.set(File("$rootDir/migrations/Dockerfile"))
    images.add("example-migrations")
    doLast {
        println("build image")
    }
}


val generateDaos by tasks.registering(GenerateDaosTask::class) {
    dependsOn(buildMigrationsImage)
    databaseImage.set("postgres:14-alpine")
    migrationImage.set(buildMigrationsImage.get().imageId)
    migrationTimeoutMinutes.set(10)
    outputDirectory.set(File("$buildDir/tables"))
    packageName.set("ch.rethinc.persistence")
}

tasks.compileKotlin.get().dependsOn(generateDaos)