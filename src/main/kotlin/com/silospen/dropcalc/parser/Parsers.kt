package com.silospen.dropcalc.parser

import com.silospen.dropcalc.*
import com.silospen.dropcalc.reader.LineParser

val baseMonstatsLineParser = object : LineParser<Monster> {
    override fun parseLine(line: List<String>): Monster? {
        if (!isValidMonstatsLine(line)) return null
        val isBoss: Boolean = parseNumericBoolean(line[87])
        return if (!isBoss) {
            parseNonBoss(line)
        } else {
            parseBoss(line)
        }
    }
}

class MinionMonstatsLineParser(private val monsterLibrary: MonsterLibrary) : LineParser<Set<MinionMonster>> {
    override fun parseLine(line: List<String>): Set<MinionMonster>? {
        if (!isValidMonstatsLine(line)) return null

        val ownerId: String = line[0]
        val minion1Id: String = line[19]
        val minion2Id: String = line[20]

        val result = mutableSetOf<MinionMonster>()
        if (minion1Id.isNotBlank()) result.add(getMinion(ownerId, minion1Id))
        if (minion2Id.isNotBlank()) result.add(getMinion(ownerId, minion2Id))
        return result
    }

    private fun getMinion(ownerId: String, minionId: String): MinionMonster {
        val owner = monsterLibrary.lookupMonster(ownerId)
        val minion = monsterLibrary.lookupMonster(minionId)
        return MinionMonster(minion, owner)
    }
}

private fun isValidMonstatsLine(line: List<String>): Boolean {
    val isEnabled: Boolean = parseNumericBoolean(line[12])
    val isKillable: Boolean = parseNumericBoolean(line[89])
    val treasureClass1: String = line[236]
    val treasureClass1N: String = line[240]
    return isEnabled && isKillable && treasureClass1.isNotBlank() && treasureClass1N.isNotBlank()
}

private fun parseBoss(line: List<String>): BossMonster {
    val hasQuestTreasureClass = line[239].isNotBlank()
    return BossMonster(line[0], hasQuestTreasureClass)
}

private fun parseNonBoss(line: List<String>): RegularMonster = RegularMonster(line[0])

private fun parseNumericBoolean(s: String) = s == "1"
