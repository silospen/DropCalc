package com.silospen.dropcalc

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import com.silospen.dropcalc.MonsterClassType.REGULAR
import java.util.*

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
    val monsterClassProperties: Table<Difficulty, MonsterType, TreasureClass>,
    val monsterLevels: Map<Difficulty, Int>,
    val minionIds: Set<String> = emptySet(),
    val monsterClassType: MonsterClassType = REGULAR
)

data class SuperUniqueMonsterConfig(val id: String, val monsterClassId: String, val hasMinions: Boolean)

data class Area(
    val id: String,
    val monsterLevels: Map<Difficulty, Int>,
    val monsterClassIds: Table<Difficulty, MonsterType, Set<String>>
)

enum class Difficulty {
    NORMAL,
    NIGHTMARE,
    HELL
}

enum class MonsterClassType {
    REGULAR,
    SUPERUNIQUE,
    BOSS
}

enum class MonsterType {
    REGULAR,
    CHAMPION,
    UNIQUE,
    QUEST,
    MINION
}

fun <R, C, V> Table<R, C, V>.getValue(rowKey: R, columnKey: C) =
    this.get(rowKey, columnKey) ?: throw NoSuchElementException("Row $rowKey Col $columnKey is missing in the map.")