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
    val treasureClassType: TreasureClassType //Perhaps passing the treasure class in directly would help the sueprunique case?
) {
    val level: Int = constructLevel()
    val treasureClass: TreasureClass = monsterClass.monsterClassProperties.getValue(difficulty, treasureClassType)

    private fun constructLevel(): Int {
        return (if (difficulty == NORMAL || type == BOSS)
            monsterClass.monsterLevels.getValue(difficulty)
        else area.monsterLevels.getValue(difficulty)) + getLevelAdjustment()
    }

    private fun getLevelAdjustment(): Int {
        if (type == BOSS) return 0
        if (type == CHAMPION) return 2
        if (type == UNIQUE) return 3
        return 0
    }

    override fun toString(): String {
        return "Monster(id='$id', monsterClass=$monsterClass, area=$area, difficulty=$difficulty, type=$type, treasureClassType=$treasureClassType, level=$level, treasureClass=$treasureClass)"
    }
}