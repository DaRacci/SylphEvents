package com.sylphmc.events.api.data

import com.sylphmc.events.api.factories.EventItemFactory
import de.tr7zw.changeme.nbtapi.NBTItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import me.racci.raccicore.api.builders.ItemBuilder
import me.racci.raccicore.api.extensions.noItalic
import me.racci.raccicore.api.serializers.UUIDSerializer
import me.racci.sylph.api.data.serializers.AttributeModifierSerializer
import me.racci.sylph.api.data.serializers.EnchantmentSerializer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

@Serializable
data class ItemStackData(
    @SerialName("Event")
    var _event: String,
    @SerialName("Type")
    var _type: Material,
    @SerialName("Name")
    var _name: String? = null,
    @SerialName("Model")
    var _model: Int? = null,
    @SerialName("SkullData")
    var _skullData: SkullData? = null,
    @SerialName("Lore")
    var _lore: List<String>? = null,
    @SerialName("ItemFlags")
    var _itemFlags: Array<out ItemFlag>? = null,
    @SerialName("Enchants")
    var _enchants: List<Pair<@Serializable(with = EnchantmentSerializer::class) Enchantment, Int>>? = null,
    @SerialName("AttributeModifiers")
    var _attributeModifiers: Map<String, @Serializable(with = AttributeModifierSerializer::class) AttributeModifier>? = null,
    @SerialName("NBT")
    var _nbt: Map<String, Boolean>? = null,
    @SerialName("Recipe")
    var _recipe: Recipe? = null
) {

    fun buildItem(key: String): ItemStack {
        var itemStack = ItemBuilder.from(_type) {
            name = _name?.let {miniMessage().parse(it).noItalic()}
            model = _model
            _itemFlags?.let {addFlag(*it)}
            lore {
                if(_lore.isNullOrEmpty()) {
                    emptyList()
                } else _lore!!.map {string->
                    if(string.isEmpty()) {
                        Component.empty()
                    } else miniMessage().parse(string).noItalic()
                }
            }
            _enchants?.map{it.first to it.second}?.let {enchant(*it.toTypedArray())}
            _attributeModifiers?.forEach {
                meta.addAttributeModifier(Attribute.valueOf(it.key), it.value)
            }
        }
        val nbtItem = NBTItem(itemStack)
        nbtItem.addCompound("PublicBukkitValues")
        nbtItem.getCompound("PublicBukkitValues").setBoolean(EventItemFactory[key, true].toString(), true)
        if(_skullData != null) {
            val skull = nbtItem.addCompound("SkullOwner")
            skull.setUUID("Id", _skullData?.owner)
            val texture = skull.addCompound("Properties").getCompoundList("textures").addCompound()
            texture.setString("Value", _skullData?.texture)
        }
        if(_nbt != null) {
            _nbt!!.forEach{nbtItem.getCompound("PublicBukkitValues").setBoolean(it.key, it.value)}
        }
        itemStack = nbtItem.item
        println("${itemStack.persistentDataContainer.keys}")
        return itemStack
    }

    @Transient
    lateinit var cachedItem: ItemStack

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(javaClass != other?.javaClass) return false

        other as ItemStackData

        if(_type != other._type) return false
        if(_name != other._name) return false
        if(_model != other._model) return false
        if(_skullData != other._skullData) return false
        if(_lore != other._lore) return false
        if(!_itemFlags.contentEquals(other._itemFlags)) return false
        if(_enchants != other._enchants) return false
        if(_attributeModifiers != other._attributeModifiers) return false
        if(_nbt != other._nbt) return false
        if(cachedItem != other.cachedItem) return false

        return true
    }

    override fun hashCode(): Int {
        var result = _type.hashCode()
        result = 31 * result + (_name?.hashCode() ?: 0)
        result = 31 * result + (_model ?: 0)
        result = 31 * result + (_skullData?.hashCode() ?: 0)
        result = 31 * result + _lore.hashCode()
        result = 31 * result + _itemFlags.contentHashCode()
        result = 31 * result + _enchants.hashCode()
        result = 31 * result + _attributeModifiers.hashCode()
        result = 31 * result + (_nbt?.hashCode() ?: 0)
        result = 31 * result + cachedItem.hashCode()
        return result
    }

}

@Serializable
data class SkullData(
    @SerialName("Owner") val owner: @Serializable(with = UUIDSerializer::class) UUID = UUID.randomUUID(),
    @SerialName("Texture") val texture: String,
)

@Serializable
data class Recipe(
    @SerialName("Pattern")
    val rows: List<String>,
    @SerialName("Replacements")
    val replacements: HashMap<Char, String>
)
