package com.sylphmc.everbright.utils

import me.racci.sylph.core.Sylph
import org.bukkit.entity.Entity

fun uuidBossBarNamespace(entity: Entity) =
    Sylph.namespacedKey("bossBar${entity.uniqueId}")