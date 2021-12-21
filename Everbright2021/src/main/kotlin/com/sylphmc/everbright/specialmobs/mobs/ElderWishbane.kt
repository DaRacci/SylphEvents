package com.sylphmc.everbright.specialmobs.mobs

import com.sylphmc.events.api.factories.EventItemFactory
import com.sylphmc.everbright.utils.uuidBossBarNamespace
import me.racci.raccicore.api.extensions.asItemStack
import me.racci.raccicore.api.extensions.coloured
import me.racci.raccicore.api.extensions.parse
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.Wolf
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class ElderWishbane(entity: Wolf): Wishbane(entity) {

    override val name: Component get() = "<gold>ElderWishbane".parse()

    override val maxHealth get() = 10000.0
    override val armour get() = 15.0
    override val attackDamage get() = 60.0

    override val minionLimit get() = 15
    override val minionCooldown get() = 15
    override val teleportCooldown get() = 10

    override val potions: List<PotionEffect>
        get() = listOf(PotionEffect(PotionEffectType.REGENERATION, Int.MAX_VALUE, 1, true, false))

    override val drops: List<ItemStack>
        get() = listOf(
            EventItemFactory["PRESENT"].cachedItem.asQuantity(8),
            EventItemFactory["TATTERED_HIDE"].cachedItem,
            Material.DIAMOND.asItemStack(r.nextInt(16, 32)),
            Material.MUTTON.asItemStack(128),
        ).also{if(r.nextInt(0, 101) < 33) it.plus(EventItemFactory["HUNGERING_JAW"].cachedItem)}

    override val bossBar = Bukkit.createBossBar(
        uuidBossBarNamespace(baseEntity),
        "&6Elder Wishbane".coloured(),
        BarColor.YELLOW,
        BarStyle.SOLID,
    )

}