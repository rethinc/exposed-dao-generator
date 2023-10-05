package ch.rethinc.exposed.generator

import com.squareup.kotlinpoet.FileSpec
import org.gradle.api.file.Directory
import org.jetbrains.exposed.gradle.ExposedCodeGenerator
import org.jetbrains.exposed.gradle.ExposedCodeGeneratorConfiguration
import org.jetbrains.exposed.gradle.MetadataGetter

object ExposedCodeGenerator {

    fun generate(
        databaseConfiguration: DatabaseConfiguration,
        packageName: String,
        outputDirectory: Directory,
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


        val tables = metadataGetter.getTables().filterUtilTables()
        val config = ExposedCodeGeneratorConfiguration(
            packageName = packageName,
            generateSingleFile = false,
            generatedFileName = null,
            collate = null,
            columnMappings = emptyMap()
        )
        val exposedCodeGenerator = ExposedCodeGenerator(tables, config)
        val files = exposedCodeGenerator.generateExposedTables().map {
            // When a table name contains quotes because it is a reserved keyword, the generated file name contains quotes
            // In these cases toJavaFileObject() will fail because the file name is not a valid file name
            stripQuotesFromFilename(it)
        }

        files.forEach {
            it.writeTo(outputDirectory.asFile)
            val generatedFile = outputDirectory.file(it.toJavaFileObject().name).asFile
            val generatedContent = generatedFile.readText()
            generatedFile.writeText(ExposedCodeGenerator.postProcessOutput(generatedContent))
        }
    }

    private fun stripQuotesFromFilename(it: FileSpec) =
        it.toBuilder(it.packageName, it.name.replace("\"", "")).build()

    private fun List<schemacrawler.schema.Table>.filterUtilTables() = this.filterNot { it.fullName.startsWith("sys.") }

}