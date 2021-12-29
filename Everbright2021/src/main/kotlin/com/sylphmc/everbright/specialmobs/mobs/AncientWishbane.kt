package com.sylphmc.everbright.specialmobs.mobs

import com.sylphmc.events.api.factories.EventItemFactory
import com.sylphmc.everbright.utils.SpecialMobUtil
import com.sylphmc.everbright.utils.uuidBossBarNamespace
import kotlinx.coroutines.delay
import me.racci.raccicore.api.extensions.asItemStack
import me.racci.raccicore.api.extensions.coloured
import me.racci.raccicore.api.extensions.onlinePlayers
import me.racci.raccicore.api.extensions.parse
import me.racci.raccicore.api.utils.now
import me.racci.sylph.core.data.Lang
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.EntityEffect
import org.bukkit.Material
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.Player
import org.bukkit.entity.Vex
import org.bukkit.entity.Wolf
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class AncientWishbane(entity: Wolf): Wishbane(entity) {

    override val name: Component get() = "<red>Palewreath, the Ancient One".parse()

    override val maxHealth get() = 100000.0
    override val armour get() = 30.0
    override val attackDamage get() = 80.0
    private var lastRage: Long = 0
    override val boolean = true


    override val minionLimit get() = 15
    override val minionCooldown get() = 10
    override val teleportCooldown get() = 7

    override val potions: List<PotionEffect>
        get() = listOf(PotionEffect(PotionEffectType.REGENERATION, Int.MAX_VALUE, 2, true, false))

    override val drops: List<ItemStack>
        get() = listOf(
            EventItemFactory["PRESENT"].cachedItem.asQuantity(r.nextInt(32, 64)),
            EventItemFactory["HEART_OF_ALTRUISM"].cachedItem,
            Material.NETHERITE_INGOT.asItemStack(r.nextInt(24, 48)),
            Material.MUTTON.asItemStack(128),
            EventItemFactory["HUNGERING_JAWS"].cachedItem,
        )
    override val xp = 64000

    override val bossBar = Bukkit.createBossBar(
        uuidBossBarNamespace(baseEntity),
        "&cPalewreath, the Ancient One".coloured(),
        BarColor.PURPLE,
        BarStyle.SEGMENTED_6,
    )

    override fun onHit(event: EntityDamageByEntityEvent) {
        val p = event.entity as? Player ?: return
        if(p.foodLevel >= 1
        ) {
            p.foodLevel -= 1
        }
        Material.PHANTOM_MEMBRANE
    }

    override suspend fun afterSpawn() {
        Audience.audience(onlinePlayers).also {
            it.sendMessage(Lang["prefix.prefix"].append(" <aqua>An audacious soul has tampered with forces unknown.".parse()).decoration(TextDecoration.BOLD, false))
            for(i in 0..5) {
                it.playSound(Sound.sound(Key.key("entity.ghast.scream"), Sound.Source.HOSTILE, 1f, r.nextFloat(0.2f, 1.2f)), Sound.Emitter.self())
                delay(5)
            }
        }
    }

    override fun onDamage(event: EntityDamageEvent) {
        if(lastDamager == null) return
        if(baseEntity.health / maxHealth > 0.1 || now().epochSeconds - lastRage < 30) return
        lastRage = now().epochSeconds
        for(i in 0..30) {
            val loc = baseEntity.location.clone()
            loc.add(r.nextDouble(-2.5, 2.5), r.nextDouble(-0.5, 0.5), r.nextDouble(-2.5, 2.5))
            val minion = SpecialMobUtil.spawnMinion<Vex>(this@AncientWishbane, minionClass, loc)
            minion.playEffect(EntityEffect.ENTITY_POOF)
            minions.add(minion)
            minion.attack(lastDamager ?: return)
            minion.isCharging = true
        }
    }

}