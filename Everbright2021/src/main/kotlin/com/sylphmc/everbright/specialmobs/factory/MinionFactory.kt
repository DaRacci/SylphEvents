package com.sylphmc.everbright.specialmobs.factory

import com.sylphmc.everbright.specialmobs.MasterMob
import com.sylphmc.everbright.specialmobs.MinionMob
import com.sylphmc.everbright.specialmobs.SpecialMob
import com.sylphmc.everbright.utils.SpecialMobUtil
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Ageable
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity


class MinionFactory(
    val entityType: EntityType,
    val clazz: Class<out MinionMob<*>>,
    val factory: (MasterMob<*>, LivingEntity) -> MinionMob<*>,
) {

    fun wrap(
        master: MasterMob<*>,
        entity: LivingEntity,
    ): SpecialMob<*> {
        SpecialMobUtil.tagSpecialMob(entity)
        val f = factory.invoke(master, entity)
        SpecialMobUtil.setSpecialMobType(entity, clazz.simpleName)
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = f.maxHealth
        entity.getAttribute(Attribute.GENERIC_ARMOR)!!.baseValue = f.armour
        entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)!!.baseValue = f.attackDamage
        entity.addPotionEffects(f.potions)
        entity.health = f.maxHealth
        entity.customName(f.name)
        entity.isCustomNameVisible = entity.customName() != null
        (entity as? Ageable)?.setAdult()
        return f
    }

    init {
        factories.putIfAbsent(clazz, this)
    }

    companion object {
        private val factories = HashMap<Class<out MinionMob<*>>, MinionFactory>()
        operator fun get(clazz: Class<out MinionMob<*>>) = factories[clazz]
    }

}