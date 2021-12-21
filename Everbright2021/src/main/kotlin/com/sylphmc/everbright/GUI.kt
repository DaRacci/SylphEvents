package com.sylphmc.everbright

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.Pane
import com.github.stefvanschie.inventoryframework.pane.util.Pattern
import com.sylphmc.events.core.SylphEvents
import me.racci.raccicore.api.builders.ItemBuilder
import me.racci.raccicore.api.extensions.*
import me.racci.raccicore.api.lifecycle.LifecycleListener
import me.racci.sylph.api.factories.GUIFactory
import me.racci.sylph.api.factories.GUIFactory.Companion.borderFiller
import me.racci.sylph.api.factories.SoundFactory
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player

class GUI(
    override val plugin: SylphEvents
): LifecycleListener<SylphEvents> {

    lateinit var luckGUI : ChestGui

    override suspend fun onEnable() {
        INSTANCE = this
        generateMenu()
    }

    private fun generateMenu() {
        luckGUI = ChestGui(3, "Luck") {
            val itemLore = arrayOf(
                Component.empty(),
                "<white><bold>»</bold> <aqua>Click</aqua> <bold>«</bold> <aqua>to ask this question.".parse().noItalic()
            )
            addPane(
                PatternPane(
                    0, 0,
                    9, 3,
                    Pane.Priority.HIGH,
                    Pattern(
                        "000000000",
                        "012345670",
                        "000080000"
                    )
                ) {
                    bindItem('0', borderFiller[Material.GRAY_STAINED_GLASS_PANE]!!)
                    bindItem('1', GuiItem(ItemBuilder.from(Material.NETHER_STAR) {
                        name = "<gold>What is Everbright?".parse().noItalic()
                        lore(*itemLore)
                    }
                    ) {
                        it.whoClicked.closeInventory()
                        it.whoClicked.playSound(SoundFactory.beeHiveSound)
                        (it.whoClicked as Player).msg("<yellow><bold>Luck<white>»</bold> <aqua>Every year at this time, a great star known as the Bright Star appears. And while not commonly, the star is known to fulfil the wishes of some who gaze upon it. With how few wishes are granted, it eventually became a tradition among my people to grant wishes ourselves in the form of gifts. Originally this was simply known as the Bright Festival. But after merging with the Beastfolk holiday of Everpeace, the season simply became known as Everbright. Fascinating, no?".parse())
                    })
                    bindItem('2', GuiItem(ItemBuilder.from(Material.STONE_AXE) {
                        name = "<gold>Wait, so this is two holidays in one?".parse().noItalic()
                        lore(*itemLore)
                    }
                    ) {
                        it.whoClicked.closeInventory()
                        it.whoClicked.playSound(SoundFactory.beeHiveSound)
                        (it.whoClicked as Player).msg("<yellow><bold>Luck<white>»</bold> <aqua>In a way, yes! Centuries ago, a great war had broken out. The goddess Gria created the Beastfolk to help end it, and their holiday of Everpeace is the anniversary of it’s end. It became a common rumor that a wish upon the Bright Star had been what ended the war. So after many centuries, the two holidays became one! A time of remembrance, appreciation, and giving back to those closest to you. It’s a wonderful time, and a great way to deal with the cold.".parse())
                    })
                    bindItem('3', GuiItem(ItemBuilder.from(Material.NETHERITE_SWORD) {
                        name = "<gold>Is there any new gear to earn?".parse().noItalic()
                        lore(*itemLore)
                    }
                    ) {
                        it.whoClicked.closeInventory()
                        it.whoClicked.playSound(SoundFactory.beeHiveSound)
                        (it.whoClicked as Player).msg("<yellow><bold>Luck<white>»</bold> <aqua>Why of course! Bug has put together a new one of her crates for the season, and it’s filled with all kinds of goodies. And like every year- Gigi made one of her wonderful hats. All you’ll need is a single Star Cookie surrounded by diamond blocks. ".parse())
                    })
                    bindItem('4', GuiItem(ItemBuilder.head {
                        model = 1
                        texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmNlZjlhYTE0ZTg4NDc3M2VhYzEzNGE0ZWU4OTcyMDYzZjQ2NmRlNjc4MzYzY2Y3YjFhMjFhODViNyJ9fX0="
                        name = "<gold>How do you get Winter Keys?".parse().noItalic()
                        lore(*itemLore)
                    }
                    ) {
                        it.whoClicked.closeInventory()
                        it.whoClicked.playSound(SoundFactory.beeHiveSound)
                        (it.whoClicked as Player).msg("<yellow><bold>Luck<white>»</bold> <aqua>I’m not quite sure how she does it. But Bug managed to work a few keys into every present underneath the great Evertree over there. Right click it and you should be able to find one with your name on it, once per day. There are also the Wishbanes…but I wouldn’t recommend attacking one so lightly.".parse())
                    })
                    bindItem('5', GuiItem(ItemBuilder.from(Material.BONE) {
                        name = "<gold>What is a Wishbane?".parse().noItalic()
                        lore(*itemLore)
                    }
                    ) {
                        it.whoClicked.closeInventory()
                        it.whoClicked.playSound(SoundFactory.beeHiveSound)
                        (it.whoClicked as Player).msg("<yellow><bold>Luck<white>»</bold> <aqua>Terrible creatures, born in the shadows of where Aeotola’s light nor the Bright Star reach. They appear in the coldest regions of the world, and attempt to steal the prized belongings of travelers. Avoid them if you can, traveler. Whatever treasures they may have is eclipsed by the danger they pose.".parse())
                    })
                    bindItem('6', GuiItem(ItemBuilder.from(Material.WRITABLE_BOOK) {
                        name = "<gold>Who are you?".parse().noItalic()
                        lore(*itemLore)
                    }
                    ) {
                        it.whoClicked.closeInventory()
                        it.whoClicked.playSound(SoundFactory.beeHiveSound)
                        (it.whoClicked as Player).msg("<yellow><bold>Luck<white>»</bold> <aqua>My name is Luck Vaeldara. I’m the head scholar of Loftside Academy. We’re a small school, but we take pride in our work. As for me, I enjoy helping the people of Terra celebrate the holiday. It’s in the spirit of giving back, and visiting lets me collect more research and assist my colleague Jim Coal in his own preparations.".parse())
                    })
                    bindItem('7', GuiItem(ItemBuilder.from(Material.ROSE_BUSH) {
                        name = "<gold>Jim Coal? Is he the next event host? Why is his name...".parse().noItalic()
                        lore(*itemLore)
                    }
                    ) {
                        it.whoClicked.closeInventory()
                        it.whoClicked.playSound(SoundFactory.beeHiveSound)
                        (it.whoClicked as Player).msg("<yellow><bold>Luck<white>»</bold> <aqua>Why is his name not similar to mine or the Goldfields? I’m not sure. It’s definitely strange how similar all of our names are. But I believe Jim had changed his for one reason or another. You’d have to ask him yourself.".parse())
                    })
                    bindItem('8', GUIFactory.exitButton)
                }
            )
        }
    }

    companion object {
        private lateinit var INSTANCE: GUI
        val luckGUI get() = INSTANCE.luckGUI
    }

}