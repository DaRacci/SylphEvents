package com.sylphmc.everbright.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import com.sylphmc.everbright.mobmanager.MobManager
import com.sylphmc.everbright.specialmobs.factory.SpecialMobRegistry
import com.sylphmc.everbright.utils.NO_TOUCH
import me.racci.raccicore.api.extensions.msg
import org.bukkit.command.CommandSender
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

@CommandAlias("everbright")
class EverbrightCommand: BaseCommand() {

    @Default
    @CommandPermission("sylph.events.admin")
    fun onCommand(sender: CommandSender) {
        if(sender !is Player) return
        sender.msg("This does nothing lol")
    }

    @CommandAlias("spawn")
    @CommandPermission("sylph.events.admin")
    @CommandCompletion("@everbright")
    fun onSpawnMob(sender: CommandSender, mob: String) {
        if(sender !is Player) return
        val factory = SpecialMobRegistry.getMobFactoryByName(mob) ?: return
        val block = sender.getTargetBlock(50) ?: return
        block.world.spawn(block.location.add(0.0, 1.0, 0.0), factory.entityType.entityClass!!) {
            it.persistentDataContainer[NO_TOUCH, PersistentDataType.BYTE] = 1.toByte()
            MobManager.wrapMob(it as LivingEntity, factory)
        }
    }


}