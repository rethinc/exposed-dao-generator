package ch.rethinc.exposed.generator

import java.io.File

data class InfrastructureTemplateModel(
    val packageName: String
)

object InfrastructureTemplates {
    val templates = listOf(
        InfrastructureTemplate(name = "GenericDao"),
        InfrastructureTemplate(name = "Persistence"),
        InfrastructureTemplate(name = "AuthenticatedGenericDao"),
    )

    fun generateFiles(model: InfrastructureTemplateModel, path: String) {
        templates.forEach { template ->
            template.writeTo(model, File(path, "${template.name}.kt"))
        }
    }

    fun isGeneratedFile(path: String): Boolean {
        return templates.any { template ->
            path.endsWith("${template.name}.kt")
        }
    }
}