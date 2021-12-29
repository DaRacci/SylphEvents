package com.sylphmc.everbright.specialmobs.factory

import com.sylphmc.events.core.SylphEvents
import com.sylphmc.everbright.specialmobs.SpecialMob
import com.sylphmc.everbright.utils.SpecialMobUtil
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Ageable
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity

class MobFactory(
    val entityType: EntityType,
    val clazz: Class<out SpecialMob<*>>,
    val factory: LivingEntity.() -> SpecialMob<*>,
) {

    fun wrap(
        entity: LivingEntity,
    ): SpecialMob<*> {
        SpecialMobUtil.tagSpecialMob(entity, true)
        val f = factory.invoke(entity)
        f.beforeWrap()
        SpecialMobUtil.setSpecialMobType(entity, clazz.simpleName)
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = f.maxHealth
        entity.getAttribute(Attribute.GENERIC_ARMOR)!!.baseValue = f.armour
        entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)!!.baseValue = f.attackDamage
        entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)!!.addModifier(AttributeModifier("sped", f.movementSpeedMultiplier, AttributeModifier.Operation.ADD_SCALAR))
        entity.health = f.maxHealth
        entity.customName(f.name)
        entity.isCustomNameVisible = entity.customName() != null
        (entity as? Ageable)?.setAdult()
        SylphEvents.launchAsync {f.afterSpawn()}
        return f
    }

    init {
        factories.putIfAbsent(clazz, this)
    }

    companion object {
        private val factories = HashMap<Class<out SpecialMob<*>>, MobFactory>()
        operator fun get(clazz: Class<out SpecialMob<*>>?) = factories[clazz]
    }

}