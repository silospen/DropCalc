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
//    val
}

data class BossMonster(override val id: String, val hasQuestTreasureClass: Boolean = false) : Monster
data class RegularMonster(override val id: String) : Monster
//data class ChampionMonster(override val id: String) : Monster
//data class UniqueMonster(override val id: String) : Monster
data class MinionMonster(val minion: Monster, val owner: Monster) : Monster {
    override val id: String
        get() = minion.id
}


class MonsterLibrary(monsters: Set<Monster>) {

    private val monstersById: Map<String, Monster> = monsters.associateBy { it.id }

    fun lookupMonster(id: String) = monstersById.getValue(id)
}