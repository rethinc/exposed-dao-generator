package ch.rethinc.exposed.generator

import java.io.File

class DaoGenerator(
    private val tableParser: TableParser,
    private val templates: DaoTemplates
) {
    fun generateFiles(directory: File) {
        if (!directory.isDirectory) {
            throw IllegalArgumentException("${directory.path} is not a directory.")
        }
        val files = directory.listFiles() ?: throw IllegalArgumentException("Could not list files of ${directory.path}")
        files.filter {
            it.isKotlinFile()
        }.filter { !templates.isGeneratedFile(it.path) && !InfrastructureTemplates.isGeneratedFile(it.path) }
            .forEach { tableFile ->
                val table = tableParser.parse(tableFile)
                templates.generateFiles(table, tableFile.path)
            }
    }

    private fun File.isKotlinFile(): Boolean =
        this.extension == "kt"
}