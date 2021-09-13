package com.silospen.dropcalc

import com.google.common.collect.Table

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

) : OutcomeType {
    override fun toString(): String {
        return "TreasureClass(name='$name', probabilityDenominator=$probabilityDenominator, properties=$properties)"
    }
}

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
    val monsterClassProperties: Table<Difficulty, TreasureClassType, TreasureClass>,
    val monsterLevels: Map<Difficulty, Int>,
    val minionIds: Set<String>,
    val isBoss: Boolean = false
)

data class SuperUniqueMonsterConfig(
    val id: String,
    val areaName: String,
    val monsterClassId: String,
    val hasMinions: Boolean,
    val treasureClasses: Map<Difficulty, TreasureClass>
)

data class Area(
    val id: String,
    val name: String,
    val monsterLevels: Map<Difficulty, Int>,
    val monsterClassIds: Table<Difficulty, MonsterType, Set<String>>
)

enum class Difficulty {
    NORMAL,
    NIGHTMARE,
    HELL
}

enum class MonsterType {
    REGULAR,
    CHAMPION,
    UNIQUE,
    MINION,
    BOSS,
    SUPERUNIQUE
}

enum class TreasureClassType(val validMonsterTypes: List<MonsterType>, val idSuffix: String) {
    REGULAR(listOf(MonsterType.REGULAR, MonsterType.BOSS), ""),
    CHAMPION(listOf(MonsterType.CHAMPION), ""),
    UNIQUE(listOf(MonsterType.UNIQUE), ""),
    QUEST(listOf(MonsterType.REGULAR, MonsterType.BOSS), "q")
}

fun <R, C, V> Table<R, C, V>.getValue(rowKey: R, columnKey: C) =
    this.get(rowKey, columnKey) ?: throw NoSuchElementException("Row $rowKey Col $columnKey is missing in the map.")