package com.sylphmc.everbright.specialmobs.mobs

import com.sylphmc.everbright.specialmobs.MasterMob
import com.sylphmc.everbright.specialmobs.MinionMob
import org.bukkit.entity.Vex

open class WishbaneMinion(
    entity: Vex,
    master: MasterMob<*>
): MinionMob<Vex>(master, entity) {

    override fun beforeWrap() {
        baseEntity.summoner = master.baseEntity
    }

}