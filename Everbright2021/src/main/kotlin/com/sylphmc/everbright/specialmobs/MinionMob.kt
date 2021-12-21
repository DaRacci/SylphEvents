package com.sylphmc.everbright.specialmobs

import me.racci.raccicore.api.extensions.cancel
import org.bukkit.entity.Mob
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityTargetEvent

abstract class MinionMob<T: Mob>(
    val master: MasterMob<*>,
    minion: T
): SpecialMob<T>(minion) {

    override fun onTargetEvent(event: EntityTargetEvent) {
        if(event.target == master.baseEntity.target) return
        event.cancel()
    }

    override fun onDeath(event: EntityDeathEvent) {
        master.onMinionDeath(event)
    }

    override val isValid: Boolean
        get() = super.isValid && master.isValid


}