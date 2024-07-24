package ch.rethinc.exposed.generator

import com.intellij.openapi.util.Disposer
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.asJava.namedUnwrappedElement
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinFileType
import java.io.File
import java.util.UUID

object TableParser {

    fun parse(file: File): Table {
        try {
            val environment = KotlinCoreEnvironment.createForProduction(
                Disposer.newDisposable(),
                CompilerConfiguration(),
                EnvironmentConfigFiles.JVM_CONFIG_FILES
            )
            val content = file.readText()
            val kvFile = createKtFile(environment, content, "temp-${UUID.randomUUID()}.kt")

            val packageDirective = kvFile.children.find { it.node.elementType === KtNodeTypes.PACKAGE_DIRECTIVE }

            val objectDeclaration =
                kvFile.children.find { it.node.elementType == KtNodeTypes.OBJECT_DECLARATION }
                    ?: throw IllegalStateException("No object declaration")
            val objectName =
                objectDeclaration.namedUnwrappedElement?.name ?: throw IllegalStateException("Object has no name")
            val classBody =
                objectDeclaration.children.find { it.node.elementType == KtNodeTypes.CLASS_BODY }
            val properties = classBody?.children?.filter { it.node.elementType == KtNodeTypes.PROPERTY } ?: emptyList()

            return Table(
                packageName = packageDirective?.lastChild?.text ?: "ch.rethinc.persistence",
                name = objectName,
                properties = properties.mapNotNull {
                    val name = it.namedUnwrappedElement?.name ?: return@mapNotNull null
                    val type = getPropertyType(it) ?: return@mapNotNull null
                    return@mapNotNull TableProperty(name, type)
                })
        } catch (t: Throwable) {
            throw Exception("Error while parsing file ${file.absolutePath}", t)
        }
    }

    private fun getPropertyType(property: PsiElement): String? =
        findTypeArgumentList(property)?.children?.firstOrNull()?.node?.text

    private fun findTypeArgumentList(psiElement: PsiElement): PsiElement? {
        if (psiElement.node.elementType == KtNodeTypes.TYPE_ARGUMENT_LIST) {
            return psiElement
        }
        for (child in psiElement.children) {
            val argumentListElement = findTypeArgumentList(child)
            if (argumentListElement != null) {
                return argumentListElement
            }
        }
        return null
    }

    private fun createKtFile(environment: KotlinCoreEnvironment, codeString: String, fileName: String) =
        PsiManager.getInstance(environment.project)
            .findFile(
                LightVirtualFile(fileName, KotlinFileType.INSTANCE, codeString)
            ) as org.jetbrains.kotlin.psi.KtFile
}