package ch.rethinc.exposed.generator

import java.io.File

object DaoTemplates {
    val templates = listOf(
        Template(name = "Entity"),
        Template(name = "DtoMapper"),
        Template(name = "Dto"),
        Template(name = "Dao"),
    )

    val protectedTemplates = listOf(
        Template(name = "Entity"),
        Template(name = "DtoMapper"),
        Template(name = "Dto"),
        Template(name = "AuthenticatedDao", generatedFileName = "Dao"),
    )

    fun generateFiles(table: Table, tableFilePath: String) {
        templates.forEach { template ->
            template.writeTo(table, File(template.addSuffixToPath(tableFilePath)))
        }
    }

    fun generateProtectedFiles(table: Table, tableFilePath: String) {
        protectedTemplates.forEach { template ->
            template.writeTo(table, File(template.addSuffixToPath(tableFilePath)))
        }
    }

    fun isGeneratedFile(path: String): Boolean {
        return templates.any { template ->
            template.hasSameSuffixAs(path)
        } || protectedTemplates.any { template ->
            template.hasSameSuffixAs(path)
        }
    }
}