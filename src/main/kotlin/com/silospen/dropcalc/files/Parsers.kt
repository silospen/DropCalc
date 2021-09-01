package com.silospen.dropcalc.files

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import com.silospen.dropcalc.*
import com.silospen.dropcalc.Difficulty.*
import com.silospen.dropcalc.MonsterClassType.BOSS
import com.silospen.dropcalc.MonsterClassType.REGULAR
import com.silospen.dropcalc.MonsterType.*
import java.util.*

class MonstatsLineParser(private val treasureClassCalculator: TreasureClassCalculator) : LineParser<MonsterClass?> {
    override fun parseLine(line: List<String>): MonsterClass? {
        val isEnabled: Boolean = parseNumericBoolean(line[12])
        val isKillable: Boolean = parseNumericBoolean(line[89])
        val treasureClass1: String = line[236]
        val treasureClass1N: String = line[240]

        val isValid = isEnabled && isKillable && treasureClass1.isNotBlank() && treasureClass1N.isNotBlank()
        if (!isValid) return null

        val id = line[0]
        val isBoss: Boolean = parseNumericBoolean(line[87])
        val level: Int = line[31].toInt()
        val levelN: Int = line[32].toInt()
        val levelH: Int = line[33].toInt()

        return MonsterClass(
            id = id,
            monsterLevels = EnumMap<Difficulty, Int>(Difficulty::class.java).apply {
                put(NORMAL, level)
                put(NIGHTMARE, levelN)
                put(HELL, levelH)
            },
            monsterClassProperties = parseMonsterClassProperties(line),
            minionIds = parseMinions(line),
            monsterClassType = if (isBoss) BOSS else REGULAR
        )
    }

    private fun parseMonsterClassProperties(
        line: List<String>
    ): HashBasedTable<Difficulty, MonsterType, TreasureClass> {
        val treasureClass1: String = line[236]
        val treasureClass2: String = line[237]
        val treasureClass3: String = line[238]
        val treasureClass4: String = line[239]
        val treasureClass1N: String = line[240]
        val treasureClass2N: String = line[241]
        val treasureClass3N: String = line[242]
        val treasureClass4N: String = line[243]
        val treasureClass1H: String = line[244]
        val treasureClass2H: String = line[245]
        val treasureClass3H: String = line[246]
        val treasureClass4H: String = line[247]
        val monsterClassProperties = HashBasedTable.create<Difficulty, MonsterType, TreasureClass>()
        addIfNotBlank(monsterClassProperties, NORMAL, MonsterType.REGULAR, treasureClass1)
        addIfNotBlank(monsterClassProperties, NORMAL, CHAMPION, treasureClass2)
        addIfNotBlank(monsterClassProperties, NORMAL, UNIQUE, treasureClass3)
        addIfNotBlank(monsterClassProperties, NORMAL, QUEST, treasureClass4)
        addIfNotBlank(monsterClassProperties, NIGHTMARE, MonsterType.REGULAR, treasureClass1N)
        addIfNotBlank(monsterClassProperties, NIGHTMARE, CHAMPION, treasureClass2N)
        addIfNotBlank(monsterClassProperties, NIGHTMARE, UNIQUE, treasureClass3N)
        addIfNotBlank(monsterClassProperties, NIGHTMARE, QUEST, treasureClass4N)
        addIfNotBlank(monsterClassProperties, HELL, MonsterType.REGULAR, treasureClass1H)
        addIfNotBlank(monsterClassProperties, HELL, CHAMPION, treasureClass2H)
        addIfNotBlank(monsterClassProperties, HELL, UNIQUE, treasureClass3H)
        addIfNotBlank(monsterClassProperties, HELL, QUEST, treasureClass4H)
        return monsterClassProperties
    }

    private fun addIfNotBlank(
        monsterClassProperties: HashBasedTable<Difficulty, MonsterType, TreasureClass>,
        difficulty: Difficulty,
        monsterType: MonsterType,
        treasureClassName: String
    ) {
        if (treasureClassName.isNotBlank()) {
            monsterClassProperties.put(
                difficulty,
                monsterType,
                treasureClassCalculator.getTreasureClass(treasureClassName)
            )
        }
    }

    private fun parseMinions(line: List<String>): Set<String> {
        val minion1Id: String = line[19]
        val minion2Id: String = line[20]
        return sequenceOf(minion1Id, minion2Id).filter { it.isNotBlank() }.toSet()
    }
}

class SuperUniqueLineParser : LineParser<SuperUniqueMonsterConfig?> {
    override fun parseLine(line: List<String>): SuperUniqueMonsterConfig? {
        val id = line[0]
        val name = line[1]
        val monsterClass = line[2]
        val hasMinions = line[9].toIntOrNull()?.let { it > 0 } ?: false
        if (name.isBlank() || monsterClass.isBlank()) return null
        return SuperUniqueMonsterConfig(id, monsterClass, hasMinions)
    }
}

class TreasureClassesLineParser : LineParser<TreasureClassConfig?> {
    override fun parseLine(line: List<String>): TreasureClassConfig? {
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

    private fun parseOutcomes(line: List<String>): Set<Pair<String, Int>> =
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
}

class LevelsLineParser : LineParser<Area?> {
    override fun parseLine(line: List<String>): Area? {
        val id = line[0]
        val level = line[59].toIntOrNull()
        val levelN = line[60].toIntOrNull()
        val levelH = line[61].toIntOrNull()
        if (level == null || levelN == null || levelH == null) return null
        val monsterClassIds = parseMonsterClassIds(line)
        return Area(
            id,
            EnumMap<Difficulty, Int>(Difficulty::class.java).apply {
                put(NORMAL, level)
                put(NIGHTMARE, levelN)
                put(HELL, levelH)
            },
            monsterClassIds
        )
    }

    private fun parseMonsterClassIds(line: List<String>): Table<Difficulty, MonsterType, Set<String>> {
        val mons = (74..83)
            .map { line[it] }
            .filter { it.isNotBlank() }
            .toSet()
        val nmons = (85..94)
            .map { line[it] }
            .filter { it.isNotBlank() }
            .toSet()
        val umons = (95..104)
            .map { line[it] }
            .filter { it.isNotBlank() }
            .toSet()
        val monsterClassIds = HashBasedTable.create<Difficulty, MonsterType, Set<String>>()
        monsterClassIds.put(NORMAL, MonsterType.REGULAR, mons)
        monsterClassIds.put(NORMAL, CHAMPION, umons)
        monsterClassIds.put(NORMAL, UNIQUE, umons)
        monsterClassIds.put(NIGHTMARE, MonsterType.REGULAR, nmons)
        monsterClassIds.put(NIGHTMARE, CHAMPION, nmons)
        monsterClassIds.put(NIGHTMARE, UNIQUE, nmons)
        monsterClassIds.put(HELL, MonsterType.REGULAR, nmons)
        monsterClassIds.put(HELL, CHAMPION, nmons)
        monsterClassIds.put(HELL, UNIQUE, nmons)
        return monsterClassIds
    }
}

private fun parseNumericBoolean(s: String) = s == "1"
