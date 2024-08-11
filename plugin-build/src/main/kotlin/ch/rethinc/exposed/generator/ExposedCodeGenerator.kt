package ch.rethinc.exposed.generator

import org.gradle.api.file.Directory
import org.jetbrains.exposed.gradle.ExposedCodeGenerator
import org.jetbrains.exposed.gradle.ExposedCodeGeneratorConfiguration
import org.jetbrains.exposed.gradle.MetadataGetter

object ExposedCodeGenerator {

    fun generate(
        databaseConfiguration: DatabaseConfiguration,
        packageName: String,
        outputDirectory: Directory,
        ignoredTables: List<String>
    ) {
        val metadataGetter =
            MetadataGetter(
                databaseDriver = databaseConfiguration.databaseDriver,
                databaseName = databaseConfiguration.databaseName,
                user = databaseConfiguration.databaseUser,
                password = databaseConfiguration.databasePassword,
                host = databaseConfiguration.databaseHost,
                port = databaseConfiguration.databasePort.toString(),
                ipv6Host = null,
                additionalProperties = null,
            )


        val tables = metadataGetter.getTables().filter {
            it.name !in ignoredTables
        }.filterUtilTables()
            val config = ExposedCodeGeneratorConfiguration(
                packageName = packageName,
                generateSingleFile = false,
                generatedFileName = null,
                collate = null,
                columnMappings = emptyMap(),
            )
        val exposedCodeGenerator = ExposedCodeGenerator(tables, config)
        val files = exposedCodeGenerator.generateExposedTables()

        files.forEach {
            it.writeTo(outputDirectory.asFile)
            val generatedFile = outputDirectory.file(it.toJavaFileObject().name).asFile
            val generatedContent = generatedFile.readText()
            generatedFile.writeText(ExposedCodeGenerator.postProcessOutput(generatedContent))
        }
    }

    private fun List<schemacrawler.schema.Table>.filterUtilTables() = this.filterNot { it.fullName.startsWith("sys.") }

}