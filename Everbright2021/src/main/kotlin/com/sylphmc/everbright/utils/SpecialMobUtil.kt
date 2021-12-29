package com.sylphmc.everbright.utils

import com.sylphmc.events.core.SylphEvents
import com.sylphmc.everbright.specialmobs.MasterMob
import com.sylphmc.everbright.specialmobs.SpecialMob
import com.sylphmc.everbright.specialmobs.factory.SpecialMobRegistry
import me.racci.raccicore.api.extensions.pdc
import me.racci.raccicore.api.extensions.scheduler
import me.racci.sylph.core.Sylph
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.*
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.projectiles.BlockProjectileSource
import org.bukkit.util.Vector
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.ThreadLocalRandom

val NO_TOUCH = Sylph.namespacedKey("no_touch")
val SPECIAL_MOB = Sylph.namespacedKey("special_mob")
val SPECIAL_TYPE = Sylph.namespacedKey("special_mob_type")
val MOB_EXTENSION = Sylph.namespacedKey("special_mob_extension")
val MOB_MINION = Sylph.namespacedKey("special_mob_minion")
val BASE_UUID = Sylph.namespacedKey("special_mob_base_uuid")

/**
 * Util class for managing SpecialMobs
 */
@Suppress("UNCHECKED_CAST")
object SpecialMobUtil {

    fun spawnParticlesAround(
        entity: Entity,
        particle: Particle,
        amount: Int,
    ) = spawnParticlesAround<Any>(entity.location, particle, amount)

    fun <T> spawnParticlesAround(
        location: Location,
        particle: Particle,
        amount: Int,
        data: T? = null,
    ) {
        val world: World = location.world
        val rand: ThreadLocalRandom = ThreadLocalRandom.current()
        for (i in 0 until amount) {
            if (data != null) {
                world.spawnParticle<T>(
                    particle,
                    location.clone()
                            .add(
                                rand.nextDouble(-3.0, 3.0),
                                rand.nextDouble(0.0, 3.0),
                                rand.nextDouble(-3.0, 3.0)
                            ),
                    1, data
                )
            } else {
                world.spawnParticle(
                    particle,
                    location.clone()
                            .add(
                                rand.nextDouble(-3.0, 3.0),
                                rand.nextDouble(0.0, 3.0),
                                rand.nextDouble(-3.0, 3.0)
                            ),
                    1
                )
            }
        }
    }

    fun <T : Projectile> launchProjectileOnTarget(
        source: Mob,
        target: Entity,
        projectile: Class<T>,
        speed: Double,
    ): T {
        val vel: Vector = getDirectionVector(source.location, target.location)
                .normalize()
                .multiply(speed)
        return source.launchProjectile(projectile, vel)
    }

    fun <T : Entity> spawnAndMount(
        carrier: Entity,
        passengerType: EntityType,
    ): T {
        val passenger = spawnAndTagEntity<T>(carrier.location, passengerType)
        tagExtension(passenger, carrier)
        carrier.addPassenger(passenger)
        return passenger
    }

    fun <T : Entity> spawnAndMount(
        carrierType: EntityType,
        rider: Entity,
    ): T {
        val carrier = spawnAndTagEntity<T>(rider.location, carrierType)
        tagExtension(carrier, rider)
        carrier.addPassenger(rider)
        return carrier
    }

    fun <T : Entity> spawnAndMount(
        carrier: Entity,
        riderClass: Class<out SpecialMob<*>>,
    ): T {
        val f = SpecialMobRegistry.getMobFactoryByName(riderClass.simpleName)!!
        return carrier.world.spawn(carrier.location, f.entityType.entityClass!!) {
            it.pdc.set(NO_TOUCH, PersistentDataType.BYTE, 1.toByte())
            carrier.addPassenger(it)
            tagExtension(it, carrier)
            scheduler { f.wrap(it as LivingEntity) }.runTaskLater(SylphEvents.instance, 1)
        } as T
    }

    private fun <T : Entity> spawnAndTagEntity(
        location: Location,
        entityType: EntityType,
    ) = location.world.spawn(location, entityType.entityClass!!) {
        tagSpecialMob(it)
    } as T

    fun <T : Entity> spawnMinion(
        master: MasterMob<*>,
        minionClass: Class<out SpecialMob<*>>,
        location: Location = master.baseEntity.location,
    ): T {
        val f = SpecialMobRegistry.getMinionFactoryByName(minionClass.simpleName)!!
        return location.world.spawn(location, f.entityType.entityClass!!) {
            it.pdc {
                set(NO_TOUCH, PersistentDataType.BYTE, 1.toByte())
                set(MOB_EXTENSION, PersistentDataType.BYTE, 1.toByte())
                set(BASE_UUID, PersistentDataType.BYTE_ARRAY, getBytesFromUUID(master.baseEntity.uniqueId))
            }
            scheduler {f.wrap(master, it as LivingEntity)}.runTaskLater(SylphEvents.instance, 1)
        } as T
    }

