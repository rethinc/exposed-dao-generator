package ch.rethinc.exposed.generator

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class GenerateDaosTask : DefaultTask() {

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:Input
    abstract val packageName: Property<String>

    @get:Input
    abstract val databaseImage: Property<String>

    @get:Input
    abstract val migrationImage: Property<String>

    @get:Input
    abstract val migrationTimeoutMinutes: Property<Long>

    @get:Input
    abstract val protectedTables: ListProperty<String>

    @get:Input
    abstract val ignoredColumns: ListProperty<IgnoredColumn>

    @TaskAction
    fun generateDaos() {
        val databaseConfiguration = DatabaseConfiguration(
            databaseImage = databaseImage.get(),
            migrationImage = migrationImage.get(),
            migrationTimeoutMinutes = migrationTimeoutMinutes.get(),
            databaseHost = "localhost",
            databaseDriver = "postgresql",
            databasePort = 5432,
            databaseName = "postgres",
            databaseUser = "postgres",
            databasePassword = "postgres"
        )
        DatabaseMigration().use {
            it.startMigratedDatabase(databaseConfiguration)
            ExposedCodeGenerator.generate(
                databaseConfiguration = databaseConfiguration,
                packageName = packageName.get(),
                outputDirectory = outputDirectory.get()
            )
            val packageDirectory = packageName.get().replace(".", "/")
            val generatedFilesDirectory = File("${outputDirectory.get().asFile.path}/$packageDirectory")
            postProcessTableFiles(generatedFilesDirectory)
            removeIgnoredColumns(generatedFilesDirectory, ignoredColumns.getOrElse(emptyList()))
            InfrastructureGenerator(InfrastructureTemplates).generateFiles(packageName.get(), generatedFilesDirectory)
            DaoGenerator(TableParser, DaoTemplates, protectedTables.getOrElse(emptyList())).generateFiles(generatedFilesDirectory)
        }
    }
}
