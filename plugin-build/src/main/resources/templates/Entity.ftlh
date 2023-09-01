package ${packageName}

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class ${name}Entity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<${name}Entity>(${name})

  <#list properties as property>
    var ${property.name} by ${name}.${property.name}
  </#list>
}