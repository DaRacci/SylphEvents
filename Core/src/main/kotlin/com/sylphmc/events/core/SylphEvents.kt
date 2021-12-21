package com.sylphmc.events.core

import co.aikar.commands.BaseCommand
import com.github.shynixn.mccoroutine.launch
import com.github.shynixn.mccoroutine.launchAsync
import com.sylphmc.events.api.data.ItemStackData
import com.sylphmc.events.core.commands.EventCommands
import com.sylphmc.events.core.factories.EventGUIFactoryImpl
import com.sylphmc.events.core.factories.EventItemFactoryImpl
import com.sylphmc.events.core.listeners.Everbright2021Listener
import com.sylphmc.events.core.listeners.FoodListener
import com.sylphmc.events.core.listeners.GeneralListener
import com.sylphmc.events.core.listeners.HollowEve2021Listener
import kotlinx.coroutines.CoroutineScope
import me.racci.raccicore.api.extensions.KotlinListener
import me.racci.raccicore.api.lifecycle.LifecycleListener
import me.racci.raccicore.api.plugin.RacciPlugin
import me.racci.raccicore.api.utils.classConstructor
import me.racci.sylph.api.utils.capitalizeWords
import me.racci.sylph.api.utils.splitCapsThing
import java.lang.reflect.Constructor

class SylphEvents: RacciPlugin(
    "&2SylphEvents"
) {

    companion object {
        lateinit var instance: SylphEvents; private set
        fun launch(f: suspend CoroutineScope.() -> Unit) = instance.launch(f)
        fun launchAsync(f: suspend CoroutineScope.() -> Unit) = instance.launchAsync(f)
    }

    override suspend fun handleEnable() {
        instance = this
    }

    override suspend fun registerListeners(): List<KotlinListener> {
        return listOf(
            HollowEve2021Listener(this),
            Everbright2021Listener(),
            GeneralListener(),
            FoodListener(),
        )
    }

    override suspend fun registerLifecycles(): List<Pair<LifecycleListener<*>, Int>> {
        val everbright: Constructor<*> = Class.forName("com.sylphmc.everbright.Everbright").getConstructor(SylphEvents::class.java)

        return listOf(
            EventItemFactoryImpl(this) to 1,
            EventGUIFactoryImpl(this) to 2,
            // This is the only entry point to initialising the everbright module
            classConstructor(everbright, this) as LifecycleListener<*> to 4,
        )
    }

    override suspend fun registerCommands(): List<BaseCommand> {
        commandManager.commandContexts.registerContext(ItemStackData::class.java) {
            EventItemFactoryImpl.INSTANCE.itemData.items[it.popFirstArg().splitCapsThing()]}
        commandManager.commandCompletions.registerAsyncCompletion("eventitems") {_->
            EventItemFactoryImpl.INSTANCE.itemData.items.keys.map{it.lowercase().capitalizeWords(true)}}
        return listOf(
            EventCommands(),
        )
    }
}