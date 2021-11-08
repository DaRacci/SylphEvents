package me.racci.events.factories

import com.github.shynixn.mccoroutine.asyncDispatcher
import kotlinx.coroutines.withContext
import me.racci.events.SylphEvents
import me.racci.events.enums.HollowsEve2021
import me.racci.raccicore.utils.extensions.addRecipe
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe

object RecipeFactory {

    private val RECIPE_ARRAY = ArrayList<NamespacedKey>()

    suspend fun init() {

        hollowsEveRecipes()

    }

    fun close() {
        RECIPE_ARRAY.clear()
    }

    private suspend fun hollowsEveRecipes() = withContext(SylphEvents.instance.asyncDispatcher) {

        val candyCornIngotRecipe = ShapedRecipe(ItemFactory[HollowsEve2021.CANDY_CORN_INGOT, true], ItemFactory[HollowsEve2021.CANDY_CORN_INGOT])
        candyCornIngotRecipe.shape("ccc", "ccc", "ccc")
        candyCornIngotRecipe.setIngredient('c', ItemFactory[HollowsEve2021.CANDY_CORN])

        val ccH = ShapedRecipe(ItemFactory[HollowsEve2021.CANDY_CORN_HELMET, true], ItemFactory[HollowsEve2021.CANDY_CORN_HELMET])
        ccH.shape("ccc", "cac")
        ccH.setIngredient('c', ItemFactory[HollowsEve2021.CANDY_CORN_INGOT])
        ccH.setIngredient('a', Material.AIR)

        val ccC = ShapedRecipe(ItemFactory[HollowsEve2021.CANDY_CORN_CHESTPLATE, true], ItemFactory[HollowsEve2021.CANDY_CORN_CHESTPLATE])
        ccC.shape("cac", "ccc", "ccc")
        ccC.setIngredient('c', ItemFactory[HollowsEve2021.CANDY_CORN_INGOT])
        ccC.setIngredient('a', Material.AIR)

        val ccP = ShapedRecipe(ItemFactory[HollowsEve2021.CANDY_CORN_PANTS, true], ItemFactory[HollowsEve2021.CANDY_CORN_PANTS])
        ccP.shape("ccc", "cac", "cac")
        ccP.setIngredient('c', ItemFactory[HollowsEve2021.CANDY_CORN_INGOT])
        ccP.setIngredient('a', Material.AIR)

        val ccB = ShapedRecipe(ItemFactory[HollowsEve2021.CANDY_CORN_BOOTS, true], ItemFactory[HollowsEve2021.CANDY_CORN_BOOTS])
        ccB.shape("cac", "cac")
        ccB.setIngredient('c', ItemFactory[HollowsEve2021.CANDY_CORN_INGOT])
        ccB.setIngredient('a', Material.AIR)

        val heH = ShapedRecipe(ItemFactory[HollowsEve2021.HOLLOWS_EVE_HAT, true], ItemFactory[HollowsEve2021.HOLLOWS_EVE_HAT])
        heH.shape("nnn", "nhc", "ccc")
        heH.setIngredient('c', ItemFactory[HollowsEve2021.CANDY_CORN_INGOT])
        heH.setIngredient('n', ItemStack(Material.NETHERITE_SCRAP))
        heH.setIngredient('h', ItemFactory[HollowsEve2021.UNDEAD_MASK])

        val ciC = ShapedRecipe(ItemFactory[HollowsEve2021.CANDY_CORN, true], ItemFactory[HollowsEve2021.CANDY_CORN])
        ciC.shape("c")
        ciC.setIngredient('c', ItemFactory[HollowsEve2021.CANDY_CORN_INGOT])

        val recipes = arrayOf(candyCornIngotRecipe, ccH, ccC, ccP, ccB, heH, ciC)

        recipes.onEach(::addRecipe).map(ShapedRecipe::getKey).forEach(RECIPE_ARRAY::add)
    }
}