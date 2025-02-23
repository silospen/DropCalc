package com.silospen.dropcalc.monsters

import com.silospen.dropcalc.*

data class Monster(
    val id: String,
    val rawId: String,
    private val name: String,
    val monsterClass: MonsterClass,
    val area: Area,
    val difficulty: Difficulty,
    val type: MonsterType,
    val treasureClass: String,
    val isDesecrated: Boolean,
    val level: Int,
    val hasMinions: Boolean,
    val treasureClassType: TreasureClassType,
    private val parentMonster: Monster? = null,
) {
    fun getDesecratedMonsterLevel(characterLevel: Int): Int {
        if (characterLevel < level) return level //TODO: Validate if this should be a pre or post upgrade check
        val newLevel = characterLevel + type.desecratedLevelAdjustment
        val desecratedLevelLimit = type.getDesecratedLevelLimit(difficulty)
        if (newLevel > desecratedLevelLimit) return desecratedLevelLimit
        return newLevel
    }

    fun getDisplayName(): String {
        val nameSuffix = getNameSuffix()
        return name + nameSuffix
    }

    private fun getNameSuffix(): String {
        return if (parentMonster != null) {
            " (${parentMonster.getDisplayName()})"
        } else {
            if (treasureClassType.idSuffix.isNotBlank()) " (${treasureClassType.idSuffix})" else ""
        }
    }
}