# exposed-dao-generator
Generates Data Access Objects from a database with JetBrains Exposed

## Installation

The easiest is to use the `plugins`-closure in your `build.gradle` file:
```gradle
plugins {
  id("ch.rethinc.exposed.generator") version "1.0.0"
}
```

You can also install the plugin by using the traditional Gradle way:

```gradle
buildscript {
  repositories {
    maven {
      url = uri("https://plugins.gradle.org/m2/")
    }
  }
  dependencies {
    classpath("ch.rethinc:exposed-dao-generator:1.0.0")
  }
}

apply(plugin = "ch.rethinc.exposed.generator")
```

## Usage

Have a look at the example implementation in the [example](https://github.com/rethinc/exposed-dao-generator/tree/main/example)-folder.

This plugin expects your migration code to run within a docker image. Here's how invoking the plugin from your `build.gradle` file might look:

```gradle
val generateDaos by tasks.registering(GenerateDaosTask::class) {
    dependsOn(buildMigrationsImage)
    databaseImage.set("postgres:14-alpine")
    migrationImage.set(buildMigrationsImage.get().imageId)
    migrationTimeoutMinutes.set(10)
    outputDirectory.set(File("$buildDir/tables"))
    packageName.set("ch.rethinc.persistence")
}
```