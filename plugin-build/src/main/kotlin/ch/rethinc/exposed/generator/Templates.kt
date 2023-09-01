package ch.rethinc.exposed.generator

import java.io.File

object Templates {
    val templates = listOf(
        Template(name = "Entity"),
        Template(name = "DtoMapper"),
        Template(name = "Dto"),
        Template(name = "Dao"),
    )

    fun generateFiles(table: Table, tableFilePath: String) {
        templates.forEach { template ->
            template.writeTo(table, File(template.addSuffixToPath(tableFilePath)))
        }
    }

    fun isGeneratedFile(path: String): Boolean {
        return templates.any { template ->
            template.hasSameSuffixAs(path)
        }
    }
}