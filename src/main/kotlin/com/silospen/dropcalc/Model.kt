package com.silospen.dropcalc

import com.silospen.dropcalc.MonsterConfigType.REGULAR

data class TreasureClassConfig(
    val name: String,
    val properties: TreasureClassProperties,
    val items: Set<Pair<String, Int>>
)

data class TreasureClass(
    override val name: String,
    val probabilityDenominator: Int,
    val properties: TreasureClassProperties,
    val outcomes: Set<Outcome>
) : OutcomeType

data class ItemClass(
    override val name: String
//    override val probabilitySum: Int
) : OutcomeType

data class TreasureClassProperties(
    val group: Int? = null,
    val level: Int? = null,
    val picks: Int,
    val unique: Int? = null,
    val set: Int? = null,
    val rare: Int? = null,
    val magic: Int? = null,
    val noDrop: Int? = null
)

data class Outcome(
    val outcomeType: OutcomeType,
    val probability: Int
)

sealed interface OutcomeType {
    val name: String
//    val probabilitySum: Int
}

data class MonsterClass(
    val id: String,
    val minionIds: Set<String> = emptySet(),
    val hasQuestTreasureClass: Boolean = false,
    val monsterConfigType: MonsterConfigType = REGULAR
)

data class SuperUniqueMonsterConfig(val id: String, val monsterClassId: String, val hasMinions: Boolean)

data class Monster(
    val id: String,
    val name: String,
    val monsterClass: String,
    val difficulty: Difficulty,
    val type: MonsterType
)

enum class Difficulty {
    NORMAL,
    NIGHTMARE,
    HELL
}

enum class MonsterConfigType {
    REGULAR,
    SUPERUNIQUE,
    BOSS
}

enum class MonsterType {
    REGULAR,
    CHAMPION,
    UNIQUE,
    SUPERUNIQUE,
    BOSS,
    MINION
}