package com.sylphmc.everbright.specialmobs.mobs

import com.sylphmc.everbright.specialmobs.MasterMob
import com.sylphmc.everbright.utils.SpecialMobUtil
import org.bukkit.Particle
import org.bukkit.entity.Vex
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent

class AncientWishbaneMinion(
    entity: Vex,
    master: MasterMob<*>
): WishbaneMinion(entity, master) {

    override fun onHit(event: EntityDamageByEntityEvent) {
        event.entity.location.createExplosion(baseEntity, 2f, true, false)
    }

    override fun onDeath(event: EntityDeathEvent) {
        event.entity.location.createExplosion(baseEntity, 4f, true, false)
    }

    override suspend fun afterSpawn() {
        SpecialMobUtil.spawnParticlesAround(baseEntity, Particle.CRIMSON_SPORE, 25)
    }

}
