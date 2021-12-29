package com.sylphmc.events.core.listeners

import com.sylphmc.events.api.factories.EventItemFactory
import me.racci.raccicore.api.extensions.KotlinListener
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class FoodListener: KotlinListener {

    private val foodPotions = arrayOf(
        arrayListOf(
            PotionEffect(PotionEffectType.HUNGER, 60, 4, false, false, false),
            PotionEffect(PotionEffectType.SATURATION, 60, 9, false, false, false),
            PotionEffect(PotionEffectType.SPEED, 60, 5, false, false, false),
        ),
        arrayListOf(
            PotionEffect(PotionEffectType.HUNGER, 60, 10, false, false, false),
            PotionEffect(PotionEffectType.SATURATION, 60, 9, false, false, false),
            PotionEffect(PotionEffectType.SPEED, 60, 5, false, false, false),
        ),
        arrayListOf(
            PotionEffect(PotionEffectType.FAST_DIGGING, 400, 3, false, false, false)
        )
    )

    // Make an invoker for each key thingy
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onConsumeTreat(event: PlayerItemConsumeEvent) {
        if(event.item.persistentDataContainer.has(EventItemFactory["GUMMY_FISH", true], PersistentDataType.BYTE)
            || event.item.persistentDataContainer.has(EventItemFactory["CANDIED_BERRIES", true], PersistentDataType.BYTE)
        ) event.player.addPotionEffects(foodPotions[0])
        else if(event.item.persistentDataContainer.has(EventItemFactory["BOWL_OF_CHOCOLATES", true], PersistentDataType.BYTE)) event.player.addPotionEffects(foodPotions[1])
        else if(event.item.persistentDataContainer.has(EventItemFactory["JOGNOG", true], PersistentDataType.BYTE)) {
            event.player.saturation += 0.5f
            event.player.foodLevel += 3
            event.player.addPotionEffects(foodPotions[2])
        } else if(event.item.persistentDataContainer.has(EventItemFactory["STAR_COOKIE", true], PersistentDataType.BYTE)) {
            event.player.saturation += 0.5f
            event.player.foodLevel += 3
        }
    }

}