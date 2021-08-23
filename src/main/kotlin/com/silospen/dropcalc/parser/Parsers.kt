package com.silospen.dropcalc.parser

import com.silospen.dropcalc.*

fun monstatsLineParser(line: List<String>): Monster? {
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

private fun parseMinions(line: List<String>): Set<String> {
    val minion1Id: String = line[19]
    val minion2Id: String = line[20]
    return sequenceOf(minion1Id, minion2Id).filter { it.isNotBlank() }.toSet()
}

private fun parseBoss(line: List<String>): BossMonster {
    val id = line[0]
    val hasQuestTreasureClass = line[239].isNotBlank()
    return BossMonster(id, parseMinions(line), hasQuestTreasureClass)
}

private fun parseNonBoss(line: List<String>): RegularMonster {
    val id = line[0]
    return RegularMonster(id, parseMinions(line))
}

private fun parseNumericBoolean(s: String) = s == "1"
