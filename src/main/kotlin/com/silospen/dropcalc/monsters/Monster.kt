package com.silospen.dropcalc.monsters

import com.silospen.dropcalc.*

data class Monster(
    val id: String,
    val rawId: String,
    val name: String,
    val monsterClass: MonsterClass,
    val area: Area,
    val difficulty: Difficulty,
    val type: MonsterType,
    val treasureClass: String,
    val isDesecrated: Boolean,
    val level: Int,
    val hasMinions: Boolean,
    val treasureClassType: TreasureClassType,
)