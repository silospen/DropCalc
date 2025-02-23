package com.silospen.dropcalc.monsters

import com.silospen.dropcalc.*
import com.silospen.dropcalc.translations.Translations

data class Monster(
    val id: String,
    val rawId: String,
    private val nameId: String,
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

    fun getDisplayName(translations: Translations): String {
        val nameSuffix = getNameSuffix(translations)
        return translations.getTranslation(nameId) + nameSuffix
    }

    private fun getNameSuffix(translations: Translations): String {
        return if (parentMonster != null) {
            " (${parentMonster.getDisplayName(translations)})"
        } else {
            if (treasureClassType.idSuffix.isNotBlank()) " (${treasureClassType.idSuffix})" else ""
        }
    }
}