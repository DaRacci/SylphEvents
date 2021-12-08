package me.racci.events.core.factories

import com.charleskorn.kaml.Yaml
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.Pane
import com.github.stefvanschie.inventoryframework.pane.util.Pattern
import kotlinx.serialization.ExperimentalSerializationApi
import me.racci.events.core.SylphEvents
import me.racci.events.api.data.GUIFile
import me.racci.events.api.factories.EventGUIFactory
import me.racci.events.api.factories.EventItemFactory
import me.racci.events.core.utils.ItemPane
import me.racci.raccicore.api.extensions.*
import me.racci.raccicore.api.utils.catch
import me.racci.sylph.api.factories.GUIFactory
import me.racci.sylph.api.factories.SoundFactory
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import org.bukkit.Material
import java.io.File

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
                ChestGui(gui.value.rows, gui.value.title) {
                    // Create the bottom menu bar
                    addPane(
                        PatternPane(
                            0,
                            gui.value.layout!!.size -1,
                            9, 1,
                            Pane.Priority.HIGHEST,
                            Pattern(*guiData.eventMenuBottom.layout.toTypedArray())
                        ) {
                            for(replacement in guiData.mainMenu.replacements!!) {
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
                    // Add all the event items
                    addPane(
                        ItemPane(
                            0, 0,
                            9,
                            gui.value.layout!!.size,
                            Pane.Priority.HIGH,
                        ) {
                            EventItemFactory
                            for(item in EventItemFactoryImpl.INSTANCE.itemData.items.values) {
                                addLast(
                                    GuiItem(item.cachedItem) {
                                        it.cancel()
                                        if(!it.whoClicked.hasPermission("sylph.events.give")) return@GuiItem
                                        val quantity = if(it.isShiftClick) 64 else 1
                                        if(it.whoClicked.inventory.hasSpace(item.cachedItem, quantity)) {
                                            it.whoClicked.msg("<red>No room in inventory!".parse())
                                            it.whoClicked.playSound(SoundFactory.errorSound)
                                        }
                                        it.whoClicked.inventory.addItem(item.cachedItem.asQuantity(quantity))
                                        it.whoClicked.playSound(SoundFactory.beeHiveSound)
                                    }
                                )
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
        mainMenu = ChestGui(guiData.mainMenu.rows, guiData.mainMenu.title) {
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
                            GuiItem(eventData.item!!.type.asItemStack() {
                                displayName(miniMessage().parse(eventData.item.name).noItalic())
                                lore(eventData.item.lore.map{m->miniMessage().parse(m).noItalic()})
                            }) {e->
                                e.cancel()
                                e.whoClicked.playSound(SoundFactory.beeHiveSound)
                                eventMenus[it.value]!!.show(e.whoClicked)
                            }
                        } else if(it.value == "ExitButton") {
                            bindItem(it.key, exitGUIButton)
                        }
                    }
                }
            )
        }
    }

    override suspend fun onEnable() {
        decode()
        buildMainMenu()
        buildEventMenus()
    }

    companion object {
        lateinit var INSTANCE: EventGUIFactoryImpl
    }

}

