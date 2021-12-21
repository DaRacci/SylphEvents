package com.sylphmc.events.api.factories

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.sylphmc.events.core.SylphEvents
import com.sylphmc.events.core.factories.EventGUIFactoryImpl
import me.racci.raccicore.api.lifecycle.LifecycleListener
import java.util.HashMap

interface EventGUIFactory: LifecycleListener<SylphEvents> {

    val mainMenu: ChestGui
    val eventMenus: HashMap<String, ChestGui>


    companion object {
        private val provider get() = EventGUIFactoryImpl.INSTANCE

        val mainMenu get() = provider.mainMenu
        val eventMenus get() = provider.eventMenus
    }

}