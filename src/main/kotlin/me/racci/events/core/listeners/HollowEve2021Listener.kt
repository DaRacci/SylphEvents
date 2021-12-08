package me.racci.events.core.listeners

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import com.github.shynixn.mccoroutine.asyncDispatcher
import com.github.shynixn.mccoroutine.minecraftDispatcher
import kotlinx.coroutines.withContext
import me.racci.events.core.SylphEvents
import me.racci.events.api.factories.EventItemFactory
import me.racci.raccicore.api.extensions.KotlinListener
import me.racci.sylph.core.Sylph
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

class HollowEve2021Listener(private val plugin: SylphEvents) : KotlinListener {

    private val speedModifier = AttributeModifier(
        UUID.fromString("9f6ffc74-2d64-11ec-8d3d-0242ac130003"),
        "CANDYCORNARMOUR",
        0.25,
        AttributeModifier.Operation.ADD_SCALAR
    )

    private val invisibilityPotion = PotionEffect(
        PotionEffectType.INVISIBILITY,
        Int.MAX_VALUE,
        0,
        false,
        false,
        false,
        EventItemFactory["HOLLOWS_EVE_HAT", true]
    )

    private val candyCornKey: NamespacedKey = Sylph.namespacedKey("hollows_eve_2021_candy_corn_armour")

    private val foodPotions = arrayOf(
        arrayListOf(
            PotionEffect(
                PotionEffectType.HUNGER,
                60,
                4,
                false,
                false,
                false
            ),
            PotionEffect(
                PotionEffectType.SATURATION,
                60,
                9,
                false,
                false,
                false
            ),
            PotionEffect(
                PotionEffectType.SPEED,
                60,
                5,
                false,
                false,
                false
            ),
        ),
        arrayListOf(
            PotionEffect(PotionEffectType.HUNGER, 60, 10, false, false, false),
            PotionEffect(PotionEffectType.SATURATION, 60, 9, false, false, false),
            PotionEffect(PotionEffectType.SPEED, 60, 5, false, false, false),
        )
    )

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    suspend fun onCandyCornArmour(event: PlayerArmorChangeEvent) = withContext(plugin.asyncDispatcher) {
        val oldPDC = event.oldItem?.itemMeta?.persistentDataContainer
        val newPDC = event.newItem?.itemMeta?.persistentDataContainer

        if(event.player.isDead) return@withContext


        if(newPDC != oldPDC) {
            if(oldPDC?.has(candyCornKey, PersistentDataType.BYTE) == true) {
                event.player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)?.apply {
                    if(this.modifiers.contains(speedModifier)) {
                        removeModifier(speedModifier)
                    }
                }
            } else if(oldPDC?.has(EventItemFactory["HollowsEve2021.HOLLOWS_EVE_HAT", true], PersistentDataType.BYTE) == true
                      && event.player.getPotionEffect(PotionEffectType.INVISIBILITY)?.key == EventItemFactory["HollowsEve2021.HOLLOWS_EVE_HAT", true]) {
                withContext(plugin.minecraftDispatcher) {event.player.removePotionEffect(PotionEffectType.INVISIBILITY)}
            }

            if(newPDC?.has(candyCornKey, PersistentDataType.BYTE) == true) {
                if(event.player.inventory.armorContents.toList().filter { it.persistentDataContainer.has(candyCornKey, PersistentDataType.BYTE) }.size == 4) {
                    event.player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)?.apply {
                        if(!this.modifiers.contains(speedModifier)) {
                            event.player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)?.addModifier(speedModifier)
                        }
                    }
                }
            } else if(newPDC?.has(EventItemFactory["HollowsEve2021.HOLLOWS_EVE_HAT", true], PersistentDataType.BYTE) == true) {
                withContext(plugin.minecraftDispatcher) {event.player.addPotionEffect(invisibilityPotion)}
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onConsumeTreat(event: PlayerItemConsumeEvent) {
        if(event.item.persistentDataContainer.has(EventItemFactory["HollowsEve2021.GUMMY_FISH", true], PersistentDataType.BYTE)
            || event.item.persistentDataContainer.has(EventItemFactory["HollowsEve2021.CANDIED_BERRIES", true], PersistentDataType.BYTE)
        ) event.player.addPotionEffects(foodPotions[0])
        else if(event.item.persistentDataContainer.has(EventItemFactory["HollowsEve2021.BOWL_OF_CHOCOLATES", true], PersistentDataType.BYTE)) event.player.addPotionEffects(foodPotions[1])
    }

}