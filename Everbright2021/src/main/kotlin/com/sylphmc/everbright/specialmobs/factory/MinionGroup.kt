package com.sylphmc.everbright.specialmobs.factory

import com.sylphmc.everbright.specialmobs.MasterMob
import com.sylphmc.everbright.specialmobs.MinionMob
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity

class MinionGroup(
    val entityType: EntityType
) {

    val factories: ArrayList<MinionFactory> = ArrayList()

    val baseClass: Class<out Entity>
        get() = entityType.entityClass!!

    fun registerFactory(
        factory: MinionFactory
    ) = factories.add(factory)

    fun registerFactory(
        clazz: Class<out MinionMob<*>>,
        factory: (MasterMob<*>, LivingEntity) -> MinionMob<*>
    ) = factories.add(MinionFactory(entityType, clazz, factory))

}