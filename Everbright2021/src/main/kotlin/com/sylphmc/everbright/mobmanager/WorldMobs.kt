package com.sylphmc.everbright.mobmanager

import com.sylphmc.everbright.specialmobs.SpecialMob
import org.bukkit.entity.Entity
import java.util.*

class WorldMobs {

    val mobs = HashMap<UUID, SpecialMob<*>>()
    private val tickQueue = ArrayDeque<SpecialMob<*>>()
    private var entityTick = 0.0

    inline fun invokeIfPresent(
        entity: Entity,
        block: SpecialMob<*>.() -> Unit
    ) = invokeIfPresent(entity.uniqueId, block)

    inline fun invokeIfPresent(
        uuid: UUID,
        block: SpecialMob<*>.() -> Unit
    ) {
        val specialMob = mobs[uuid]
        if (specialMob != null) {
            block.invoke(specialMob)
        }
    }

    fun invokeAll(
        block: SpecialMob<*>.() -> Unit
    ) = mobs.values.forEach(block)

    fun tick(tickDelay: Int) {
        if(tickQueue.isEmpty()) return
        entityTick += tickQueue.size / tickDelay.toDouble()
        while (entityTick > 0) {
            if(tickQueue.isEmpty()) return
            val poll = tickQueue.poll()
            if (!poll.baseEntity.isValid) {
                remove(poll.baseEntity.uniqueId)
                poll.remove()
            } else {
                poll.tick()
                tickQueue.add(poll)
            }
            entityTick--
        }
    }

    val isEmpty: Boolean
        get() = mobs.isEmpty()

    fun put(key: UUID, value: SpecialMob<*>) {
        mobs[key] = value
        tickQueue.add(value)
    }

    /**
     * Attempts to remove an entity from world mobs and the world.
     *
     * @param key uid of entity
     * @return special mob if present.
     */
    fun remove(key: UUID): SpecialMob<*>? {
        if (!mobs.containsKey(key)) return null
        val removed = mobs.remove(key)
        tickQueue.remove(removed)
        removed?.remove()
        return removed
    }

    fun clear() {
        mobs.clear()
        tickQueue.clear()
    }
}