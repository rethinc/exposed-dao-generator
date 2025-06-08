package ${packageName}

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import java.util.*

abstract class AuthenticatedGenericDao<Entity : UUIDEntity, Dto>(
    private val mapToDto: (entity: Entity) -> Dto,
    private val getId: (dto: Dto) -> UUID,
    val setValuesFrom: Entity.(dto: Dto) -> Unit,
    private val persistence: Persistence,
    private val entity: UUIDEntityClass<Entity>
) {

    fun all(authenticatedUserId: UUID): List<Dto> = persistence.execute(authenticatedUserId) {
        entity.all().toList().map(mapToDto)
    }

    fun getById(id: UUID, authenticatedUserId: UUID): Dto? = persistence.execute(authenticatedUserId) {
        val entity = entity.findById(id)
        entity?.let {
            mapToDto(it)
        }
    }

    fun create(dto: Dto, authenticatedUserId: UUID) = persistence.execute(authenticatedUserId) {
        entity.new(id = getId(dto)) {
            this.setValuesFrom(dto)
        }
    }

    fun update(id: UUID, dto: Dto, authenticatedUserId: UUID) = persistence.execute(authenticatedUserId) {
        entity.findById(id)?.apply {
            this.setValuesFrom(dto)
        }
    }

    fun delete(id: UUID, authenticatedUserId: UUID) = persistence.execute(authenticatedUserId) {
        entity.findById(id)?.delete()
    }

    fun find(authenticatedUserId: UUID, op: SqlExpressionBuilder.() -> Op<Boolean>): List<Dto> = persistence.execute(authenticatedUserId) {
        entity.find(op).toList().map(mapToDto)
    }

    fun <T> withPersistence(authenticatedUserId: UUID, block: () -> T): T = persistence.execute(authenticatedUserId) { block() }
}