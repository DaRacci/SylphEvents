package me.racci.events.factories

import com.willfp.ecoenchants.enchantments.EcoEnchants
import me.racci.events.enums.HollowsEve2021
import me.racci.raccicore.builders.ItemBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.EnumMap
import java.util.UUID

object ItemFactory {

    internal val hollowsEve2021Items = EnumMap<HollowsEve2021, ItemStack>(HollowsEve2021::class.java)

    private val namespaces : EnumMap<HollowsEve2021, NamespacedKey> = object : EnumMap<HollowsEve2021, NamespacedKey>(HollowsEve2021::class.java) {init{
        HollowsEve2021.values().forEach {this[it] = NamespacedKey("sylph", "hollows_eve_2021_${it.name.lowercase()}")}
    }}

    operator fun get(enum: HollowsEve2021) = hollowsEve2021Items[enum]!!

    operator fun get(enum: HollowsEve2021, getKey: Boolean) = namespaces[enum]!!


    fun init() {
        hollowsEve2021()
    }

    private fun hollowsEve2021() {
        fun gradient(name: String, bold: Boolean = true) =
                when(bold) {
                    true -> MiniMessage.get().parse("<gradient:#6fe461:#3f473f><bold>$name</gradient>").decoration(
                        TextDecoration.ITALIC, false)
                    false -> MiniMessage.get().parse("<gradient:#6fe461:#3f473f>$name</gradient>").decoration(TextDecoration.ITALIC, false)
                }
        val resolver: java.util.function.Function<String, ComponentLike> = java.util.function.Function<String, ComponentLike> { name ->
            when(name.lowercase()) {
                "hollowseve" -> MiniMessage.get().parse("<gradient:#6fe461:#3f473f>Hollow's Eve</gradient>")
                "chuck" -> MiniMessage.get().parse("<white>- <green><bold>Chuck")
                "gigi" -> MiniMessage.get().parse("<white>- <yellow><bold>Gigi")
                "unknown" -> MiniMessage.get().parse("<white>- <grey><bold>Unknown")
                else -> null
            }
        }


        var universalLore: List<Component> = listOf(
            Component.empty(),
            MiniMessage.get().parse("<white>Part of the <#6fe461>Hollow's Eve <white>event.").decoration(TextDecoration.ITALIC, false),
        )


        hollowsEve2021Items[HollowsEve2021.CANDY_CORN] = ItemBuilder.from(Material.GOLD_NUGGET) {
            name = gradient("Candy Corn")
            lore {
                listOf(
                    MiniMessage.get().parse("<yellow>\"People swear that it's edible")
                        .decoration(TextDecoration.ITALIC, false).asComponent(),
                    MiniMessage.get().parse("<yellow>but sometimes it's hard as a rock.")
                        .decoration(TextDecoration.ITALIC, false).asComponent(),
                    MiniMessage.builder().placeholderResolver(resolver).build().parse("<yellow>...wonder if it has another use...\" <Chuck>")
                        .decoration(TextDecoration.ITALIC, false),
                    Component.empty(),
                    MiniMessage.get().parse("<white>» <green>Used to make Candy Corn Ingots")
                        .decoration(TextDecoration.ITALIC, false).asComponent(),
                ).plus(universalLore)
            }
            pdc {
                set(namespaces[HollowsEve2021.CANDY_CORN]!!, PersistentDataType.BYTE, 1.toByte())
            }
        }



        hollowsEve2021Items[HollowsEve2021.CANDY_CORN_INGOT] = ItemBuilder.from(Material.GOLD_INGOT) {
            name = gradient("Candy Corn Ingot")
            lore {
                listOf(
                    MiniMessage.get().parse("<yellow>\"I don't think I can buy that off you.").decoration(TextDecoration.ITALIC, false),
                    MiniMessage.builder().placeholderResolver(resolver).build().parse("<yellow>Maybe you could make something?\" <Gigi>").decoration(TextDecoration.ITALIC, false),
                    Component.empty(),
                    MiniMessage.get().parse("<white>» <green>Used to make Candy Corn Armour").decoration(TextDecoration.ITALIC, false),
                ).plus(universalLore)
            }
        }

        universalLore = listOf(
            Component.empty(),
            MiniMessage.get().parse("<white>» <green>Wear the full set for a massive speed boost").decoration(
                TextDecoration.ITALIC, false),
            Component.empty(),
            MiniMessage.get().parse("<white>Part of the <#6fe461>Hollow's Eve <white>event.").decoration(TextDecoration.ITALIC, false),
        )

        hollowsEve2021Items[HollowsEve2021.CANDY_CORN_HELMET] = ItemBuilder.from(Material.GOLDEN_HELMET) {
            name = gradient("Candy Corn Helmet")
            lore {
                listOf(
                    Component.empty(),
                    MiniMessage.get().parse("<yellow>A Helmet made from candy corn").decoration(TextDecoration.ITALIC, false),
                    MiniMessage.get().parse("<yellow>You can feel the uncontrolled sugar.").decoration(TextDecoration.ITALIC, false),
                ).plus(universalLore)
            }
        }

        hollowsEve2021Items[HollowsEve2021.CANDY_CORN_CHESTPLATE] = ItemBuilder.from(Material.GOLDEN_CHESTPLATE) {
            name = gradient("Candy Corn Chestplate")
            lore {
                listOf(
                    Component.empty(),
                    MiniMessage.get().parse("<yellow>A Chestplate made from candy corn").decoration(TextDecoration.ITALIC, false),
                    MiniMessage.get().parse("<yellow>You can feel the uncontrolled sugar.").decoration(TextDecoration.ITALIC, false),
                ).plus(universalLore)
            }
        }

        hollowsEve2021Items[HollowsEve2021.CANDY_CORN_PANTS] = ItemBuilder.from(Material.GOLDEN_LEGGINGS) {
            name = gradient("Candy Corn Pants")
            lore {
                listOf(
                    Component.empty(),
                    MiniMessage.get().parse("<yellow>Pants made from candy corn").decoration(TextDecoration.ITALIC, false),
                    MiniMessage.get().parse("<yellow>You can feel the uncontrolled sugar.").decoration(TextDecoration.ITALIC, false),
                ).plus(universalLore)
            }
        }

        hollowsEve2021Items[HollowsEve2021.CANDY_CORN_BOOTS] = ItemBuilder.from(Material.GOLDEN_BOOTS) {
            name = gradient("Candy Corn Boots")
            lore {
                listOf(
                    Component.empty(),
                    MiniMessage.get().parse("<yellow>Boots made from candy corn").decoration(TextDecoration.ITALIC, false),
                    MiniMessage.get().parse("<yellow>You can feel the uncontrolled sugar.").decoration(TextDecoration.ITALIC, false),
                ).plus(universalLore)
            }
        }

        universalLore = listOf(
            Component.empty(),
            MiniMessage.get().parse("<white>Part of the <#6fe461>Hollow's Eve <white>event.").decoration(TextDecoration.ITALIC, false),
        )

        hollowsEve2021Items[HollowsEve2021.ONCE_PREY_BOOTS] = ItemBuilder.from(Material.NETHERITE_BOOTS) {
            name = gradient("Once-Prey Boots")
            enchant(
                Enchantment.PROTECTION_ENVIRONMENTAL to 7,
                Enchantment.DURABILITY to 5,
                Enchantment.SOUL_SPEED to 4,
                Enchantment.MENDING to 1,
            )
            lore {
                listOf(
                    Component.empty(),
                    MiniMessage.get().parse("<yellow>\"The undead think we have no choice").decoration(TextDecoration.ITALIC, false),
                    MiniMessage.get().parse("<yellow>but to fear them. Let's show them just").decoration(TextDecoration.ITALIC, false),
                    MiniMessage.builder().placeholderResolver(resolver).build().parse("how wrong they are.\" <Chuck>").decoration(
                        TextDecoration.ITALIC, false),
                ).plus(universalLore)
            }
        }

        hollowsEve2021Items[HollowsEve2021.UMBERWOOD_SHARDTHROWER] = ItemBuilder.from(Material.CROSSBOW) {
            name = gradient("Umberwood Shardthrower")
            enchant(
                EcoEnchants.BUCKSHOT to 3,
                Enchantment.PIERCING to 4,
                Enchantment.DURABILITY to 5,
                EcoEnchants.REVENANT to 6,
            )
            lore {
                listOf(
                    Component.empty(),
                    MiniMessage.get().parse("<yellow>\"Ah, one of my old crossbows.").decoration(TextDecoration.ITALIC, false),
                    MiniMessage.get().parse("<yellow>Turns arrows into shards of pure silver.").decoration(TextDecoration.ITALIC, false),
                    MiniMessage.builder().placeholderResolver(resolver).build().parse("Let's see you put it to use\" <Chuck>").decoration(
                        TextDecoration.ITALIC, false),
                ).plus(universalLore)
            }
        }

        hollowsEve2021Items[HollowsEve2021.GNELL] = ItemBuilder.from(Material.NETHERITE_HOE) {
            name = gradient("Gnell")
            enchant(
                EcoEnchants.CLEAVE to 3,
                EcoEnchants.LEECHING to 3,
                EcoEnchants.NECROTIC to 5,
                EcoEnchants.CRITICALS to 3,
                Enchantment.DAMAGE_ALL to 10,
                Enchantment.DURABILITY to 5,
                Enchantment.MENDING to 1,
            )
            lore {
                listOf(
                    Component.empty(),
                    MiniMessage.get().parse("<yellow>\"When the soul sands dry, and the innocent sing.").decoration(
                        TextDecoration.ITALIC, false),
                    MiniMessage.builder().placeholderResolver(resolver).build().parse("<yellow>The undead shall hear me. And hear my ring\" <Unknown>").decoration(
                        TextDecoration.ITALIC, false),
                ).plus(universalLore)
            }
        }

        hollowsEve2021Items[HollowsEve2021.HEADSMANS_HATCHET] = ItemBuilder.from(Material.NETHERITE_AXE) {
            name = gradient("Headsman's Hatchet")
            enchant(
                EcoEnchants.BEHEADING to 5,
                EcoEnchants.LEECHING to 3,
                EcoEnchants.BLEED to 7,
                Enchantment.DAMAGE_ALL to 7,
                Enchantment.DURABILITY to 5,
                Enchantment.MENDING to 1,
            )
            lore {
                listOf(
                    Component.empty(),
                    MiniMessage.get().parse("<yellow>\"A grim tradition. In parts of the mountains,").decoration(
                        TextDecoration.ITALIC, false),
                    MiniMessage.get().parse("<yellow>it was thought being buried headless could").decoration(TextDecoration.ITALIC, false),
                    MiniMessage.get().parse("<yellow>save you. I hope this axe finds better purpose").decoration(
                        TextDecoration.ITALIC, false),
                    MiniMessage.builder().placeholderResolver(resolver).build().parse("<yellow>now.\" <Chuck>").decoration(
                        TextDecoration.ITALIC, false),
                ).plus(universalLore)
            }
        }

        hollowsEve2021Items[HollowsEve2021.GRAVE_DIGGER] = ItemBuilder.from(Material.NETHERITE_PICKAXE) {
            name = gradient("Grave Digger")
            enchant(
                EcoEnchants.INSTANTANEOUS to 8,
                EcoEnchants.TELEKINESIS to 1,
                Enchantment.DIG_SPEED to 7,
                Enchantment.DURABILITY to 5,
                Enchantment.MENDING to 1,
            )
            lore {
                listOf(
                    Component.empty(),
                    MiniMessage.get().parse("<yellow>\"Gets cold enough, the hardest soil can").decoration(TextDecoration.ITALIC, false),
                    MiniMessage.builder().placeholderResolver(resolver).build().parse("<yellow>feel like the average slate.\" - Chuck").decoration(
                        TextDecoration.ITALIC, false),
                ).plus(universalLore)
            }
        }

        hollowsEve2021Items[HollowsEve2021.CANDY_PAIL] = ItemBuilder.from(Material.BUCKET) {
            name = gradient("Candy Pail")
            glow
            lore {
                listOf(
                    MiniMessage.get().parse("<yellow>\"It's an old tradition to go around in costume,").decoration(TextDecoration.ITALIC, false),
                    MiniMessage.builder().placeholderResolver(resolver).build().parse("<yellow>asking for candy. Go on, give it a try\" <Chuck>").decoration(TextDecoration.ITALIC, false),
                    Component.empty(),
                    MiniMessage.get().parse("<yellow>Right-click on <green><bold>Chuck</bold><yellow>, <yellow><bold>Gigi</bold><yellow>, or <gold><bold>Bug</bold> <yellow>to get candy!").decoration(TextDecoration.ITALIC, false),
                )
            }
        }

        hollowsEve2021Items[HollowsEve2021.GUMMY_FISH] = ItemBuilder.from(Material.TROPICAL_FISH) {
            name = gradient("Gummy Fish")
            glow
            lore {
                listOf(
                    MiniMessage.get().parse("<yellow>A gummy fish! Come in <gold>orange<yellow>, <red>cherry<yellow>, and <dark_purple>grape").decoration(TextDecoration.ITALIC, false),
                    MiniMessage.get().parse("<yellow>Popular among young children and Merlings").decoration(TextDecoration.ITALIC, false),
                    Component.empty(),
                    MiniMessage.get().parse("<white>» <green>Grants bonus food and speed on consumption").decoration(TextDecoration.ITALIC, false),
                )
            }
        }

        hollowsEve2021Items[HollowsEve2021.CANDIED_BERRIES] = ItemBuilder.from(Material.SWEET_BERRIES) {
            name = gradient("Candied Berries")
            glow
            lore {
                listOf(
                    MiniMessage.get().parse("<yellow>Native berries with a candy coating!").decoration(TextDecoration.ITALIC, false),
                    MiniMessage.get().parse("<yellow>Favoured candy of the Fae and Beastfolk.").decoration(TextDecoration.ITALIC, false),
                    Component.empty(),
                    MiniMessage.get().parse("<white>» <green>Grants bonus food and speed on consumption").decoration(TextDecoration.ITALIC, false),
                )
            }
        }

        hollowsEve2021Items[HollowsEve2021.BOWL_OF_CHOCOLATES] = ItemBuilder.from(Material.SUSPICIOUS_STEW) {
            name = gradient("Bowl of Chocolates")
            glow
            lore {
                listOf(
                    MiniMessage.get().parse("<yellow>A bowl of candy coated chocolates!").decoration(TextDecoration.ITALIC, false),
                    MiniMessage.get().parse("<yellow>Invented by humanity for long treks.").decoration(TextDecoration.ITALIC, false),
                    Component.empty(),
                    MiniMessage.get().parse("<white>» <green>Grants bonus food and speed on consumption").decoration(TextDecoration.ITALIC, false),
                )
            }
        }

        hollowsEve2021Items[HollowsEve2021.ONCE_HOLY_SABER] = ItemBuilder.from(Material.NETHERITE_SWORD) {
            name = gradient("Once-Holy Saber")
            enchant(
                Enchantment.DAMAGE_UNDEAD to 7,
                EcoEnchants.MISSILE to 4,
                Enchantment.FIRE_ASPECT to 5,
                Enchantment.DURABILITY to 5,
                Enchantment.MENDING to 1,
            )
            lore {
                listOf(
                    Component.empty(),
                    MiniMessage.get().parse("<yellow>The sword of a once great hero.").decoration(TextDecoration.ITALIC, false),
                    MiniMessage.get().parse("<yellow>Even still, it wards off the dark").decoration(TextDecoration.ITALIC, false),
                    MiniMessage.builder().placeholderResolver(resolver).build().parse("<yellow>that has come to taint it.").decoration(TextDecoration.ITALIC, false),
                ).plus(universalLore)
            }
        }

        var hat = ItemBuilder.from(Material.JACK_O_LANTERN) {
            name = gradient("Hollow's Eve Hat 2021")
            glow
            lore {
                listOf(
                    MiniMessage.get().parse("<yellow>\"What do you think?").decoration(TextDecoration.ITALIC, false),
                    MiniMessage.get().parse("<yellow>I let Hoggers help out on this one.").decoration(TextDecoration.ITALIC, false),
                    MiniMessage.builder().placeholderResolver(resolver).build().parse("<yellow>Little guy has talent, I'll admit it.\" <Gigi>").decoration(
                        TextDecoration.ITALIC, false),
                    Component.empty(),
                    MiniMessage.get().parse("<green>Gives the wearer Invisibility").decoration(TextDecoration.ITALIC, false),
                    MiniMessage.get().parse("<green>when worn. <dark_red>Warning: <red>Does not").decoration(TextDecoration.ITALIC, false),
                    MiniMessage.get().parse("<red>effect the hat itself.").decoration(TextDecoration.ITALIC, false),
                    Component.empty(),
                    MiniMessage.get().parse("<#6fe461>Hollow's Eve <green>2021.").decoration(TextDecoration.ITALIC, false),
                )
            }
        }
        var meta = hat.itemMeta
        meta.setCustomModelData(55)
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, AttributeModifier(UUID.fromString("e02c42be-2d90-11ec-8d3d-0242ac130003"), "1", 4.5, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD))
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, AttributeModifier(UUID.fromString("e5109262-2d90-11ec-8d3d-0242ac130003"), "1", 2.5, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD))
        hat.itemMeta = meta
        hollowsEve2021Items[HollowsEve2021.HOLLOWS_EVE_HAT] = hat

        hat = ItemBuilder.head {
            name = gradient("Strider Man Mask")
            owner = Bukkit.getOfflinePlayer(UUID.fromString("38dff22c-c0ec-40b8-bd11-b4376e9a20a5"))
            glow
            texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2RkOWE3NDQ0NTNhNDUzOGY1NzU2Y2JjNDZmYTRjMzk5NGI2N2I4MGZlM2I4YWYwN2IwNDg2YWEwMjU0MDkyZiJ9fX0="
            lore {
                listOf(
                    MiniMessage.get().parse("<yellow>\"Strider Man, Strider Man,").decoration(TextDecoration.ITALIC, false),
                    MiniMessage.get().parse("<yellow>does whatever a Strider").decoration(TextDecoration.ITALIC, false),
                    MiniMessage.get().parse("<yellow>can!\" <white>- <red><bold>Hoggers").decoration(TextDecoration.ITALIC, false),
                ).plus(universalLore)
            }
        }
        meta = hat.itemMeta
        meta.setCustomModelData(55)
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, AttributeModifier(UUID.fromString("dc2828fe-2d90-11ec-8d3d-0242ac130003"), "yes", 2.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD))
        hat.itemMeta = meta
        hollowsEve2021Items[HollowsEve2021.STRIDER_MAN_MASK] = hat

        hat = ItemBuilder.head {
            name = gradient("Clown Mask")
            owner = Bukkit.getOfflinePlayer(UUID.fromString("38dff22c-c0ec-40b8-bd11-b4376e9a20a5"))
            glow
            texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzljZWRmNzgwZTVhYjAxYTNiYjY5ZjM3ZjBkNzc4OGE0YTM1MTNjYjNhMmU0Y2Q4OGJiNjA1MWI0MjdhYTgzMSJ9fX0="
            lore {
                listOf(
                    MiniMessage.get().parse("<yellow>\"Humans thing they're funny, but").decoration(TextDecoration.ITALIC, false),
                    MiniMessage.get().parse("<yellow>Everyone else things they're scary.").decoration(TextDecoration.ITALIC, false),
                    MiniMessage.get().parse("<yellow>It's weeeeeird!\" <white>- <red><bold>Hoggers").decoration(
                        TextDecoration.ITALIC, false),
                ).plus(universalLore)
            }
        }
        meta = hat.itemMeta
        meta.setCustomModelData(55)
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, AttributeModifier(UUID.fromString("d22b09b6-2d90-11ec-8d3d-0242ac130003"), "yes", 2.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD))
        hat.itemMeta = meta
        hollowsEve2021Items[HollowsEve2021.CLOWN_MASK] = hat

        hat = ItemBuilder.head {
            name = gradient("Scarecrow Mask")
            owner = Bukkit.getOfflinePlayer(UUID.fromString("38dff22c-c0ec-40b8-bd11-b4376e9a20a5"))
            texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2UzYjY5MjljOGMzZjBlMzE0YzUyZjA2OWE4NjQ0MDdlYzMxZTc3Y2I0ZGQxOTM5NDI5OGVjYjJmNWMzOTQ4ZiJ9fX0="
            glow
            lore {
                listOf(
                    MiniMessage.get().parse("<yellow>\"I think these are for scaring off").decoration(TextDecoration.ITALIC, false),
                    MiniMessage.get().parse("<yellow>birds, but an Angel told me they're").decoration(TextDecoration.ITALIC, false),
                    MiniMessage.get().parse("<yellow>scary, so here they are!\" <white>- <red><bold>Hoggers").decoration(
                        TextDecoration.ITALIC, false),
                ).plus(universalLore)
            }
        }
        meta = hat.itemMeta
        meta.setCustomModelData(55)
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, AttributeModifier(UUID.fromString("cbad58a0-2d90-11ec-8d3d-0242ac130003"), "yes", 2.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD))
        hat.itemMeta = meta
        hollowsEve2021Items[HollowsEve2021.SCARECROW_MASK] = hat

        hat = ItemBuilder.head {
            name = gradient("Undead Mask")
            owner = Bukkit.getOfflinePlayer(UUID.fromString("38dff22c-c0ec-40b8-bd11-b4376e9a20a5"))
            texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmUyNTY0ZmVmZWUyZDM5OTI0YWU1ZmU2MTg5NjQxZjU2YjM5MjliNDc2YjJmYWFhN2E2MDY0ZjVmMzU2YmViZCJ9fX0="
            glow
            lore {
                listOf(
                    MiniMessage.get().parse("<yellow>\"My best work yet! Is it realistic?").decoration(TextDecoration.ITALIC, false),
                    MiniMessage.get().parse("<yellow>Is it gooooorey ...I've never seen").decoration(TextDecoration.ITALIC, false),
                    MiniMessage.get().parse("<yellow>one. I hope it is...\" <white>- <red><bold>Hoggers").decoration(
                        TextDecoration.ITALIC, false),
                ).plus(universalLore)
            }
        }

        meta = hat.itemMeta
        meta.setCustomModelData(55)
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, AttributeModifier(UUID.fromString("c4eb8a6e-2d90-11ec-8d3d-0242ac130003"), "yes", 2.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HEAD))
        hat.itemMeta = meta
        hollowsEve2021Items[HollowsEve2021.UNDEAD_MASK] = hat

        hollowsEve2021Items[HollowsEve2021.CANDY_SWORD] = ItemBuilder.from(Material.DIAMOND_SWORD) {
            name = gradient("Candy Sword")
            enchant(
                Enchantment.DAMAGE_ALL to 6,
                EcoEnchants.ILLUSION_ASPECT to 3,
                Enchantment.DURABILITY to 5,
                Enchantment.MENDING to 1,
            )
            lore {
                listOf(
                    MiniMessage.get().parse("<yellow>\"A sword made out of rock candy?").decoration(TextDecoration.ITALIC, false),
                    MiniMessage.get().parse("<yellow>Eh. I've fought with worse.\" <white>- <lime><bold>Dylan").decoration(
                        TextDecoration.ITALIC, false),
                ).plus(universalLore)
            }
        }

        hollowsEve2021Items.entries.forEach {
            it.setValue(
                ItemBuilder.from(it.value) {
                    pdc {
                        val value =
                                if(it.key == HollowsEve2021.CANDY_CORN_HELMET || it.key == HollowsEve2021.CANDY_CORN_CHESTPLATE || it.key == HollowsEve2021.CANDY_CORN_PANTS || it.key == HollowsEve2021.CANDY_CORN_BOOTS) {
                                    namespaces[HollowsEve2021.CANDY_CORN_ARMOUR]!!
                                } else namespaces[it.key]!!
                        set(value, PersistentDataType.BYTE, 1.toByte())
                    }
                }
            )
        }
    }

    fun close() {
        hollowsEve2021Items.clear()
    }

}