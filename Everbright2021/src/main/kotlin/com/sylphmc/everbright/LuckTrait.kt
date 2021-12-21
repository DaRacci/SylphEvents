package com.sylphmc.everbright

import net.citizensnpcs.api.event.NPCRightClickEvent
import net.citizensnpcs.api.trait.Trait
import org.bukkit.event.EventHandler

class LuckTrait: Trait("luck_trait") {

    @EventHandler
    fun click(event: NPCRightClickEvent) {
        if(event.npc != npc) return
        GUI.luckGUI.show(event.clicker)
        //Handle a click on a NPC. The event has a getNPC() method.
        //Be sure to check event.getNPC() == this.getNPC() so you only handle clicks on this NPC!
    }

}