package com.sylphmc.events.api.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import org.bukkit.Material

@Serializable
data class GUIFile(
    @SerialName("MainMenu")
    val mainMenu: GUIData,
    @SerialName("EventMenuBottom")
    val eventMenuBottom: LayoutData,
    @SerialName("EventMenus")
    val eventMenus: Map<String, GUIData>
) {

    @Serializable
    data class LayoutData(
        @SerialName("Layout")
        val layout: Set<String>,
        @SerialName("Replacements")
        val replacements: Map<Char, String>,
    )

    @Serializable
    data class GUIData(
        @SerialName("Item")
        val item: ItemData? = null,
        @SerialName("Title")
        val title: String,
        @SerialName("Layout")
        val layout: Set<String>? = null,
        @SerialName("GuiPlacements")
        val replacements: Map<Char, String>? = null,
    ) {

        @Serializable
        data class ItemData(
            @SerialName("Type")
            val type: Material,
            @SerialName("Name")
            val name: String,
            @SerialName("Lore")
            val lore: List<String>
        )

    }

}