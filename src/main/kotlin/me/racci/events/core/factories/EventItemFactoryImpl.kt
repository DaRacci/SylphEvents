package me.racci.events.core.factories

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.decodeFromStream
import me.racci.events.core.SylphEvents
import me.racci.events.api.factories.EventItemFactory
import me.racci.raccicore.api.extensions.addRecipe
import me.racci.raccicore.api.utils.catch
import me.racci.raccicore.api.utils.collections.CollectionUtils.getIgnoreCase
import me.racci.sylph.api.utils.splitCapsThing
import me.racci.sylph.core.Sylph
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.persistence.PersistentDataType
import java.io.File
import java.io.IOException

@OptIn(ExperimentalSerializationApi::class)
class EventItemFactoryImpl(
    override val plugin: SylphEvents
): EventItemFactory {

    override lateinit var itemData: EventItemFactory.ItemFactoryData

    override val itemKeyCache = HashMap<String, NamespacedKey>()

    private val recipeKeys = ArrayList<NamespacedKey>()

    operator fun get(
        value: String
    ) = itemData.items.getIgnoreCase(value)!!

    operator fun get(
        value: String,
        key: Boolean,
    ) = itemKeyCache.computeIfAbsent(value) {
        Sylph.namespacedKey("${this[value]._event.splitCapsThing().lowercase()}_${value.lowercase()}")
    }

    private fun decode() {
        val file = File("${plugin.dataFolder}/Items.json")
        if(!file.exists()) file.createNewFile()
        itemData = file.inputStream().use {
            Sylph.jsonFormat.decodeFromStream(it)
        }
    }

    private fun ShapedRecipe.setIngredient(char: Char, ingredient: Any) {
        when(ingredient) {
            is ItemStack    -> setIngredient(char, ingredient)
            is Material -> setIngredient(char, ingredient)
            else    -> assert(false) {"Item must be an ItemStack or Material"}
        }
    }

    private fun buildItems() {
        for(item in itemData.items) {
            val itemStack = item.value.buildItem()
            itemStack.persistentDataContainer[
                    this[item.key, true],
                    PersistentDataType.BYTE
            ] = 1.toByte()
            item.value.cachedItem = itemStack
        }
    }

    private fun recipeLoader() {
        for(item in itemData.items) {
            if(item.value._recipe != null) {
                val recipe = item.value._recipe!!
                assert(recipe.rows.isNotEmpty())
                val bukkitRecipe = ShapedRecipe(this[item.key, true], item.value.cachedItem)
                bukkitRecipe.shape(*recipe.rows.toTypedArray())
                bukkitRecipe.setIngredient('a', Material.AIR)
                recipe.replacements.forEach {
                    val ingredientItem: Any = itemData.items.getIgnoreCase(it.value)?.cachedItem
                        ?: Material.getMaterial(it.value)!!
                    bukkitRecipe.setIngredient(it.key, ingredientItem)
                }
                addRecipe(bukkitRecipe)
                recipeKeys.add(bukkitRecipe.key)
            }
        }
    }

    override suspend fun onEnable() {
        decode()
        buildItems()
        recipeLoader()
    }

    override suspend fun onReload() {
        onEnable()
    }

    override suspend fun onDisable() {
        val f = File("${plugin.dataFolder}/Items.json")
        val e = Sylph.jsonFormat.encodeToString(itemData)
        catch<IOException> {
            f.writeText(e)
        }
    }

    companion object {
        lateinit var INSTANCE: EventItemFactoryImpl
    }

}