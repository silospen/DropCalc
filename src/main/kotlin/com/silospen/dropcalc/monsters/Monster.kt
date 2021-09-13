package com.silospen.dropcalc.monsters

import com.silospen.dropcalc.*
import com.silospen.dropcalc.Difficulty.NORMAL
import com.silospen.dropcalc.MonsterType.*

data class Monster(
    val id: String,
    val monsterClass: MonsterClass,
    val area: Area,
    val difficulty: Difficulty,
    val type: MonsterType,
    val treasureClass: TreasureClass
) {
    val level: Int = constructLevel()

    private fun constructLevel(): Int {
        return (if (difficulty == NORMAL || type == BOSS)
            monsterClass.monsterLevels.getValue(difficulty)
        else area.monsterLevels.getValue(difficulty)) + getLevelAdjustment()
    }

    private fun getLevelAdjustment(): Int {
        if (type == BOSS) return 0
        if (type == CHAMPION) return 2
        if (type == UNIQUE || type == MINION) return 3
        return 0
    }

    override fun toString(): String {
        return "Monster(id='$id', monsterClass=$monsterClass, area=$area, difficulty=$difficulty, type=$type, treasureClass=$treasureClass, level=$level)"
    }
}