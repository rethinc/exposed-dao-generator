package ch.rethinc.exposed.generator

import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class InfrastructureTemplate(val name: String) {
    fun writeTo(model: Any, directory: File) {
        val fs = OutputStreamWriter(FileOutputStream(directory))
        val template = FreeMarkerConfiguration.fromClassPath.getTemplate("$name.ftlh")
        template.process(model, fs)
    }
}