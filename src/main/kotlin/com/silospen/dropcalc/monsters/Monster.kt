package com.silospen.dropcalc.monsters

import com.silospen.dropcalc.*
import com.silospen.dropcalc.Difficulty.NORMAL
import com.silospen.dropcalc.MonsterClassType.BOSS
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
        return (if (difficulty == NORMAL || monsterClass.monsterClassType == BOSS)
            monsterClass.monsterLevels.getValue(difficulty)
        else area.monsterLevels.getValue(difficulty)) + getLevelAdjustment()
    }

    private fun getLevelAdjustment(): Int {
        if (monsterClass.monsterClassType == BOSS) return 0
        if (type == CHAMPION) return 2
        if (type == UNIQUE) return 3
        return 0
    }
}