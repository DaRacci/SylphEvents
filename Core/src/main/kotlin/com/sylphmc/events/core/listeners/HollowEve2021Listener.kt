package com.sylphmc.events.core.listeners

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import com.github.shynixn.mccoroutine.asyncDispatcher
import com.github.shynixn.mccoroutine.minecraftDispatcher
import com.sylphmc.events.api.factories.EventItemFactory
import com.sylphmc.events.core.SylphEvents
import kotlinx.coroutines.withContext
import me.racci.raccicore.api.extensions.KotlinListener
import me.racci.sylph.core.Sylph
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
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

    // TODO Turn pdc matcher into map to invoke removing and enabled without these cancer if else statements.
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
            } else if(oldPDC?.has(EventItemFactory["HOLLOWS_EVE_HAT", true], PersistentDataType.BYTE) == true
                      && event.player.getPotionEffect(PotionEffectType.INVISIBILITY)?.key == EventItemFactory["HOLLOWS_EVE_HAT", true]) {
                withContext(plugin.minecraftDispatcher) {event.player.removePotionEffect(PotionEffectType.INVISIBILITY)}
            }

            if(newPDC?.has(candyCornKey, PersistentDataType.BYTE) == true) {
                // Yes this needs a null check because????? idk fuck you bukkit
                if(event.player.inventory.armorContents.filter { it != null && it.persistentDataContainer.has(candyCornKey, PersistentDataType.BYTE) }.size == 4) {
                    event.player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)?.apply {
                        if(!this.modifiers.contains(speedModifier)) {
                            event.player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)?.addModifier(speedModifier)
                        }
                    }
                }
            } else if(newPDC?.has(EventItemFactory["HOLLOWS_EVE_HAT", true], PersistentDataType.BYTE) == true) {
                withContext(plugin.minecraftDispatcher) {event.player.addPotionEffect(invisibilityPotion)}
            }
        }
    }

}