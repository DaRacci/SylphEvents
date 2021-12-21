package com.sylphmc.events.core.factories

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.decodeFromStream
import com.sylphmc.events.api.factories.EventItemFactory
import com.sylphmc.events.core.SylphEvents
import me.racci.raccicore.api.extensions.addRecipe
import me.racci.raccicore.api.utils.catch
import me.racci.raccicore.api.utils.catchAndReturn
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
        val s = "${this[value]._event.splitCapsThing(true).lowercase()}_${value.lowercase()}"
        Sylph.namespacedKey(s)
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
            val itemStack = item.value.buildItem(item.key)
//            println(itemStack)
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
                if(bukkitRecipe.shape.any{it.any{c->c == 'a'}}) {
                    bukkitRecipe.setIngredient('a', Material.AIR)
                }

                for((key, value) in recipe.replacements.entries) {
                    val ingredientItem = catchAndReturn<NullPointerException, Any>({
                        plugin.log.error("$value for char $key isn't a valid event item or Material Enum!")
                    }) {
                        itemData.items.getIgnoreCase(value)?.cachedItem
                            ?: Material.getMaterial(value)!!
                    } ?: continue
                    bukkitRecipe.setIngredient(key, ingredientItem)
                }
                addRecipe(bukkitRecipe)
                recipeKeys.add(bukkitRecipe.key)
            }
        }
    }

    override suspend fun onEnable() {
        INSTANCE = this
        decode()
        buildItems()
        recipeLoader()
    }

    override suspend fun onReload() {
        onEnable()
    }

//    override suspend fun onDisable() {
//        val f = File("${plugin.dataFolder}/Items.json")
//        val e = Sylph.jsonFormat.encodeToString(itemData)
//        catch<IOException> {
//            f.writeText(e)
//        }
//    }

    companion object {
        lateinit var INSTANCE: EventItemFactoryImpl
    }

}