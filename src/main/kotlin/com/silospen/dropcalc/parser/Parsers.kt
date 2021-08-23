package com.silospen.dropcalc.parser

import com.silospen.dropcalc.*

fun monstatsLineParser(line: List<String>): Set<Monster>? {
    val isEnabled: Boolean = parseNumericBoolean(line[12])
    val isKillable: Boolean = parseNumericBoolean(line[89])
    val treasureClass1: String = line[236]
    val treasureClass1N: String = line[240]

    val isValid = isEnabled && isKillable && treasureClass1.isNotBlank() && treasureClass1N.isNotBlank()
    if (!isValid) return null

    val isBoss: Boolean = parseNumericBoolean(line[87])

    return if (!isBoss) {
        parseNonBoss(line)
    } else {
        parseBoss(line)
    }
}

private fun parseBoss(line: List<String>): Set<Monster> {
    val id = line[0]
    val result = mutableSetOf(BossMonster(id))
    val hasQuestTreasureClass = line[239].isNotBlank()
    if (hasQuestTreasureClass) result.add(BossMonster(id, hasQuestTreasureClass))
    return result
}

private fun parseNonBoss(line: List<String>): Set<Monster> {
    val id = line[0]
    return mutableSetOf(RegularMonster(id), ChampionMonster(id), UniqueMonster(id))
}

private fun parseNumericBoolean(s: String) = s == "1"
