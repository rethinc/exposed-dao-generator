package ${packageName}

import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.to${name}(): ${name}Dto =
    ${name}Dto(
        id = this.get(${name}.id).value,
<#list properties as property>
        ${property.name} = this.get(${name}.${property.name}),
</#list>
    )

fun ResultRow.to${name}OrNull(): ${name}Dto? {
    if (this.getOrNull(${name}.id) == null) {
        return null
    }
    return this.to${name}()
}