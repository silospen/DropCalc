package com.silospen.dropcalc

data class TreasureClass(
    val name: String,
    val group: Int,
    val level: Int,
    val picks: Int,
    val unique: Int,
    val set: Int,
    val rare: Int,
    val magic: Int,
    val noDrop: Int,
    val outcomes: Set<Outcome>
) : OutcomeType

data class Outcome(
    val outcomeType: OutcomeType,
    val probability: Int
)

sealed interface OutcomeType

data class MonsterConfig(
    val id: String,
    val minionIds: Set<String> = emptySet(),
    val hasQuestTreasureClass: Boolean = false,
    val isBoss: Boolean = false
)

data class SuperUniqueMonsterConfig(val id: String, val monsterClass: String, val hasMinions: Boolean)