    private fun tagExtension(
        entity: Entity,
        extended: Entity,
    ) {
        entity.pdc {
            set(MOB_EXTENSION, PersistentDataType.BYTE, 1.toByte())
            set(BASE_UUID, PersistentDataType.BYTE_ARRAY, getBytesFromUUID(extended.uniqueId))
        }
    }

    fun isMinion(
        entity: Entity
    ) = entity.pdc.keys.contains(MOB_MINION)

    fun isExtension(
        entity: Entity,
    ) = entity.pdc.keys.contains(MOB_EXTENSION)

    fun getBaseUUID(
        entity: Entity,
    ) = if(isExtension(entity)) {
        getUUIDFromBytes(entity.pdc[BASE_UUID, PersistentDataType.BYTE_ARRAY]!!)
    } else null

    fun setSpecialMobType(
        entity: Entity,
        type: String,
    ) = entity.pdc.set(SPECIAL_TYPE, PersistentDataType.STRING, type)

    fun getSpecialMobType(
        entity: Entity,
    ) = if(isSpecialMob(entity)) {
        entity.pdc.get(SPECIAL_TYPE, PersistentDataType.STRING)
    } else null

    fun tagSpecialMob(
        entity: Entity,
        persistence: Boolean = false,
    ) {
        entity.pdc.set(SPECIAL_MOB, PersistentDataType.BYTE, 1.toByte())
        if(entity is LivingEntity) entity.removeWhenFarAway = !persistence
        entity.isPersistent = false
    }

    fun isSpecialMob(
        entity: Entity,
    ) = entity.pdc.keys.contains(SPECIAL_MOB)

    fun handleExtendedEntityDamage(
        receiver: LivingEntity,
        other: LivingEntity,
        event: EntityDamageEvent,
    ) {
        if(event is EntityDamageByEntityEvent
            && event.damager.uniqueId == other.uniqueId
            || receiver.health == 0.0
        ) return

        val newHealth = 0.0.coerceAtLeast(other.health - event.finalDamage)
        if (newHealth == 0.0) {
            other.damage(event.finalDamage, event.entity)
            return
        }
        other.health = newHealth
        other.playEffect(EntityEffect.HURT)
    }

    fun getProjectileSource(entity: Entity): ProjectileSender {
        // Check if the item is a projectile. If not this is not necessary
        if(entity !is Projectile) return ProjectileSender()
        val source = entity.shooter ?: return ProjectileSender()

        // Projectile source should normally be a entity
        if(source is Entity) {
            return ProjectileSender(source as Entity)
        }
        // in some cases it could also be a block. Eg. dispenser
        if(source is BlockProjectileSource) {
            val damager = source.block
            return ProjectileSender(damager)
        }
        throw IllegalArgumentException(source.javaClass.simpleName + " is either a block nor an entity")
    }

    class ProjectileSender private constructor(
        val entity: Entity?,
        val block: Block?,
    ) {

        /**
         * Create a new projectile send with a entity
         *
         * @param entity entity which is the sender
         */
        constructor(entity: Entity): this(entity, null)

        /**
         * Create a new projectile send with a block
         *
         * @param block block which send the projectile
         */
        constructor(block: Block): this(null, block)

        /**
         * Empty projectile sender
         */
        constructor(): this(null, null)

        /**
         * Checks if a entity is present.
         *
         * @return true if the sender is a entity
         */
        val isEntity get() = entity != null

        /**
         * Check if a block is present.
         *
         * @return true if the sender is a block
         */
        val isBlock get() = block != null

        /**
         * Check if the sender is empty.
         *
         * @return false if [isBlock] and [isEntity] is false. otherwise true
         */
        val isEmpty get() = block == null && entity == null

        /**
         * Get the sender as [EntityType].
         *
         * @return entity type of sender
         * @throws NullPointerException When [isEntity] is false
         */
        @get:Throws(NullPointerException::class)
        val entityType get() = entity!!.type

        /**
         * Get the sender as [Material].
         *
         * @return material of sender
         * @throws NullPointerException When [isEntity] is false
         */
        @get:Throws(NullPointerException::class)
        val blockType get() = block!!.type

    }

    private fun getEntitiesAround(
        location: Location,
        range: Double,
    ) = location.world?.getNearbyEntities(
        location,
        range,
        range,
        range
    ).orEmpty()

    private fun getDirectionVector(
        start: Location,
        target: Location,
    ): Vector {
        val s = start.toVector()
        val t = target.toVector()
        return Vector(t.x - s.x, t.y - s.y, t.z - s.z)
    }

    private fun getBytesFromUUID(uuid: UUID): ByteArray {
        val bb = ByteBuffer.wrap(ByteArray(16))
        bb.putLong(uuid.mostSignificantBits)
        bb.putLong(uuid.leastSignificantBits)
        return bb.array()
    }

    private fun getUUIDFromBytes(bytes: ByteArray): UUID {
        val byteBuffer = ByteBuffer.wrap(bytes)
        val high = byteBuffer.long
        val low = byteBuffer.long
        return UUID(high, low)
    }


}