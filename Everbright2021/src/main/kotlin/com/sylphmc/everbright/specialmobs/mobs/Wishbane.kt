package com.sylphmc.everbright.specialmobs.mobs

import com.sylphmc.events.api.factories.EventItemFactory
import com.sylphmc.events.core.SylphEvents
import com.sylphmc.everbright.specialmobs.MasterMob
import com.sylphmc.everbright.utils.SpecialMobUtil
import com.sylphmc.everbright.utils.uuidBossBarNamespace
import me.racci.raccicore.api.extensions.asItemStack
import me.racci.raccicore.api.extensions.coloured
import me.racci.raccicore.api.extensions.parse
import me.racci.raccicore.api.utils.now
import net.kyori.adventure.text.Component
import org.bukkit.*
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.*
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityTargetEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.floor

open class Wishbane(entity: Wolf): MasterMob<Wolf>(entity) {

    val r: ThreadLocalRandom = ThreadLocalRandom.current()
    private var lastMinion = 0L
    private var lastTeleport = 0L
    private var lastPlayerCheck = 0L
    private var lastDamager: Player? = null

    open val teleportCooldown get() = 20
    open val minionCooldown get() = 30
    open val minionLimit get() = 6

    override val name: Component get() = "<aqua>Wishbane".parse()

    override val maxHealth get() = 3000.0
    override val armour get() = 10.0
    override val attackDamage get() = 40.0
    override val movementSpeedMultiplier get() = 0.5
    override val potions
        get() = listOf(PotionEffect(PotionEffectType.REGENERATION, Int.MAX_VALUE, 0, true, false))

    override val drops: List<ItemStack>
        get() = listOf(
            EventItemFactory["PRESENT"].cachedItem,
            EventItemFactory["TATTERED_HIDE"].cachedItem,
            Material.DIAMOND.asItemStack(r.nextInt(8, 16)),
            Material.MUTTON.asItemStack(32)
        )

//    override val spawnAnnouncement: Component?
//        get() = Lang["prefix.prefix"].append("<aqua>A ")

    open val bossBar = Bukkit.createBossBar(
        uuidBossBarNamespace(baseEntity),
        "&bWishbane".coloured(),
        BarColor.BLUE,
        BarStyle.SOLID,
    )

    override fun beforeWrap() {
        baseEntity.isAngry = true
        baseEntity.isRabid = true
        bossBar.progress = 1.0
    }

    override fun onTargetEvent(event: EntityTargetEvent) {
        if(event.target == null || event.target!!.isDead) return
        minions.onEach{it.target = event.target as LivingEntity}
    }

    override fun onDamageByEntity(event: EntityDamageByEntityEvent) {
        val damager = event.damager
        val entity = event.entity as? LivingEntity ?: return
         if (damager is Projectile && damager.shooter is Player) {
            lastDamager = damager.shooter as Player
        }
        var hp = entity.health - event.finalDamage
        if (hp < 0.0) hp = 0.0
        hp = floor(hp * 100.0) / 100.0
        hp = hp * 100.0 / maxHealth
        bossBar.progress = hp / 100
    }

    override fun onHit(event: EntityDamageByEntityEvent) {
        val p = event.entity as? Player ?: return
        if(r.nextBoolean()
           && p.foodLevel >= 1
        ) {
            p.foodLevel -= 1
        }
    }

    override fun onDeath(event: EntityDeathEvent) {
        event.drops.addAll(drops)
        bossBar.isVisible = false
    }

    override fun tick() {
        if (now().epochSeconds - lastPlayerCheck > 5) {
            lastPlayerCheck = now().epochSeconds
            SylphEvents.launch {
                val nearbyEntities = baseEntity.getNearbyEntities(50.0, 50.0, 50.0)
                for(it in nearbyEntities) {
                    if(it !is Player) continue
                    if(it in bossBar.players) continue
                    bossBar.addPlayer(it)
                }
                for(it in bossBar.players) {
                    if(it in nearbyEntities) continue
                    bossBar.removePlayer(it)
                }
            }
        }
        val now = now().epochSeconds
        if(now - lastMinion > minionCooldown
            && minions.size < minionLimit
        ) {
//            println("Trying to summon minions")
            val loc = baseEntity.location.clone()
            var summonLoc: Location? = null
//            for(i in 0..10) {
//                println("Retrying location attempt $i")
            val fuzz = Vector(r.nextDouble(-5.0, 5.0), 0.0, r.nextDouble(-5.0, 5.0))
            val var1 = loc.world.getBlockAt(loc.add(fuzz))
//            val var2 = var1.getRelative(0, 1, 0)
//            if (var1.type != Material.AIR || var2.type != Material.AIR) continue
            summonLoc = var1.location
//                break
//            }
//            if(summonLoc == null) return
            SylphEvents.launch {
                val minion = SpecialMobUtil.spawnMinion<Vex>(this@Wishbane, WishbaneMinion::class.java, summonLoc)
                SpecialMobUtil.spawnParticlesAround(minion, Particle.CAMPFIRE_SIGNAL_SMOKE, 10)
                minion.playEffect(EntityEffect.ENTITY_POOF)
                minions.add(minion)
                lastMinion = now().epochSeconds
                baseEntity.world.playSound(baseEntity.location, Sound.ENTITY_POLAR_BEAR_WARNING, 1.0f, 1.0f)
            }
        }
        val player = lastDamager ?: return
        if(now - lastTeleport > teleportCooldown) {
            if (baseEntity.location.distance(player.location) > 50.0) {
                lastDamager = null
                return
            }
            lastTeleport = now().epochSeconds
            val loc = player.location
            loc.add(player.eyeLocation.direction.normalize().multiply(-0.7)).add(0.0, 0.5, 0.0)
            baseEntity.teleportAsync(loc)
            baseEntity.target = player
            baseEntity.world.playSound(loc, Sound.ENTITY_GHAST_SCREAM, 1.0f, 1.0f)
            baseEntity.world.playSound(loc, Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1.0f, 1.0f)
        }
    }

    override fun remove() {
        bossBar.removeAll()
        bossBar.isVisible = false
        super.remove()
    }

}