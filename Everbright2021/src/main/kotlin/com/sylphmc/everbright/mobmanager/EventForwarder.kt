package com.sylphmc.everbright.mobmanager

import com.sylphmc.everbright.mobmanager.MobManager.Companion.getWorldMobs
import com.sylphmc.everbright.utils.SpecialMobUtil
import me.racci.raccicore.api.extensions.KotlinListener
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.*

class EventForwarder: KotlinListener {

    @EventHandler
    fun onEntityTeleport(event: EntityTeleportEvent) {
        getWorldMobs(event.entity.world).invokeIfPresent(event.entity) { onTeleport(event) }
    }

    @EventHandler
    fun onProjectileShoot(event: ProjectileLaunchEvent) {
        val projectileSource = SpecialMobUtil.getProjectileSource(event.entity)
        if (projectileSource.isEntity) {
            getWorldMobs(event.entity.world).invokeIfPresent(projectileSource.entity!!) { onProjectileShoot(event) }
        }
    }

    @EventHandler
    fun onProjectileHit(event: ProjectileHitEvent) {
        val projectileSource = SpecialMobUtil.getProjectileSource(event.entity)
        if (projectileSource.isEntity) {
            getWorldMobs(event.entity.world).invokeIfPresent(projectileSource.entity!!) { onProjectileHit(event) }
        }
    }

    @EventHandler
    fun onDeath(event: EntityDeathEvent) {
        if (SpecialMobUtil.isSpecialMob(event.entity)) {
            if (SpecialMobUtil.isExtension(event.entity)) {
                val baseUUID = SpecialMobUtil.getBaseUUID(event.entity)
                getWorldMobs(event.entity.world).invokeIfPresent(baseUUID!!) { onExtensionDeath(event) }
            } else {
                getWorldMobs(event.entity.world).invokeIfPresent(event.entity) { onDeath(event) }
            }
        }
    }

    @EventHandler
    fun onKill(event: EntityDeathEvent) {
        getWorldMobs(event.entity.world).invokeIfPresent(event.entity) { onKill(event) }
    }

    @EventHandler
    fun onExplosionPrimeEvent(event: ExplosionPrimeEvent) {
        getWorldMobs(event.entity.world).invokeIfPresent(event.entity) { onExplosionPrimeEvent(event) }
    }

    @EventHandler
    fun onExplosionEvent(event: EntityExplodeEvent) {
        getWorldMobs(event.entity.world).invokeIfPresent(event.entity) { onExplosionEvent(event) }
    }

    @EventHandler
    fun onTargetEvent(event: EntityTargetEvent) {

        if (event.target != null
            && SpecialMobUtil.isSpecialMob(event.target!!)
        ) {
            event.isCancelled = true; return
        }

        if (!SpecialMobUtil.isSpecialMob(event.entity)) return

        if (event.target != null
            && event.target!!.type != EntityType.PLAYER
        ) {
            event.isCancelled = true; return
        }

        if (event.target == null || event.target is LivingEntity) {
            getWorldMobs(event.entity.world).invokeIfPresent(event.entity) { onTargetEvent(event) }
        }
    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        if (!SpecialMobUtil.isSpecialMob(event.entity)) return
        if (SpecialMobUtil.isExtension(event.entity)) {
            val baseUUID = SpecialMobUtil.getBaseUUID(event.entity)
            getWorldMobs(event.entity.world).invokeIfPresent(baseUUID!!) { onExtensionDamage(event) }
        } else {
            getWorldMobs(event.entity.world).invokeIfPresent(event.entity) { onDamage(event) }
        }
    }

    @EventHandler
    fun onDamageByEntity(event: EntityDamageByEntityEvent) {
        if (!SpecialMobUtil.isSpecialMob(event.entity)) return
        if (SpecialMobUtil.isExtension(event.entity)) {
            val baseUUID = SpecialMobUtil.getBaseUUID(event.entity)
            getWorldMobs(event.entity.world).invokeIfPresent(baseUUID!!) {onDamageByEntity(event)}
        } else {
            getWorldMobs(event.entity.world).invokeIfPresent(event.entity) {onDamageByEntity(event)}
        }
    }

    @EventHandler
    fun onHit(event: EntityDamageByEntityEvent) {
        if(!SpecialMobUtil.isSpecialMob(event.damager)) return
        getWorldMobs(event.damager.world).invokeIfPresent(event.damager) {onHit(event)}
    }

}