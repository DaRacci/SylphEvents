package com.sylphmc.everbright.specialmobs.factory

import com.sylphmc.everbright.specialmobs.SpecialMob
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity

class MobGroup(
    val entityType: EntityType
) {

    val factories: ArrayList<MobFactory> = ArrayList()

    val baseClass: Class<out Entity>
        get() = entityType.entityClass!!

    fun registerFactory(
        factory: MobFactory
    ) = factories.add(factory)

    fun registerFactory(
        clazz: Class<out SpecialMob<*>>,
        factory: LivingEntity.() -> SpecialMob<*>
    ) = factories.add(MobFactory(entityType, clazz, factory))

}