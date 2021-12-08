package me.racci.events.core.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import me.racci.events.api.data.ItemStackData
import me.racci.events.api.factories.EventGUIFactory
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

    @Subcommand("give")
    @CommandPermission("sylph.events.hollowsEve2021Items")
    @CommandCompletion("@players @eventitems [1-64]")
    fun giveEventItem(sender: CommandSender, target: Player, item: ItemStackData, amount: Int = 1) {
        if(amount > 64) return // TODO LANG
        val items = target.inventory.addItem(item.cachedItem.asQuantity(amount))
        items.forEach{target.location.dropItemNaturally(it.value)}
        sender.msg(miniMessage().parse("Gave $amount ${item.cachedItem.displayName()} to ${target.name()}")) // TODO LANG
    }


}