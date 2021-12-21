package com.sylphmc.everbright

import com.sylphmc.events.api.factories.EventItemFactory
import com.sylphmc.everbright.GUI.Companion.luckGUI
import com.sylphmc.everbright.mobmanager.MobManager
import com.sylphmc.everbright.specialmobs.factory.SpecialMobRegistry
import com.sylphmc.everbright.utils.NO_TOUCH
import me.racci.raccicore.api.extensions.*
import me.racci.raccicore.api.utils.now
import me.racci.sylph.api.factories.SoundFactory
import me.racci.sylph.api.hooks.HookManager
import me.racci.sylph.core.Sylph
import me.racci.sylph.core.data.Lang
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.title.Title
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import su.nightexpress.goldencrates.api.GoldenCratesAPI
import java.time.Duration
import java.util.*
import java.util.concurrent.ThreadLocalRandom

class Listener: KotlinListener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    fun onOffering(event: BlockPlaceEvent) {
        if(event.itemInHand.type != Material.NETHER_WART_BLOCK
            || !event.itemInHand.persistentDataContainer.keys.contains(EventItemFactory["DARK_OFFERING", true])
        ) return
        event.setBuild(false)
        if(HookManager.landsManager?.isClaimed(event.block.location) == true) {
            event.player.msg(Lang["prefix.prefix"].append("<aqua>You can't place this in a claimed land!".parse()).decoration(TextDecoration.BOLD, false))
            event.player.playSound(SoundFactory.errorSound)
            return
        }
        event.player.inventory.setItem(event.hand, event.itemInHand.asQuantity(event.itemInHand.amount -1))
        val factory = SpecialMobRegistry.getMobFactoryByName("ElderWishbane")!!
        val bane = event.block.world.spawn(event.block.location.add(0.0, 1.0, 0.0), factory.entityType.entityClass!!) {
            it.persistentDataContainer[NO_TOUCH, PersistentDataType.BYTE] = 1.toByte()
            MobManager.wrapMob(it as LivingEntity, factory)
        } as LivingEntity
        bane.isInvulnerable = true
        for(i in 0 until 4) {
            bane.location.add(r.nextDouble(-2.5, 2.5), r.nextDouble(-0.5, 0.5), r.nextDouble(-2.5, 2.5)).createExplosion(bane, 4f, false, false)
        }
        bane.isInvulnerable = false
        bane.attack(event.player)
    }

    private val REDEEM_PRESENT = Sylph.namespacedKey("redeem_present")

    private val r: ThreadLocalRandom = ThreadLocalRandom.current()

    private val presentLocations = arrayListOf(
        Location(server.worlds.first(), -355.0, 67.0, 114.0),
        Location(server.worlds.first(), -354.0, 66.0, 115.0),
        Location(server.worlds.first(), -355.0, 67.0, 116.0),
        Location(server.worlds.first(), -352.0, 66.0, 116.0),
        Location(server.worlds.first(), -354.0, 67.0, 118.0),
        Location(server.worlds.first(), -355.0, 66.0, 119.0),
        Location(server.worlds.first(), -355.0, 68.0, 118.0),
        Location(server.worlds.first(), -357.0, 66.0, 117.0),
        Location(server.worlds.first(), -358.0, 66.0, 116.0),
        Location(server.worlds.first(), -357.0, 67.0, 116.0),
        Location(server.worlds.first(), -357.0, 66.0, 115.0),
    )

    private fun gifting(): Set<ItemStack> =
        setOf(
            when(r.nextFloat()) {
                in 0f..0.33f -> EventItemFactory["STAR_COOKIE"].cachedItem.asQuantity(r.nextInt(3, 5))
                in 0.33f..0.66f -> EventItemFactory["JOGNOG"].cachedItem.asQuantity(r.nextInt(3, 5))
                in 0.66f..0.78f -> ItemStack(Material.DIAMOND).asQuantity(r.nextInt(3, 5))
                in 0.78f..0.90f -> ItemStack(Material.COAL).asQuantity(r.nextInt(3, 5))
                in 0.90f..0.95f -> EventItemFactory["PET_METEOR"].cachedItem.asQuantity(1)
                else            -> EventItemFactory["TOY_SWORD"].cachedItem.asQuantity(1)
            },
            GoldenCratesAPI.getKeyManager().getKeyById("everbright2021")!!.item.asQuantity(r.nextInt(1, 3))
        )

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    fun onPresent(event: PlayerInteractEvent) {

        if(!event.hasItem()
            || event.hand == null
            || event.item!!.type != Material.PLAYER_HEAD
            || !event.item!!.persistentDataContainer.keys.contains(EventItemFactory["PRESENT", true])
        ) return

        event.cancel()

        event.player.inventory.setItem(event.hand!!, event.item!!.asQuantity(event.item!!.amount -1))

        val items = event.player.inventory.addItem(*gifting().toTypedArray())
        event.player.playSound(SoundFactory.beeHiveSound)
        // Drop the items if the players' inv was full
        if(items.isNotEmpty()) {
            items.forEach {event.player.location.dropItemNaturally(it.value)}
        }

    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onRedeemPresent(event: PlayerInteractEvent) {

        if(!event.hasBlock()
            || event.clickedBlock!!.type != Material.PLAYER_HEAD
            || !presentLocations.contains(event.clickedBlock!!.location)
        ) return

        val pdc = event.player.pdc
        if(pdc[REDEEM_PRESENT, PersistentDataType.LONG] == null) {
            // Make sure the player's value isn't null
            event.player.pdc[Sylph.namespacedKey("redeem_present"), PersistentDataType.LONG] = 0
        }
        val pdcS = pdc[REDEEM_PRESENT, PersistentDataType.LONG]!!
        // If they have redeemed a present within the past eight hours say no
        if((now().epochSeconds - pdcS) < 72000) {
            val var1 : Double = (72000.0 - (now().epochSeconds - pdcS)) / 3600
            val var2 = var1.toString().split(".").toTypedArray()
            var2[1] = (var2[1].take(2).toInt() * 60).toString()
            var2.joinToString()
            var string = ""
            if(var2[0].toInt() > 0) {
                var2[0].apply {
                    string = if(this.toInt() > 1) "${this.take(2)} hours and " else "${this.take(2)} hour and "
                }
            }
            string += "${var2[1].take(2)} ${if(var2[1].take(2).toInt() > 1) "minutes" else "minute"}."

            val title = Title.title(
                Component.text("oopsie woopsie!").color(NamedTextColor.RED),
                Component.text("You have to wait another $string"),
                Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ZERO),
            )
            event.player.showTitle(title)

            return // Don't allow the continuation if they can't redeem it
        }
        // Set the players new time redeemed before dropping items to account for any ping glitching etc
        pdc[REDEEM_PRESENT, PersistentDataType.LONG] = now().epochSeconds

        val items = event.player.inventory.addItem(*gifting().toTypedArray())
        event.player.playSound(SoundFactory.beeHiveSound)
        // Drop the items if the players' inv was full
        if(items.isNotEmpty()) {
            items.forEach {event.player.location.dropItemNaturally(it.value)}
        }
    }

//    @EventHandler
//    fun onLuckInteract(event: PlayerInteractEntityEvent) {
//        println("Touch entity")
//        println("15b0b9c7-ae98-4ae3-b182-41a7e1761902")
//        println(event.rightClicked.uniqueId.toString())
//        if(event.rightClicked.uniqueId != UUID.fromString("15b0b9c7-ae98-4ae3-b182-41a7e1761902")) return
//        println("Thats luck")
//        event.cancel()
//        println("Showing GUI")
//        luckGUI.show(event.player)
//    }

}