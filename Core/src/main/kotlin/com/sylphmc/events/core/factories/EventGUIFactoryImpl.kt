package com.sylphmc.events.core.factories

import com.charleskorn.kaml.Yaml
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.Pane
import com.github.stefvanschie.inventoryframework.pane.util.Pattern
import kotlinx.serialization.ExperimentalSerializationApi
import com.sylphmc.events.api.data.GUIFile
import com.sylphmc.events.api.factories.EventGUIFactory
import com.sylphmc.events.api.factories.EventItemFactory
import com.sylphmc.events.core.SylphEvents
import com.sylphmc.events.core.utils.ItemPane
import me.racci.raccicore.api.extensions.*
import me.racci.raccicore.api.utils.catch
import me.racci.sylph.api.factories.GUIFactory
import me.racci.sylph.api.factories.SoundFactory
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import org.bukkit.Material
import java.io.File
import kotlin.math.ceil

@OptIn(ExperimentalSerializationApi::class)
class EventGUIFactoryImpl(
    override val plugin: SylphEvents,
): EventGUIFactory {

    override lateinit var mainMenu: ChestGui
    override val eventMenus = HashMap<String, ChestGui>()

    private lateinit var guiData: GUIFile
    private val exitGUIButton get() = GUIFactory.exitButton

    private fun buildEventMenus() {
        for(gui in guiData.eventMenus.entries) {
            eventMenus.computeIfAbsent(gui.key) {
                val rows = ceil(EventItemFactoryImpl.INSTANCE.itemData.items.values.filter{it._event == gui.key}.size.toDouble() / 9).toInt().takeIf{it > 0} ?: 1
                ChestGui(rows + 1, gui.value.title) {
                    // Add all the event items
                    addPane(
                        ItemPane(
                            0, 0,
                            // This is needed to not cause issues with arrays not having the right item amount
                            9,
                            rows,
                            Pane.Priority.HIGH,
                        ) {
                            for(item in EventItemFactoryImpl.INSTANCE.itemData.items.values) {
                                if(item._event != gui.key) continue
                                addLast(
                                    GuiItem(item.cachedItem.clone()) {
                                        it.cancel()
                                        if(!it.whoClicked.hasPermission("sylph.events.give")) return@GuiItem
                                        val quantity = if(it.isShiftClick) item.cachedItem.maxStackSize else 1
                                        it.whoClicked.inventory.addItem(item.cachedItem.asQuantity(quantity))
                                        it.whoClicked.playSound(SoundFactory.beeHiveSound)
                                    }
                                )
                            }
                        }
                    )
                    // Create the bottom menu bar
                    addPane(
                        PatternPane(
                            x = 0,
                            y = rows,
                            length = guiData.eventMenuBottom.layout.first().length,
                            height = guiData.eventMenuBottom.layout.size,
                            priority = Pane.Priority.HIGHEST,
                            Pattern(*guiData.eventMenuBottom.layout.toTypedArray())
                        ) {
                            for(replacement in guiData.eventMenuBottom.replacements) {
                                if(Material.getMaterial(replacement.value.uppercase()) != null) {
                                    bindItem(replacement.key, GuiItem(Material.getMaterial(replacement.value.uppercase())!!.asItemStack {
                                        displayName(Component.empty())
                                    }) {e-> e.cancel()})
                                } else if(replacement.value == "ExitButton") {
                                    bindItem(replacement.key, exitGUIButton)
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    private fun decode() {
        val file = File("${plugin.dataFolder}/GUI.yml")
        catch<Exception>({throw Exception("There was an error adding the default GUI file. $it")}) {
            if(!file.exists()) {
                plugin.saveResource("GUI.yml", false)
            }
        }
        guiData = file.inputStream().use {
            Yaml.default.decodeFromStream(GUIFile.serializer(), it)
        }
    }

    private fun buildMainMenu() {
        mainMenu = ChestGui((guiData.eventMenus.size / 9).takeIf {it > 0} ?: 1, guiData.mainMenu.title) {
            addPane(
                PatternPane(
                    0, 0,
                    guiData.mainMenu.layout!!.first().length,
                    guiData.mainMenu.layout!!.size,
                    Pane.Priority.HIGH,
                    Pattern(*guiData.mainMenu.layout!!.toTypedArray())
                ) {
                    guiData.mainMenu.replacements!!.forEach {
                        if(Material.getMaterial(it.value.uppercase()) != null) {
                            bindItem(it.key, GuiItem(Material.getMaterial(it.value.uppercase())!!.asItemStack {
                                displayName(Component.empty())
                            }) {e->e.cancel()})
                        } else if(guiData.eventMenus.containsKey(it.value)) {
                            val eventData = guiData.eventMenus[it.value]!!
                            bindItem(it.key, GuiItem(eventData.item!!.type.asItemStack() {
                                displayName(miniMessage().parse(eventData.item.name).noItalic())
                                lore(eventData.item.lore.map{m->miniMessage().parse(m).noItalic()})
                            }) {e->
                                e.cancel()
                                e.whoClicked.playSound(SoundFactory.beeHiveSound)
                                eventMenus[it.value]!!.show(e.whoClicked)
                            })
                        } else if(it.value == "ExitButton") {
                            bindItem(it.key, exitGUIButton)
                        }
                    }
                }
            )
        }
    }

    override suspend fun onEnable() {
        INSTANCE = this
        decode()
        buildMainMenu()
        buildEventMenus()
    }

    companion object {
        lateinit var INSTANCE: EventGUIFactoryImpl
    }

}

