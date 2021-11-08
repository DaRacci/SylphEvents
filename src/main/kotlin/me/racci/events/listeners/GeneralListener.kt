package me.racci.events.listeners

import me.racci.raccicore.utils.extensions.KotlinListener
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