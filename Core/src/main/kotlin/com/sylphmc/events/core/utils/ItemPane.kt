package com.sylphmc.events.core.utils

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.InventoryComponent
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui
import com.github.stefvanschie.inventoryframework.pane.Pane
import me.racci.sylph.api.factories.GUIFactory
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent

inline fun ItemPane(
    x: Int,
    y: Int,
    length: Int,
    height: Int,
    priority: Pane.Priority,
    unit: ItemPane.() -> Unit
) = ItemPane(x,y,length,height,priority).also(unit)

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
        for(x in 0 until length) {
            for(y in 0 until height) {
                val index = x + (y * 9)
                val item = if(index >= items.size) {
                    GUIFactory.borderFiller[Material.PURPLE_STAINED_GLASS_PANE]!!
                } else items[index]
                if(!item.isVisible) continue
                val finalRow = getY() + y + paneOffsetY
                val finalColumn = getX() + x + paneOffsetX
                inventoryComponent.setItem(item, finalColumn, finalRow)
            }
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