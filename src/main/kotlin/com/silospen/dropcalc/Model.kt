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

interface Monster {
    val id: String
    val minionIds: Set<String>
//    val
}

data class BossMonster(
    override val id: String,
    override val minionIds: Set<String> = emptySet(),
    val hasQuestTreasureClass: Boolean = false
) : Monster

data class RegularMonster(override val id: String, override val minionIds: Set<String> = emptySet()) : Monster

data class SuperUniqueMonster(val id: String, val monsterClass: String)