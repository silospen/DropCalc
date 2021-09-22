package com.silospen.dropcalc.monsters

import com.silospen.dropcalc.Area
import com.silospen.dropcalc.Difficulty
import com.silospen.dropcalc.MonsterClass
import com.silospen.dropcalc.MonsterType

data class Monster(
    val id: String,
    val name: String,
    val monsterClass: MonsterClass,
    val area: Area,
    val difficulty: Difficulty,
    val type: MonsterType,
    val treasureClass: String,
    val level: Int
)