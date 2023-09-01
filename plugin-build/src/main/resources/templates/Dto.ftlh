package ${packageName}

import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.String
import kotlin.Boolean
import kotlin.Int
import java.util.UUID

data class ${name}Dto(
    val id: UUID,
<#list properties as property>
    val ${property.name}: ${property.type},
</#list>
)