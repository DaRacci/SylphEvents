package com.sylphmc.everbright

import com.github.shynixn.mccoroutine.registerSuspendingEvents
import com.google.common.collect.ImmutableList
import com.sylphmc.events.core.SylphEvents
import com.sylphmc.everbright.commands.EverbrightCommand
import com.sylphmc.everbright.mobmanager.EventForwarder
import com.sylphmc.everbright.mobmanager.MobManager
import com.sylphmc.everbright.specialmobs.factory.SpecialMobRegistry
import com.sylphmc.everbright.specialmobs.mobs.*
import me.racci.raccicore.api.extensions.pm
import me.racci.raccicore.api.extensions.registerEvents
import me.racci.raccicore.api.extensions.server
import me.racci.raccicore.api.lifecycle.Lifecycle
import me.racci.raccicore.api.lifecycle.LifecycleEvent
import me.racci.raccicore.api.lifecycle.LifecycleListener
import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.trait.TraitInfo
import org.bukkit.entity.*

class Everbright(
    override val plugin: SylphEvents,
): LifecycleListener<SylphEvents> {

    override suspend fun onEnable() {
        val eventForwarder = EventForwarder()
        val mobManager = MobManager(plugin)
        val gui = GUI(plugin)

        listOf(
            eventForwarder,
            mobManager,
            Listener()
        ).forEach {pm.registerSuspendingEvents(it, plugin)}
        plugin.lifecycleListeners.addAll(listOf(Lifecycle(5, mobManager), Lifecycle(6, gui)))
        mobManager(LifecycleEvent.ENABLE)
        gui(LifecycleEvent.ENABLE)
        registerMobs()

//        plugin.log.debugMode = false

        if(server.pluginManager.getPlugin("Citizens") != null && server.pluginManager.getPlugin("Citizens")!!.isEnabled) {
            CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(LuckTrait::class.java).withName("luck_trait"))
        }

        plugin.commandManager.commandCompletions.registerAsyncCompletion("everbright") {
            ImmutableList.of("AncientWishbane", "ElderWishbane", "Wishbane")}
        plugin.commandManager.registerCommand(EverbrightCommand())
    }

    private fun registerMobs() {
        SpecialMobRegistry.registerMob(
            EntityType.WOLF,
            Wishbane::class.java,
        ) {Wishbane(this as Wolf)}
        SpecialMobRegistry.registerMob(
            EntityType.WOLF,
            ElderWishbane::class.java,
        ) {ElderWishbane(this as Wolf)}
        SpecialMobRegistry.registerMob(
            EntityType.WOLF,
            AncientWishbane::class.java,
        ) {AncientWishbane(this as Wolf)}
        SpecialMobRegistry.registerMinion(
            EntityType.VEX,
            WishbaneMinion::class.java,
        ) {master, minion -> WishbaneMinion(minion as Vex, master)}
        SpecialMobRegistry.registerMinion(
            EntityType.VEX,
            AncientWishbaneMinion::class.java,
        ) {master, minion -> AncientWishbaneMinion(minion as Vex, master)}
    }

}