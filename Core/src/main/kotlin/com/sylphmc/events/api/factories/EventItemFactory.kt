package com.sylphmc.events.api.factories

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.sylphmc.events.api.data.ItemStackData
import com.sylphmc.events.core.SylphEvents
import com.sylphmc.events.core.factories.EventItemFactoryImpl
import me.racci.raccicore.api.lifecycle.LifecycleListener
import org.bukkit.NamespacedKey

interface EventItemFactory: LifecycleListener<SylphEvents> {

    /**
     * Map of event items, now with strings instead of enums.
     */
    var itemData: ItemFactoryData

    /**
     * Cached map of [NamespacedKey]'s for each item based on their name and event.
     */
    val itemKeyCache: HashMap<String, NamespacedKey>

    @Serializable
    data class ItemFactoryData(
        @SerialName("Items")
        val items: HashMap<String, ItemStackData>
    )

    companion object {
        private val provider get() = EventItemFactoryImpl.INSTANCE

        operator fun get(
            value: String
        ) = provider[value]

        operator fun get(
            value: String,
            key: Boolean,
        ) = provider[value, key]

    }

}