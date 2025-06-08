package ${packageName}

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import java.util.*

abstract class GenericDao<Entity : UUIDEntity, Dto>(
    private val mapToDto: (entity: Entity) -> Dto,
    private val getId: (dto: Dto) -> UUID,
    val setValuesFrom: Entity.(dto: Dto) -> Unit,
    private val persistence: Persistence,
    private val entity: UUIDEntityClass<Entity>
) {

    fun all(): List<Dto> = persistence.execute {
        entity.all().toList().map(mapToDto)
    }

    fun getById(id: UUID): Dto? = persistence.execute {
        val entity = entity.findById(id)
        entity?.let {
            mapToDto(it)
        }
    }

    fun create(dto: Dto) = persistence.execute {
        entity.new(id = getId(dto)) {
            this.setValuesFrom(dto)
        }
    }

    fun update(id: UUID, dto: Dto) = persistence.execute {
        entity.findById(id)?.apply {
            this.setValuesFrom(dto)
        }
    }

    fun delete(id: UUID) = persistence.execute {
        entity.findById(id)?.delete()
    }

    fun find(op: SqlExpressionBuilder.() -> Op<Boolean>): List<Dto> = persistence.execute {
        entity.find(op).toList().map(mapToDto)
    }

    fun <T> withPersistence(block: () -> T): T = persistence.execute { block() }
}