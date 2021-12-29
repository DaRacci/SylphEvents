package com.sylphmc.everbright.mobmanager

import com.sylphmc.events.core.SylphEvents
import com.sylphmc.everbright.specialmobs.MasterMob
import com.sylphmc.everbright.specialmobs.SpecialMob
import com.sylphmc.everbright.specialmobs.factory.MobFactory
import com.sylphmc.everbright.specialmobs.mobs.Wishbane
import com.sylphmc.everbright.utils.NO_TOUCH
import com.sylphmc.everbright.utils.SpecialMobUtil
import com.sylphmc.everbright.utils.uuidBossBarNamespace
import kotlinx.coroutines.delay
import me.racci.raccicore.api.extensions.*
import me.racci.raccicore.api.lifecycle.LifecycleListener
import me.racci.sylph.api.utils.SPECIAL
import me.racci.sylph.core.Sylph
import me.racci.sylph.core.data.Lang
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.attribute.Attribute
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityTameEvent
import org.bukkit.event.vehicle.VehicleEnterEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.persistence.PersistentDataType
import java.util.*
import java.util.concurrent.ThreadLocalRandom


val wishbane = Sylph.namespacedKey("wishbane")
val lost = Sylph.namespacedKey("lost_mob")

class MobManager(
    override val plugin: SylphEvents,
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

        if(random.nextInt(101) > 1) return

        val nearby = event.location.getNearbyEntities(75.0, 50.0, 75.0)
        for(mob in nearby) {
            if(!mob.pdc.keys.contains(wishbane)) continue
            return
        }

        val factory = MobFactory[Wishbane::class.java] ?: return

        val mob = if(factory.entityType != event.entity.type) {
            val loc = event.entity.location
            event.entity.remove()
            loc.world.spawn(loc, factory.entityType.entityClass!!) {
                it.persistentDataContainer[NO_TOUCH, PersistentDataType.BYTE] = 1.toByte()
            } as LivingEntity
        } else event.entity

        for(player in nearby) {
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
    private fun onEnterBoat(event: VehicleEnterEvent) {
        if(SpecialMobUtil.isSpecialMob(event.entered)) event.cancel()
    }

    @EventHandler // TODO Remove minions when unloading master type
    private fun onChunkUnload(event: ChunkUnloadEvent) {
        val worldMobs = getWorldMobs(event.world)
        event.chunk.entities.asSequence()
                .mapNotNull {
                    val r = worldMobs.mobs.remove(it.uniqueId)
                    worldMobs.tickQueue.remove(r)
                    r
                }
                .forEach {
                    plugin.log.debug("Unloading ${it.baseEntity.name} as type ${it::class.java.simpleName}")
                    it.baseEntity.pdc.set(lost, PersistentDataType.STRING, it::class.java.name)
                    if(it is MasterMob<*>) {
                        it.minions.forEach(::remove)
                    }
                }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private suspend fun onChunkLoad(event: ChunkLoadEvent) {
        val worldMobs = getWorldMobs(event.world)
        val chunkCoord = ChunkCoord(event.chunk)
        delay(100)
        chunkCoord.chunk?.entities?.asSequence()
                ?.filter{plugin.log.debug("Trying to recover ${it.name} with keys ${it.pdc.keys}"); it.pdc.keys.contains(lost)}
                ?.forEach {
//                    println("Recovering lost mob ${it.name}")
                    val factory = MobFactory[Class.forName(it.pdc[lost, PersistentDataType.STRING]) as Class<SpecialMob<*>>] ?: return@forEach
//                    println("Mob is of type $factory")
                    it.pdc.keys.remove(lost)
                    val f = factory.factory.invoke(it as LivingEntity)
                    it.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = f.maxHealth
                    it.getAttribute(Attribute.GENERIC_ARMOR)!!.baseValue = f.armour
                    it.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)!!.baseValue = f.attackDamage
//                    println("Invoked mob factory for new ${factory.clazz}")
                    worldMobs.put(it.uniqueId, f)
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
        mobFactory: MobFactory,
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
        world: World,
    ) = mobRegistry.computeIfAbsent(world.name) {WorldMobs()}

    private fun remove(entity: Entity) {
        getWorldMobs(entity.world).remove(entity.uniqueId)
    }

    companion object {
        private lateinit var INSTANCE: MobManager

        fun wrapMob(
            entity: Entity,
            mobFactory: MobFactory,
        ) = INSTANCE.wrapMob(entity, mobFactory)

        fun getWorldMobs(
            world: World,
        ) = INSTANCE.getWorldMobs(world)
    }

}

class ChunkCoord(
    val worldUUID: UUID,
    val x: Int,
    val z: Int,
) {
    constructor(chunk: Chunk): this(chunk.world.uid, chunk.x, chunk.z)
    constructor(loc: Location): this(loc.world.uid, loc.blockX shr 4, loc.blockZ shr 4)

    override fun equals(obj: Any?): Boolean {
        if(this === obj) {
            return true
        }
        if(obj == null || javaClass != obj.javaClass) {
            return false
        }
        val other = obj as ChunkCoord
        if(worldUUID != other.worldUUID) {
            return false
        }
        return x == other.x && z == other.z
    }

    val chunk: Chunk?
        get() {
            val world = Bukkit.getWorld(worldUUID)
            return world?.getChunkAt(x, z)
        }

    override fun hashCode(): Int {
        val prime = 31
        return prime * (prime * (prime + (worldUUID.hashCode() ?: 0)) + x) + z
    }

    fun setForceLoaded(b: Boolean) {
        val chunk = chunk
        if(chunk != null && SUPPORTS_FORCE_LOADED) {
            try {
                chunk.isForceLoaded = b
            } catch(e: NoSuchMethodError) {
                SUPPORTS_FORCE_LOADED = false
            }
        }
    }

    override fun toString(): String {
        return "[$x,$z]"
    }

    companion object {
        private var SUPPORTS_FORCE_LOADED = true
    }
}