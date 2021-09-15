package com.silospen.dropcalc.monsters

import com.silospen.dropcalc.Area
import com.silospen.dropcalc.Difficulty
import com.silospen.dropcalc.Difficulty.NORMAL
import com.silospen.dropcalc.MonsterClass
import com.silospen.dropcalc.MonsterType
import com.silospen.dropcalc.MonsterType.*

data class Monster(
    val id: String,
    val name: String,
    val monsterClass: MonsterClass,
    val area: Area,
    val difficulty: Difficulty,
    val type: MonsterType,
    val treasureClass: String
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
}