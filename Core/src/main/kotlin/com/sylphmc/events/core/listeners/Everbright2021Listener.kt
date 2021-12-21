package com.sylphmc.events.core.listeners

import com.sylphmc.events.api.factories.EventItemFactory
import me.racci.raccicore.api.extensions.KotlinListener
import me.racci.raccicore.api.extensions.cancel
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent

class Everbright2021Listener: KotlinListener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onSnowDamage(event: EntityDamageEvent) {
        val p = event.entity as? Player ?: return
        if(event.cause != EntityDamageEvent.DamageCause.FREEZE
            || p.inventory.helmet == null
            || p.inventory.helmet!!.type != Material.SEA_LANTERN
            || !p.inventory.helmet!!.persistentDataContainer.keys.contains(EventItemFactory["EVERBRIGHT_2021_HAT", true])
        ) return

        event.cancel()
    }
}