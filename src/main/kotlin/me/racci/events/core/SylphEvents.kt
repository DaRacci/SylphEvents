package me.racci.events.core

import co.aikar.commands.BaseCommand
import me.racci.events.api.data.ItemStackData
import me.racci.events.core.commands.EventCommands
import me.racci.events.core.factories.EventGUIFactoryImpl
import me.racci.events.core.factories.EventItemFactoryImpl
import me.racci.events.core.listeners.GeneralListener
import me.racci.events.core.listeners.HollowEve2021Listener
import me.racci.raccicore.api.extensions.KotlinListener
import me.racci.raccicore.api.lifecycle.LifecycleListener
import me.racci.raccicore.api.plugin.RacciPlugin
import me.racci.sylph.api.utils.capitalizeWords

class SylphEvents: RacciPlugin(
    "&2SylphEvents"
) {

    companion object {
        lateinit var instance: SylphEvents; private set
    }

    override suspend fun handleEnable() {
        instance = this
    }

    override suspend fun registerListeners(): List<KotlinListener> {
        return listOf(
            HollowEve2021Listener(this),
            GeneralListener(),
        )
    }

    override suspend fun registerLifecycles(): List<Pair<LifecycleListener<*>, Int>> {
        return listOf(
            EventItemFactoryImpl(this) to 1,
            EventGUIFactoryImpl(this) to 2,
        )
    }

    override suspend fun registerCommands(): List<BaseCommand> {
        commandManager.commandContexts.registerContext(ItemStackData::class.java) {
            EventItemFactoryImpl.INSTANCE.itemData.items[it.popFirstArg()]}
        commandManager.commandCompletions.registerAsyncCompletion("eventitems") {_->
            EventItemFactoryImpl.INSTANCE.itemData.items.keys.map{it.lowercase().capitalizeWords(true)}}
        return listOf(
            EventCommands(),
        )
    }
}