package me.racci.events

import co.aikar.commands.BaseCommand
import me.racci.events.enums.HollowsEve2021
import me.racci.events.factories.GUI
import me.racci.events.factories.ItemFactory
import me.racci.events.factories.RecipeFactory
import me.racci.events.listeners.GeneralListener
import me.racci.events.listeners.HollowEve2021Listener
import me.racci.raccicore.api.extensions.KotlinListener
import me.racci.raccicore.api.lifecycle.LifecycleListener
import me.racci.raccicore.api.plugin.RacciPlugin
import org.bukkit.inventory.ItemStack
import java.util.Locale
import kotlin.properties.Delegates

class SylphEvents: RacciPlugin(
    "&2SylphEvents"
) {

    companion object {
        var instance by Delegates.notNull<SylphEvents>() ; private set
    }

    override suspend fun handleEnable() {
        instance = this
    }

    override suspend fun registerListeners(): List<KotlinListener> {
        return listOf(
            HollowEve2021Listener(this),
            GeneralListener()
        )
    }

    override suspend fun registerLifecycles(): List<Pair<LifecycleListener<*>, Int>> {
        return listOf(
            ItemFactory(this) to 1,
            GUI(this) to 2,
            RecipeFactory(this) to 3,
        )
    }

    override suspend fun registerCommands(): List<BaseCommand> {
        commandManager.commandContexts.registerContext(ItemStack::class.java) {
            ItemFactory.hollowsEve2021Items.getValue(HollowsEve2021.valueOf(
                it.popFirstArg().splitCapsThing()
            ))}
            commandManager.commandCompletions.registerAsyncCompletion("hollowseve2021items") {_->
                ItemFactory.hollowsEve2021Items.entries.map{it.key.name.lowercase().capitalizeWords(true)}}
        return listOf(
            EventCommands(),
        )
    }
}

internal fun String.capitalizeWords(removeSpaces: Boolean = false) =
    split(
        " ",
        "_"
    ).joinToString(if(removeSpaces) "" else " ") { it -> it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } }

internal fun String.splitCapsThing() : String {
    var newString = ""
    forEachIndexed { i, c ->
        newString += ("${if(c.isUpperCase() && i > 0) "_" else ""}${c.uppercase()}")
    }
    return newString
}