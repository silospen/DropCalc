package com.silospen.dropcalc.parser

import com.silospen.dropcalc.*
import com.silospen.dropcalc.MonsterConfigType.BOSS
import com.silospen.dropcalc.MonsterConfigType.REGULAR

fun monstatsLineParser(line: List<String>): MonsterClass? {
    val isEnabled: Boolean = parseNumericBoolean(line[12])
    val isKillable: Boolean = parseNumericBoolean(line[89])
    val treasureClass1: String = line[236]
    val treasureClass1N: String = line[240]

    val isValid = isEnabled && isKillable && treasureClass1.isNotBlank() && treasureClass1N.isNotBlank()
    if (!isValid) return null

    val id = line[0]
    val isBoss: Boolean = parseNumericBoolean(line[87])
    val hasQuestTreasureClass = line[239].isNotBlank()
    return MonsterClass(
        id = id,
        minionIds = parseMinions(line),
        hasQuestTreasureClass = hasQuestTreasureClass,
        monsterConfigType = if (isBoss) BOSS else REGULAR
    )
}

private fun parseMinions(line: List<String>): Set<String> {
    val minion1Id: String = line[19]
    val minion2Id: String = line[20]
    return sequenceOf(minion1Id, minion2Id).filter { it.isNotBlank() }.toSet()
}

fun superUniqueLineParser(line: List<String>): SuperUniqueMonsterConfig? {
    val id = line[0]
    val name = line[1]
    val monsterClass = line[2]
    val hasMinions = line[9].toIntOrNull()?.let { it > 0 } ?: false
    if (name.isBlank() || monsterClass.isBlank()) return null
    return SuperUniqueMonsterConfig(id, monsterClass, hasMinions)
}

fun treasureClassesParser(line: List<String>): TreasureClassConfig? {
    val name = line[0]
    if (name.isBlank()) return null
    val group = line[1].toIntOrNull()
    val level = line[2].toIntOrNull()
    val picks = line[3].toInt()
    val unique = line[4].toIntOrNull()
    val set = line[5].toIntOrNull()
    val rare = line[6].toIntOrNull()
    val magic = line[7].toIntOrNull()
    val noDrop = line[8].toIntOrNull()

    val outcomes = parseOutcomes(line)

    return TreasureClassConfig(
        name,
        TreasureClassProperties(
            group,
            level,
            picks,
            unique,
            set,
            rare,
            magic,
            noDrop,
        ), outcomes
    )
}

fun parseOutcomes(line: List<String>): Set<Pair<String, Int>> =
    generateSequence(9) { it + 2 }
        .take(10)
        .mapNotNull {
            val item = line[it]
            val prob = line[it + 1]
            if (item.isBlank()) {
                null
            } else {
                item to prob.toInt()
            }
        }
        .toSet()

private fun parseNumericBoolean(s: String) = s == "1"
