package me.racci.events.listeners

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import com.github.shynixn.mccoroutine.asyncDispatcher
import com.github.shynixn.mccoroutine.minecraftDispatcher
import kotlinx.coroutines.withContext
import me.racci.events.SylphEvents
import me.racci.events.enums.HollowsEve2021
import me.racci.events.factories.ItemFactory
import me.racci.raccicore.api.extensions.KotlinListener
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.UUID

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
        ItemFactory[HollowsEve2021.HOLLOWS_EVE_HAT, true]
    )

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
            if(oldPDC?.has(ItemFactory[HollowsEve2021.CANDY_CORN_ARMOUR, true], PersistentDataType.BYTE) == true) {
                event.player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)?.apply {
                    if(this.modifiers.contains(speedModifier)) {
                        removeModifier(speedModifier)
                    }
                }
            } else if(oldPDC?.has(ItemFactory[HollowsEve2021.HOLLOWS_EVE_HAT, true], PersistentDataType.BYTE) == true
                      && event.player.getPotionEffect(PotionEffectType.INVISIBILITY)?.key == ItemFactory[HollowsEve2021.HOLLOWS_EVE_HAT, true]) {
                withContext(plugin.minecraftDispatcher) {event.player.removePotionEffect(PotionEffectType.INVISIBILITY)}
            }

            if(newPDC?.has(ItemFactory[HollowsEve2021.CANDY_CORN_ARMOUR, true], PersistentDataType.BYTE) == true) {
                if(event.player.inventory.armorContents.toList().filter { it.persistentDataContainer.has(ItemFactory[HollowsEve2021.CANDY_CORN_ARMOUR, true], PersistentDataType.BYTE) }.size == 4) {
                    event.player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)?.apply {
                        if(!this.modifiers.contains(speedModifier)) {
                            event.player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)?.addModifier(speedModifier)
                        }
                    }
                }
            } else if(newPDC?.has(ItemFactory[HollowsEve2021.HOLLOWS_EVE_HAT, true], PersistentDataType.BYTE) == true) {
                withContext(plugin.minecraftDispatcher) {event.player.addPotionEffect(invisibilityPotion)}
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onConsumeTreat(event: PlayerItemConsumeEvent) {
        when(event.item.persistentDataContainer) {
            ItemFactory[HollowsEve2021.GUMMY_FISH].persistentDataContainer, ItemFactory[HollowsEve2021.CANDIED_BERRIES].persistentDataContainer -> event.player.addPotionEffects(foodPotions[0])
            ItemFactory[HollowsEve2021.BOWL_OF_CHOCOLATES].persistentDataContainer                                                              -> event.player.addPotionEffects(foodPotions[1])
            else                                                                                                                                -> return
        }
    }
}