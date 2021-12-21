package com.sylphmc.everbright.specialmobs

import com.sylphmc.events.core.SylphEvents
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityTargetEvent

abstract class MasterMob<T: Mob>(
    entity: T
): SpecialMob<T>(entity) {

    val minions = ArrayList<Mob>()

    /**
     * Removes all minions when the mob dies
     */
    override fun onDeath(event: EntityDeathEvent) {
        minions.removeIf{it.remove();true}
    }

    /**
     * Synchronises the target with all minions
     */
    override fun onTargetEvent(event: EntityTargetEvent) {
        for(minion in minions) {
            // Don't set the target if its already set, duh
            if(minion.target == event.target) continue
            minion.target = event.target as? LivingEntity
        }
    }

    override fun remove() {
        SylphEvents.launch {
            minions.removeIf {it.remove();true}
            super.remove()
        }
    }

    open fun onMinionDeath(event: EntityDeathEvent) {
        minions.remove(event.entity)
    }

}