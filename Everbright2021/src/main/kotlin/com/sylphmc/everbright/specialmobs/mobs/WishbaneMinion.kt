package com.sylphmc.everbright.specialmobs.mobs

import com.sylphmc.everbright.specialmobs.MasterMob
import com.sylphmc.everbright.specialmobs.MinionMob
import org.bukkit.entity.Vex
import org.bukkit.event.entity.ProjectileHitEvent

class WishbaneMinion(
    entity: Vex,
    master: MasterMob<*>
): MinionMob<Vex>(master, entity) {

    override fun onProjectileHit(event: ProjectileHitEvent) {
        if(event.hitEntity != null
            && !event.hitEntity!!.isDead
        ) {
            event.hitEntity!!.freezeTicks += 100
        }
    }

}