package me.racci.events.core.listeners

import me.racci.raccicore.api.extensions.KotlinListener
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockPlaceEvent

class GeneralListener: KotlinListener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlaceNonPlaceable(event: BlockPlaceEvent) {
        if(event.itemInHand.hasCustomModelData() && event.itemInHand.customModelData == 55) {
            event.isCancelled = true
        }
    }

}