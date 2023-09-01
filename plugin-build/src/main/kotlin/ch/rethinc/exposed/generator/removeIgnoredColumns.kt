package ch.rethinc.exposed.generator

import java.io.File

data class IgnoredColumn(val table: String, val column: String)

fun removeIgnoredColumns(directory: File, ignoredColumns: List<IgnoredColumn>) {
    ignoredColumns.forEach { ignoredColumn ->
        val file = "${directory.path}/Public.${ignoredColumn.table}.kt"
        removeIgnoredColumnsInFile(file, ignoredColumn)
    }
}

private fun removeIgnoredColumnsInFile(file: String, ignoredColumn: IgnoredColumn) {
    val lines = File(file).useLines {
        it.toList()
    }

    val linesWithoutIgnoredColumn = lines.filter { !it.contains("\"${ignoredColumn.column}\"") }
    File(file).delete()
    File(file).writeText(linesWithoutIgnoredColumn.joinToString("\n"))
}