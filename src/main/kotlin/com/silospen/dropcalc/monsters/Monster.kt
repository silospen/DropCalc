package com.silospen.dropcalc.monsters

import com.silospen.dropcalc.*

data class Monster(
    val monsterClass: MonsterClass,
    val areas: Set<Area>,
    val type: MonsterType
) {
    fun getTreasureClass(monsterType: MonsterType, difficulty: Difficulty): TreasureClass? {
        return monsterClass.monsterClassProperties.get(difficulty, monsterType)?.treasureClass
    }
}