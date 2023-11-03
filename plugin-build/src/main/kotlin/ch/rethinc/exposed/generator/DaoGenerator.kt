package ch.rethinc.exposed.generator

import java.io.File

class DaoGenerator(
    private val tableParser: TableParser,
    private val templates: DaoTemplates,
    private val protectedTables: List<String>
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
                if(protectedTables.containsIgnoreCase(table.name)
                ) {
                    templates.generateProtectedFiles(table, tableFile.path)
                }
                else {
                    templates.generateFiles(table, tableFile.path)
                }
            }
    }

    private fun File.isKotlinFile(): Boolean =
        this.extension == "kt"

    private fun List<String>.containsIgnoreCase(s: String, ignoreCase: Boolean = false): Boolean {
        return any { it.equals(s, ignoreCase) }
    }
}