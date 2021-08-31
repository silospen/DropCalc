package com.silospen.dropcalc.monsters

import com.silospen.dropcalc.*

data class Monster(
    val monsterClass: MonsterClass,
    val area: Area,
    val difficulty: Difficulty,
    val type: MonsterType
) {
    val level: Int = constructLevel()

    private fun constructLevel(): Int {
        return 1
    }

    fun getTreasureClass(monsterType: MonsterType, difficulty: Difficulty): TreasureClass? {
        return monsterClass.monsterClassProperties.get(difficulty, monsterType)?.treasureClass
    }
}