package me.racci.events.factories

import com.destroystokyo.paper.MaterialTags
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.InventoryComponent
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui
import com.github.stefvanschie.inventoryframework.pane.Pane
import com.github.stefvanschie.inventoryframework.pane.util.Pattern
import me.racci.events.SylphEvents
import me.racci.raccicore.api.builders.ItemBuilder
import me.racci.raccicore.api.extensions.PatternPane
import me.racci.raccicore.api.lifecycle.LifecycleListener
import me.racci.raccicore.api.utils.primitive.colouredTextOf
import me.racci.sylph.api.factories.SoundFactory
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import kotlin.math.floor
import kotlin.properties.Delegates

class GUI(
    override val plugin: SylphEvents
): LifecycleListener<SylphEvents> {

    private val exitGUIButton = GuiItem(ItemBuilder.from(Material.BARRIER) {
        name = (colouredTextOf("&cClose this menu"))
        lore = colouredTextOf("&f&l» &bClick &f&l« &eclose the menu.")
    }
    ) {
        it.whoClicked.closeInventory()
        it.whoClicked.playSound(SoundFactory.beeHiveSound, Sound.Emitter.self())
    }

    private val borderFiller = HashMap<Material, GuiItem>().apply {
        for(mat in MaterialTags.GLASS_PANES.values) {
            this[mat] = GuiItem(ItemBuilder.from(mat) {
                name = Component.empty()
            }).apply {setAction {it.isCancelled = true}}
        }
    }

    var mainMenuGUI         : ChestGui by Delegates.notNull()
    private var hollowsEve2021GUI   : ChestGui by Delegates.notNull()

    override suspend fun onEnable() {
        mainMenuGUI = ChestGui(1, "Event Item GUI").apply {
            addPane(PatternPane(
                0, 0,
                9, 1,
                Pane.Priority.HIGH,
                Pattern("100000008")
            ) {
                bindItem('0', borderFiller[Material.GRAY_STAINED_GLASS_PANE]!!)
                bindItem('1', GuiItem(ItemBuilder.from(Material.ROTTEN_FLESH) {
                    name = miniMessage().parse("gradient:#6fe461:#3f473f><bold>HollowsEve2021</gradient></bold>").decoration(TextDecoration.ITALIC, false)
                    lore {
                        listOf(
                            Component.empty(),
                            miniMessage().parse("<white><bold>»</bold> <aqua>Click</aqua> <bold>«</bold> <yellow>to view gradient:#6fe461:#3f473f><bold>HollowsEve2021</gradient></bold> <yellow>Items.").decoration(TextDecoration.ITALIC,false)
                        )
                    }
                }){
                    it.isCancelled = true
                    it.whoClicked.playSound(SoundFactory.beeHiveSound, Sound.Emitter.self())
                    hollowsEve2021GUI.show(it.whoClicked)
                })
                bindItem('8', exitGUIButton)
            })
        }
        hollowsEve2021GUI = ChestGui(4, "HollowsEve 2021 Items").apply {
            addPane(ItemPane(
                0, 0,
                9, 4,
                Pane.Priority.HIGH,
            ).apply {
                for((enum,item) in ItemFactory.hollowsEve2021Items.entries) {
                    addItem(
                        GuiItem(item) {
                            it.isCancelled = true
                            if(!it.whoClicked.hasPermission("sylph.events.give")) return@GuiItem
                            var slot = it.whoClicked.inventory.first(item)
                            if(slot == -1) slot = it.whoClicked.inventory.firstEmpty()
                            if(slot == -1) it.whoClicked.sendMessage("No room in inventory!")
                            val quantity = if(it.isShiftClick) 64 else 1
                            it.whoClicked.inventory.setItem(slot, item.asQuantity(quantity))
                            it.whoClicked.playSound(SoundFactory.beeHiveSound)
                    }, enum.ordinal)
                }
            })
            addPane(PatternPane(
                0, 0,
                9, 3,
                Pane.Priority.HIGHEST,
                Pattern(
                    "000000000",
                    "000000000",
                    "000000000",
                    "121282121"
                )) {
                bindItem('1', borderFiller[Material.GRAY_STAINED_GLASS_PANE]!!)
                bindItem('2', borderFiller[Material.WHITE_STAINED_GLASS_PANE]!!)
                bindItem('8', exitGUIButton)
                setOnClick{it.isCancelled = true}
            })
        }
    }

    companion object {
        private lateinit var INSTANCE: GUI
        val mainMenuGUI get() = INSTANCE.mainMenuGUI
    }

}

class ItemPane(
    x: Int = 0,
    y: Int = 0,
    length: Int,
    height: Int,
    priority: Priority,
): Pane(x,y,length,height,priority) {

    private val items = ArrayList<GuiItem>(length * height)

    fun insert(guiItem: GuiItem, slot: Int) {
        items.add(slot, guiItem)
    }
    fun take(slot: Int) {
        items.removeAt(slot)
    }
    fun addLast(guiItem: GuiItem) {
        items.add(guiItem)
    }

    override fun display(
        inventoryComponent: InventoryComponent,
        paneOffsetX: Int,
        paneOffsetY: Int,
        maxLength: Int,
        maxHeight: Int,
    ) {
        items.forEachIndexed { slot, item->
            val x = if(slot >= 9) slot - 9 else slot
            val y = if(slot >= 9) floor(slot.toDouble() / 9).toInt() else 0
            inventoryComponent.setItem(item, x, y)
        }
    }

    fun addItem(item: GuiItem, slot: Int) {
        items.add(slot, item)
    }


    override fun click(
        gui: Gui,
        inventoryComponent: InventoryComponent,
        event: InventoryClickEvent,
        slot: Int,
        paneOffsetX: Int,
        paneOffsetY: Int,
        maxLength: Int,
        maxHeight: Int,
    ): Boolean {

        val length = length.coerceAtMost(maxLength)
        val height = height.coerceAtMost(maxHeight)

        val adjustedSlot = slot - (getX() + paneOffsetX) - inventoryComponent.length * (getY() + paneOffsetY)

        val x = adjustedSlot % inventoryComponent.length
        val y = adjustedSlot / inventoryComponent.length

        //this isn't our item
        if(x < 0 || x >= length || y < 0 || y >= height) {
            return false
        }

        callOnClick(event)

        val itemStack = event.currentItem ?: return false

        val clickedItem: GuiItem = findMatchingItem(items, itemStack)
                                   ?: return false

        clickedItem.callAction(event)

        return true
    }

    override fun getItems(): MutableCollection<GuiItem> {
        return items.toMutableList()
    }

    override fun getPanes(): MutableCollection<Pane> {
        return HashSet()
    }

    override fun clear() {
        items.clear()
    }
}