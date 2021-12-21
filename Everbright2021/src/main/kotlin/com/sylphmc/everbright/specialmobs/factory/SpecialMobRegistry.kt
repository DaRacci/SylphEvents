package com.sylphmc.everbright.specialmobs.factory

import com.google.common.collect.ImmutableMap
import com.sylphmc.everbright.specialmobs.MasterMob
import com.sylphmc.everbright.specialmobs.MinionMob
import com.sylphmc.everbright.specialmobs.SpecialMob
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity

//typealias MinionBlock = (MasterMob<*>, LivingEntity) -> MinionMob<*>

object SpecialMobRegistry {

    private val MOB_GROUPS = HashMap<Class<out Entity>, MobGroup>()
    private val MINION_GROUPS = HashMap<Class<out Entity>, MinionGroup>()
    private val ENTITY_MAPPINGS = HashMap<Class<out Entity>, Class<out Entity>>()

    fun <T: SpecialMob<*>> registerMob(
        entityType: EntityType,
        clazz: Class<T>,
        factory: LivingEntity.() -> SpecialMob<*>
    ) = MOB_GROUPS.computeIfAbsent(entityType.entityClass!!) {
        MobGroup(entityType)
    }.registerFactory(clazz, factory)

    fun <T: MinionMob<*>> registerMinion(
        entityType: EntityType,
        clazz: Class<T>,
        factory: (MasterMob<*>, LivingEntity) -> MinionMob<*>
    ) = MINION_GROUPS.computeIfAbsent(entityType.entityClass!!) {
        MinionGroup(entityType)
    }.registerFactory(clazz, factory)

    private fun getMobBaseClass(
        entity: Entity
    ): Class<out Entity>? {
        return if(entity.type != EntityType.UNKNOWN) {
            ENTITY_MAPPINGS.computeIfAbsentOrReturnIfNull(entity.type.entityClass!!) {
                for(clazz in MOB_GROUPS.keys) {
                    if(clazz.isAssignableFrom(this)) {
                        return clazz
                    }
                }
                return null
            }
        } else null
    }

    private fun getMinionBaseClass(
        entity: Entity
    ): Class<out Entity>? {
        return if(entity.type != EntityType.UNKNOWN) {
            ENTITY_MAPPINGS.computeIfAbsentOrReturnIfNull(entity.type.entityClass!!) {
                for(clazz in MINION_GROUPS.keys) {
                    if(clazz.isAssignableFrom(this)) {
                        return clazz
                    }
                }
                return null
            }
        } else null
    }

    private inline fun <K: Any, V: Any> HashMap<K, V>.computeIfAbsentOrReturnIfNull(
        key: K,
        mappingBlock: K.() -> V?
    ): V? {
        if(this.containsKey(key)) return null
        val result = mappingBlock.invoke(key) ?: return null
        return this.put(key, result)
    }

    fun getMobGroup(
        entity: Entity
    ) = mobGroups[getMobBaseClass(entity)]

    fun getMinionGroup(
        entity: Entity
    ) = minionGroups[getMinionBaseClass(entity)]

    fun getMobGroup(
        name: String
    ) = MOB_GROUPS.entries.firstOrNull{it.key.simpleName.equals(name, true)}?.value

    fun getMinionGroup(
        name: String
    ) = MINION_GROUPS.entries.firstOrNull{it.key.simpleName.equals(name, true)}?.value

    val registeredMobs: Set<MobFactory>
        get() {
            val wrappers: MutableSet<MobFactory> = HashSet()
            for (value in MOB_GROUPS.values) {
                wrappers.addAll(value.factories)
            }
            return wrappers
        }

    private val registeredMinions: Set<MinionFactory>
        get() {
            val wrappers = HashSet<MinionFactory>()
            for(value in MINION_GROUPS.values) {
                wrappers.addAll(value.factories)
            }
            return wrappers
        }

    private val mobGroups: ImmutableMap<Class<out Entity>, MobGroup>
        get() = ImmutableMap.copyOf(MOB_GROUPS)

    private val minionGroups: ImmutableMap<Class<out Entity>, MinionGroup>
        get() = ImmutableMap.copyOf(MINION_GROUPS)

    fun getMobFactoryByName(
        arg: String
    ) = registeredMobs.firstOrNull {it.clazz.simpleName.equals(arg, true)}

    fun getMinionFactoryByName(
        arg: String
    ) = registeredMinions.firstOrNull{it.clazz.simpleName.equals(arg, true)}

}