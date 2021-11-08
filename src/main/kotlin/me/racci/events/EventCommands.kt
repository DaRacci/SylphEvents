package me.racci.events

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Subcommand
import me.racci.events.enums.HollowsEve2021
import me.racci.events.factories.GUI
import me.racci.events.factories.GUI.mainMenuGUI
import me.racci.events.factories.ItemFactory
import me.racci.raccicore.utils.extensions.pdc
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@CommandAlias("SylphEvents")
class EventCommands : BaseCommand() {

    @Default
    @CommandPermission("sylph.events.menu")
    fun onBase(sender: CommandSender) {
        GUI.mainMenuGUI.show(sender as Player)
    }

    @Subcommand("hollowseve2021 give")
    @CommandPermission("sylph.events.hollowsEve2021Items")
    @CommandCompletion("@players @hollowseve2021items 1-64")
    fun giveHollowEve2021(sender: CommandSender, target: Player, item: ItemStack, amount: Int = 1) {
        if(amount > 64) return
        var slot = target.inventory.first(item)
        if(slot == -1) slot = target.inventory.firstEmpty()
        if(slot == -1) return
        target.inventory.setItem(slot, item.asQuantity(amount))
        sender.sendMessage("Gave $amount ${item.displayName} to ${target.name}") // TODO LANG
    }
}