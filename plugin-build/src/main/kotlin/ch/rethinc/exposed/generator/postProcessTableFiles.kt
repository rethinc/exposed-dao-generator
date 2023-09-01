package ch.rethinc.exposed.generator

import java.io.File

fun postProcessTableFiles(directory: File) {
    removeFlyWayTable(directory)
    replaceInFiles(
        directory = directory,
        old = "org.jetbrains.exposed.sql.`java-time`",
        new = "org.jetbrains.exposed.sql.javatime"
    )
}

private fun removeFlyWayTable(directory: File) {
    val f = File("${directory.path}/Public.flywaySchemaHistory.kt")
    if (f.exists()) {
        f.delete()
    }
}

private fun replaceInFiles(directory: File, old: String, new: String) {
    if (!directory.isDirectory) {
        return
    }

    directory.listFiles()
        ?.filter { it.isFile }
        ?.forEach { file ->
            file.writeText(file.readText().replace(old, new))
        }
}