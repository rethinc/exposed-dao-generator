import ch.rethinc.exposed.generator.GenerateDaosTask
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage

plugins {
    id("com.bmuschko.docker-remote-api") version "9.0.1"
    id("ch.rethinc.exposed.generator")
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