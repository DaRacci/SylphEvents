package com.sylphmc.hollowseve2021

import com.github.shynixn.mccoroutine.asyncDispatcher
import com.sylphmc.events.api.factories.EventItemFactory
import com.sylphmc.events.core.SylphEvents
import kotlinx.coroutines.withContext
import me.racci.raccicore.api.extensions.KListener
import me.racci.raccicore.api.utils.now
import me.racci.sylph.core.Sylph
import net.citizensnpcs.api.event.NPCRightClickEvent
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.persistence.PersistentDataType
import su.nightexpress.goldencrates.api.GoldenCratesAPI
import java.time.Duration
import java.util.*
import kotlin.random.Random.Default.nextFloat
import kotlin.random.Random.Default.nextInt

class HollowEve2021Listener(
    override val plugin: SylphEvents
): KListener<SylphEvents> {

    private val npcUUID = arrayOf(
        UUID.fromString("b4099652-20ec-4105-b649-ac1192b8dac9"), // GIGI
        UUID.fromString("6096cbed-01b4-4092-945a-687ae32d02b3"), // Bug
        UUID.fromString("11a2248a-0b0b-4bae-b56a-4d653d149be6"), // Chuck
    )

    private val trickOrTreatNamespacedkey = arrayOf(
        Sylph.namespacedKey("trickortreat_gigi"),
        Sylph.namespacedKey("trickortreat_bug"),
        Sylph.namespacedKey("trickortreat_chuck"),
    )

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlaceHatOrMask(event: BlockPlaceEvent) {
        if(event.itemInHand.hasCustomModelData() && event.itemInHand.customModelData == 55) {
            event.isCancelled = true
        }
    }

    private fun trickOrTreat() =
        when(nextFloat()) {
            in 0f..0.33f -> EventItemFactory["HollowsEve2021.CANDY_CORN"].cachedItem.asQuantity(nextInt(11, 17))  // 5 - 9
            in 0.33f..0.44f -> EventItemFactory["HollowsEve2021.CANDIED_BERRIES"].cachedItem.asQuantity(nextInt(6, 12)) // 3 - 5
            in 0.44f..0.55f -> EventItemFactory["HollowsEve2021.GUMMY_FISH"].cachedItem.asQuantity(nextInt(6, 9)) // 3-5
            in 0.55f..0.66f -> EventItemFactory["HollowsEve2021.BOWL_OF_CHOCOLATES"].cachedItem.asQuantity(nextInt(2, 5))
            in 0.66f..1f -> GoldenCratesAPI.getKeyManager().getKeyById("hollowseve")?.item?.asQuantity(nextInt(1, 2))
            else -> null
        }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    fun onInteractChuck(event: NPCRightClickEvent) {
        if(event.npc.uniqueId != UUID.fromString("11a2248a-0b0b-4bae-b56a-4d653d149be6")) return
        if (event.clicker.inventory.itemInMainHand.type == Material.BUCKET) return
        GUI.chuckGUI.show(event.clicker)
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    suspend fun onInteractNPC(event: NPCRightClickEvent) = withContext(plugin.asyncDispatcher){
        if(event.npc.uniqueId !in npcUUID) return@withContext
        event.clicker.inventory.itemInMainHand.apply {
            if (this.type != Material.BUCKET) return@withContext
            if (!this.persistentDataContainer.keys.contains(EventItemFactory["HollowsEve2021.CANDY_PAIL", true])) return@withContext
        }

        val namespacedKey = when(event.npc.uniqueId) {
            npcUUID[0] -> {trickOrTreatNamespacedkey[0]} // Gigi
            npcUUID[1] -> {trickOrTreatNamespacedkey[1]} // Bug
            npcUUID[2] -> {trickOrTreatNamespacedkey[2]} // Chuck
            else -> return@withContext
        }

        val pdc = event.clicker.persistentDataContainer
        if(pdc[namespacedKey, PersistentDataType.LONG] == null) {
            pdc[namespacedKey, PersistentDataType.LONG] = 0L
        }
        if(now().epochSeconds - pdc[namespacedKey, PersistentDataType.LONG]!! < 0) {
            pdc[namespacedKey, PersistentDataType.LONG] = 0L
        }

        pdc[namespacedKey, PersistentDataType.LONG]!!.apply {
            println("$this")
            println("${now().epochSeconds - this}")
            if((now().epochSeconds - this) < 28800) {
                val var1 : Double = (28800.0 - (now().epochSeconds - this)) / 3600
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
                event.clicker.showTitle(title)
                return@withContext
            }
            val item = trickOrTreat()
            event.clicker.apply {
                persistentDataContainer[namespacedKey, PersistentDataType.LONG] = now().epochSeconds
                if(item != null) inventory.addItem(item)
                playSound(Sound.sound(Key.key("block.beehive.exit"), Sound.Source.PLAYER, 0.7f, 0.7f))
            }
        }
    }

}