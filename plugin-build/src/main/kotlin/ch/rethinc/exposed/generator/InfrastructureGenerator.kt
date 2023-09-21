package ch.rethinc.exposed.generator

import java.io.File

class InfrastructureGenerator(
    private val templates: InfrastructureTemplates
) {
    fun generateFiles(packageName: String, directory: File) {
        if (!directory.isDirectory) {
            throw IllegalArgumentException("${directory.path} is not a directory.")
        }
        val model = InfrastructureTemplateModel(packageName = packageName)
        templates.generateFiles(model, directory.path)
    }
}