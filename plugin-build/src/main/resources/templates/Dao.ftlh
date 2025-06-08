package ${packageName}

class ${name}EntityDao(persistence: Persistence) : GenericDao<${name}Entity, ${name}, ${name}Dto>(
mapToDto = {
    ${name}Dto(
        id = it.id.value,
    <#list properties as property>
        ${property.name} = it.${property.name},
    </#list>
    )
},
setValuesFrom = {
<#list properties as property>
    this.${property.name} = it.${property.name}
</#list>
},
setBatchInsertValuesFrom = {
<#list properties as property>
    this[${name}.${property.name}] = it.${property.name}
</#list>
},
persistence = persistence,
getId = { it.id },
entity = ${name}Entity,
table = ${name},
)