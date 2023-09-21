package ch.rethinc.exposed.generator

import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class Template(val name: String) {
    fun addSuffixToPath(filePath: String): String {
        return filePath.replace(".kt", "$name.kt")
    }

    fun hasSameSuffixAs(filePath: String): Boolean {
        return filePath.endsWith("$name.kt")
    }

    fun writeTo(model: Any, file: File) {
        val fs = OutputStreamWriter(FileOutputStream(file))
        val template = FreeMarkerConfiguration.fromClassPath.getTemplate("$name.ftlh")
        template.process(model, fs)
    }
}