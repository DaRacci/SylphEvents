package com.sylphmc.events.core.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import com.sylphmc.events.api.data.ItemStackData
import com.sylphmc.events.api.factories.EventGUIFactory
import com.sylphmc.events.core.factories.EventItemFactoryImpl
import me.racci.raccicore.api.extensions.dropItemNaturally
import me.racci.raccicore.api.extensions.msg
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("SylphEvents")
class EventCommands : BaseCommand() {

    @Default
    @CommandPermission("sylph.events.menu")
    fun onBase(sender: CommandSender) {
        if(sender !is Player) return
        EventGUIFactory.mainMenu.show(sender)
    }

    @Subcommand("dropallitems")
    @CommandPermission("sylph.events.dangerousperms")
    fun giveAllItems(sender: CommandSender) {
        if(sender !is Player) return
        println(EventItemFactoryImpl.INSTANCE.itemKeyCache.values)
        println(EventItemFactoryImpl.INSTANCE.itemData.items.keys)
        for(item in EventItemFactoryImpl.INSTANCE.itemData.items.values.map{it.cachedItem}) {
            sender.location.dropItemNaturally(item)
        }
    }

    @Subcommand("give")
    @CommandPermission("sylph.events.hollowsEve2021Items")
    @CommandCompletion("@eventitems [1-64]")
    fun giveEventItem(sender: CommandSender, item: ItemStackData, amount: Int) {
        if(sender !is Player) return
        if(amount > 64) return // TODO LANG
        val items = sender.inventory.addItem(item.cachedItem.asQuantity(amount))
        items.forEach{sender.location.dropItemNaturally(it.value)}
        sender.msg(miniMessage().parse("Gave $amount ${item.cachedItem.displayName()} to ${sender.name()}")) // TODO LANG
    }


}