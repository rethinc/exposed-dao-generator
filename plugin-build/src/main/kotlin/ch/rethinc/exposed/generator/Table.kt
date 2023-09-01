package ch.rethinc.exposed.generator

data class Table(
    val packageName: String,
    val name: String,
    val properties: List<TableProperty>
)