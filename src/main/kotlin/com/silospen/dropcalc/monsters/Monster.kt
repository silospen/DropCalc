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
) {
    fun getDesecratedMonsterLevel(characterLevel: Int): Int {
        if (characterLevel < level) return level //TODO: Validate if this should be a pre or post upgrade check
        val newLevel = characterLevel + type.desecratedLevelAdjustment
        val desecratedLevelLimit = type.getDesecratedLevelLimit(difficulty)
        if (newLevel > desecratedLevelLimit) return desecratedLevelLimit
        return newLevel
    }
}