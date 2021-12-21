package com.sylphmc.everbright.mobmanager

import com.sylphmc.events.core.SylphEvents
import com.sylphmc.everbright.specialmobs.SpecialMob
import com.sylphmc.everbright.specialmobs.factory.MobFactory
import com.sylphmc.everbright.specialmobs.mobs.Wishbane
import com.sylphmc.everbright.utils.NO_TOUCH
import com.sylphmc.everbright.utils.SpecialMobUtil
import com.sylphmc.everbright.utils.uuidBossBarNamespace
import me.racci.raccicore.api.extensions.*
import me.racci.raccicore.api.lifecycle.LifecycleListener
import me.racci.sylph.api.utils.SPECIAL
import me.racci.sylph.core.data.Lang
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityTameEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.persistence.PersistentDataType
import java.util.*
import java.util.concurrent.ThreadLocalRandom

class MobManager(
    override val plugin: SylphEvents
): KotlinListener, LifecycleListener<SylphEvents> {

    override suspend fun onDisable() {
        for(world in server.worlds) {
            for((_, mob) in getWorldMobs(world).mobs) {
                Bukkit.getBossBar(uuidBossBarNamespace(mob.baseEntity))?.also{Bukkit.removeBossBar(it.key)}
                mob.remove()
            }
        }
    }

    private val random = ThreadLocalRandom.current()

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onMobSpawn(event: CreatureSpawnEvent) {

        if(event.spawnReason == CreatureSpawnEvent.SpawnReason.SPAWNER) {
            event.entity.persistentDataContainer[NO_TOUCH, PersistentDataType.BYTE] = 1.toByte()
            return
        }

        if(event.entity.persistentDataContainer.has(NO_TOUCH, PersistentDataType.BYTE)
            || event.location.block.temperature > 0
            || event.location.y < 60
        ) return

        if(random.nextInt(101) > 3) return

        val factory = MobFactory[Wishbane::class.java] ?: return

        val mob = if(factory.entityType != event.entity.type) {
            val loc = event.entity.location
            event.entity.remove()
            loc.world.spawn(loc, factory.entityType.entityClass!!) {
                it.persistentDataContainer[NO_TOUCH, PersistentDataType.BYTE] = 1.toByte()
            } as LivingEntity
        } else event.entity

        for(player in mob.getNearbyEntities(100.0, 75.0, 100.0)) {
            if(player !is Player) continue
            player.msg(Lang["prefix.prefix"].append(" <aqua>A Wishbane has appeared nearby, be quick to find and defeat the beast!".parse()).decoration(TextDecoration.BOLD, false))
            player.playSound(Sound.sound(Key.key("entity.ender_dragon.growl"), Sound.Source.HOSTILE, 1f, 1f), Sound.Emitter.self())
        }

        scheduler {
            wrapMob(mob, factory)
        }.runTaskLater(plugin, 1)

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEntityKill(event: EntityDeathEvent) {
        val entity = event.entity
        val player = event.entity.killer

        if (entity !is Monster
            || entity is Boss
            || entity.pdc.keys.contains(SPECIAL)
        ) return

        if(player == null) {
            plugin.log.debug("Entity ${entity.customName} was not killed by a player.")
            remove(entity)
            return
        }

        if(SpecialMobUtil.isExtension(entity)) {
            plugin.log.debug("Entity was an extension, Ignoring")
            return
        }

        plugin.log.debug("Entity ${entity.customName} was killed by ${player.name}")
//        if(SpecialMobUtil.isSpecialMob(entity)) {
//            event.drops.addAll(getWorldMobs(entity.world).mobs[entity.uniqueId]?.drops.orEmpty())
//        }
    }

    @EventHandler
    private fun onChunkLoad(event: ChunkLoadEvent) {
        val worldMobs = getWorldMobs(event.world)
        event.chunk.entities.forEach {
            val remove = worldMobs.remove(it.uniqueId)
            if (!SpecialMobUtil.isSpecialMob(it)) return
            if (remove != null) remove.remove() else it.remove()
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private fun onTame(event: EntityTameEvent) {
        if(event.entity !is Tameable) return
        if(SpecialMobUtil.isSpecialMob(event.entity)) event.cancel()
    }

    private val mobRegistry = HashMap<String, WorldMobs>()
    private val lostEntities = ArrayDeque<Entity>()

    fun wrapMob(
        entity: Entity,
        mobFactory: MobFactory
    ) {
        if (entity !is LivingEntity
            || SpecialMobUtil.isSpecialMob(entity)
        ) return

        val specialMob = mobFactory.wrap(entity)
        registerMob(specialMob)
    }

    override suspend fun onEnable() {
        INSTANCE = this
        scheduler {
            for(world in server.worlds) {
                getWorldMobs(world).tick(5)
            }
            for(i in 0..lostEntities.size.coerceAtMost(10)) {
                lostEntities.poll().takeIf{it?.isValid == true}?.remove()
            }
        }.runAsyncTaskTimer(plugin, 100, 1)
    }

    private fun registerMob(mob: SpecialMob<*>) {
        val world: World = mob.baseEntity.location.world
        getWorldMobs(world).put(mob.baseEntity.uniqueId, mob)
    }

    fun getWorldMobs(
        world: World
    ) = mobRegistry.computeIfAbsent(world.name) {WorldMobs()}

    private fun remove(entity: Entity) {
        getWorldMobs(entity.world).remove(entity.uniqueId)
    }

    companion object {
        private lateinit var INSTANCE: MobManager

        fun wrapMob(
            entity: Entity,
            mobFactory: MobFactory
        ) = INSTANCE.wrapMob(entity, mobFactory)

        fun getWorldMobs(
            world: World
        ) = INSTANCE.getWorldMobs(world)
    }

}