package ${packageName}

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ISqlExpressionBuilder
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.statements.BatchInsertStatement
import java.util.*

abstract class GenericDao<Entity : UUIDEntity, Table: UUIDTable, Dto>(
    private val mapToDto: (entity: Entity) -> Dto,
    private val getId: (dto: Dto) -> UUID,
    private val setValuesFrom: Entity.(dto: Dto) -> Unit,
    private val setBatchInsertValuesFrom: BatchInsertStatement.(dto: Dto) -> Unit,
    private val persistence: Persistence,
    private val entity: UUIDEntityClass<Entity>,
    private val table: Table
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

    fun batchCreate(dtos: List<Dto>) = persistence.execute {
        table.batchInsert(dtos, shouldReturnGeneratedValues = false) {
            this.setBatchInsertValuesFrom(it)
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

    fun delete(op: Table.(ISqlExpressionBuilder) -> Op<Boolean>) = persistence.execute {
        table.deleteWhere(op = op)
    }

    fun find(op: SqlExpressionBuilder.() -> Op<Boolean>): List<Dto> = persistence.execute {
        entity.find(op).toList().map(mapToDto)
    }

    fun <T> withPersistence(block: () -> T): T = persistence.execute { block() }
}