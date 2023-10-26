package ${packageName}

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import java.util.*

abstract class AuthenticatedGenericDao<Entity : UUIDEntity, Dto>(
    private val mapToDto: (entity: Entity) -> Dto,
    private val getId: (dto: Dto) -> UUID,
    private val setValuesFrom: Entity.(dto: Dto) -> Unit,
    private val persistence: AuthenticatedPersistence,
    private val entity: UUIDEntityClass<Entity>
) {

    fun all(authenticatedUserId: String): List<Dto> = persistence.execute(authenticatedUserId) {
        entity.all().toList().map(mapToDto)
    }

    fun getById(authenticatedUserId: String, id: UUID): Dto? = persistence.execute(authenticatedUserId) {
        val entity = entity.findById(id)
        entity?.let {
            mapToDto(it)
        }
    }

    fun create(authenticatedUserId: String, dto: Dto) = persistence.execute(authenticatedUserId) {
        entity.new(id = getId(dto)) {
            this.setValuesFrom(dto)
        }
    }

    fun update(authenticatedUserId: String, id: UUID, dto: Dto) = persistence.execute(authenticatedUserId) {
        entity.findById(id)?.apply {
            this.setValuesFrom(dto)
        }
    }

    fun delete(authenticatedUserId: String, id: UUID) = persistence.execute(authenticatedUserId) {
        entity.findById(id)?.delete()
    }

    fun find(authenticatedUserId: String, op: SqlExpressionBuilder.() -> Op<Boolean>): List<Dto> = persistence.execute(authenticatedUserId) {
        entity.find(op).toList().map(mapToDto)
    }

    fun <T> withPersistence(authenticatedUserId: String, block: () -> T): T = persistence.execute(authenticatedUserId) { block() }
}