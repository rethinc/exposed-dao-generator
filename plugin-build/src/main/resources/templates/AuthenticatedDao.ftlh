package ${packageName}

class ${name}EntityDao(persistence: AuthenticatedPersistence) : AuthenticatedGenericDao<${name}Entity, ${name}Dto>(
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
persistence = persistence,
getId = { it.id },
entity = ${name}Entity,
)