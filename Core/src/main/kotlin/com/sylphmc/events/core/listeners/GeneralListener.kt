package com.sylphmc.events.core.listeners

import me.racci.raccicore.api.extensions.KotlinListener
import me.racci.raccicore.api.extensions.cancel
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockPlaceEvent

class GeneralListener: KotlinListener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlaceNonPlaceable(event: BlockPlaceEvent) {
        if(event.itemInHand.hasCustomModelData() && event.itemInHand.customModelData == 55) {
            event.cancel()
        }
    }

}