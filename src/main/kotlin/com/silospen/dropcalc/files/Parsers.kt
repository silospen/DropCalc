package com.silospen.dropcalc.files

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import com.silospen.dropcalc.*
import com.silospen.dropcalc.Difficulty.*
import com.silospen.dropcalc.MonsterType.*
import com.silospen.dropcalc.items.ItemTypeCodeLibrary
import com.silospen.dropcalc.items.SingleItemTypeCodeEntry
import com.silospen.dropcalc.translations.Translations
import java.util.*

class BaseItemLineParser(
    private val translations: Translations,
    private val spawnableColumnIndex: Int,
    private val namestrColumnIndex: Int,
    private val codeColumnIndex: Int,
    private val typeColumnIndex: Int,
    private val levelColumnIndex: Int,
    itemTypes: List<ItemType>
) : LineParser<BaseItem?> {

    private val itemTypesById = itemTypes.associateBy { it.id }

    override fun parseLine(line: List<String>): BaseItem? {
        val type = line[typeColumnIndex]
        if (!parseNumericBoolean(line[spawnableColumnIndex]) || type == "ques") return null
        val level = line[levelColumnIndex].toInt()
        val itemType = itemTypesById.getValue(type)
        val tcLevel = level + (3 - level % 3) % 3
        return BaseItem(
            line[codeColumnIndex],
            translations.getTranslation(line[namestrColumnIndex]),
            itemType,
            level,
            itemType.itemTypeCodes.map { it + tcLevel }.toSet()
        )
    }

    companion object {
        fun forWeaponsTxt(translations: Translations, itemTypes: List<ItemType>) = BaseItemLineParser(
            translations = translations,
            spawnableColumnIndex = 9,
            namestrColumnIndex = 5,
            codeColumnIndex = 3,
            typeColumnIndex = 1,
            levelColumnIndex = 27,
            itemTypes = itemTypes
        )

        fun forArmorTxt(translations: Translations, itemTypes: List<ItemType>) = BaseItemLineParser(
            translations = translations,
            spawnableColumnIndex = 4,
            namestrColumnIndex = 18,
            codeColumnIndex = 17,
            typeColumnIndex = 48,
            levelColumnIndex = 13,
            itemTypes = itemTypes
        )

        fun forMiscTxt(translations: Translations, itemTypes: List<ItemType>) = BaseItemLineParser(
            translations = translations,
            spawnableColumnIndex = 8,
            namestrColumnIndex = 15,
            codeColumnIndex = 13,
            typeColumnIndex = 32,
            levelColumnIndex = 5,
            itemTypes = itemTypes
        )
    }
}

class ItemTypeParser(private val itemTypeCodeLibrary: ItemTypeCodeLibrary) : LineParser<ItemType?> {
    override fun parseLine(line: List<String>): ItemType? {
        val id = line[1]
        if (id.isBlank()) return null
        return ItemType(
            id,
            line[0],
            line[27].isNotBlank(),
            line[24].toInt(),
            itemTypeCodeLibrary.getAllParentCodes(id) + id
        )
    }
}

class ItemTypeCodesParser : LineParser<SingleItemTypeCodeEntry?> {
    override fun parseLine(line: List<String>): SingleItemTypeCodeEntry? {
        val id = line[1]
        val equiv1 = line[2]
        val equiv2 = line[3]
        if (id.isBlank()) return null
        return SingleItemTypeCodeEntry(id, sequenceOf(equiv1, equiv2).filter { it.isNotBlank() }.toSet())
    }
}

class UniqueItemLineParser(
    private val translations: Translations,
    baseItems: List<BaseItem>
) : LineParser<Item?> {

    private val baseItemsById = baseItems.associateBy { it.id }

    override fun parseLine(line: List<String>): Item? {
        val level = line[6].toIntOrNull() ?: 0
        val enabled = parseNumericBoolean(line[2])
        if (!enabled || level == 0) return null
        val id = line[0]
        return Item(id, translations.getTranslation(id), ItemQuality.UNIQUE, baseItemsById.getValue(line[8]), level)
    }
}

class MonstatsLineParser(
    private val translations: Translations
) : LineParser<MonsterClass?> {
    override fun parseLine(line: List<String>): MonsterClass? {
        val isEnabled: Boolean = parseNumericBoolean(line[12])
        val isKillable: Boolean = parseNumericBoolean(line[89])
        val treasureClass1: String = line[236]
        val treasureClass1N: String = line[240]

        val isValid = isEnabled && isKillable && treasureClass1.isNotBlank() && treasureClass1N.isNotBlank()
        if (!isValid) return null

        val id = line[0]
        val name = translations.getTranslation(line[5])
        val isBoss: Boolean = parseNumericBoolean(line[87])
        val level: Int = line[31].toInt()
        val levelN: Int = line[32].toInt()
        val levelH: Int = line[33].toInt()

        return MonsterClass(
            id = id,
            name = name,
            monsterLevels = EnumMap<Difficulty, Int>(Difficulty::class.java).apply {
                put(NORMAL, level)
                put(NIGHTMARE, levelN)
                put(HELL, levelH)
            },
            monsterClassTreasureClasses = parseMonsterClassTreasureClasses(line),
            minionIds = parseMinions(id, line),
            isBoss = isBoss
        )
    }

    private fun parseMonsterClassTreasureClasses(
        line: List<String>
    ): HashBasedTable<Difficulty, TreasureClassType, String> {
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
        val monsterClassProperties = HashBasedTable.create<Difficulty, TreasureClassType, String>()
        addIfNotBlank(monsterClassProperties, NORMAL, TreasureClassType.REGULAR, treasureClass1)
        addIfNotBlank(monsterClassProperties, NORMAL, TreasureClassType.CHAMPION, treasureClass2)
        addIfNotBlank(monsterClassProperties, NORMAL, TreasureClassType.UNIQUE, treasureClass3)
        addIfNotBlank(monsterClassProperties, NORMAL, TreasureClassType.QUEST, treasureClass4)
        addIfNotBlank(monsterClassProperties, NIGHTMARE, TreasureClassType.REGULAR, treasureClass1N)
        addIfNotBlank(monsterClassProperties, NIGHTMARE, TreasureClassType.CHAMPION, treasureClass2N)
        addIfNotBlank(monsterClassProperties, NIGHTMARE, TreasureClassType.UNIQUE, treasureClass3N)
        addIfNotBlank(monsterClassProperties, NIGHTMARE, TreasureClassType.QUEST, treasureClass4N)
        addIfNotBlank(monsterClassProperties, HELL, TreasureClassType.REGULAR, treasureClass1H)
        addIfNotBlank(monsterClassProperties, HELL, TreasureClassType.CHAMPION, treasureClass2H)
        addIfNotBlank(monsterClassProperties, HELL, TreasureClassType.UNIQUE, treasureClass3H)
        addIfNotBlank(monsterClassProperties, HELL, TreasureClassType.QUEST, treasureClass4H)
        return monsterClassProperties
    }

    private fun addIfNotBlank(
        monsterClassProperties: HashBasedTable<Difficulty, TreasureClassType, String>,
        difficulty: Difficulty,
        treasureClassType: TreasureClassType,
        treasureClassName: String
    ) {
        if (treasureClassName.isNotBlank()) {
            monsterClassProperties.put(
                difficulty,
                treasureClassType,
                treasureClassName
            )
        }
    }

    private fun parseMinions(id: String, line: List<String>): Set<String> {
        val minion1Id: String = line[19]
        val minion2Id: String = line[20]
        return if (minion1Id.isBlank() && minion2Id.isBlank()) setOf(id)
        else sequenceOf(minion1Id, minion2Id).filter { it.isNotBlank() }.toSet()
    }
}

class SuperUniqueLineParser(
    private val areasBySuperUniqueId: Map<String, String>,
    private val translations: Translations
) :
    LineParser<SuperUniqueMonsterConfig?> {

    override fun parseLine(line: List<String>): SuperUniqueMonsterConfig? {
        val id = line[0]
        val name = line[1]
        val monsterClass = line[2]
        val hasMinions = line[9].toIntOrNull()?.let { it > 0 } ?: false
        val normalTc = line[17]
        if (name.isBlank() || monsterClass.isBlank() || normalTc.isBlank()) return null
        return SuperUniqueMonsterConfig(
            id,
            translations.getTranslation(name),
            areasBySuperUniqueId.getValue(id),
            monsterClass,
            hasMinions,
            EnumMap<Difficulty, String>(Difficulty::class.java).apply {
                put(NORMAL, normalTc)
                put(NIGHTMARE, line[18])
                put(HELL, line[19])
            })
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
                val item = parsePossibleCsv(line[it])
                val prob = line[it + 1]
                if (item.isBlank()) {
                    null
                } else {
                    item to prob.toInt()
                }
            }
            .toSet()

    private fun parsePossibleCsv(s: String): String {
        if (s.startsWith("\"") && s.endsWith("\"")) {
            return s.split(",")[0].removePrefix("\"")
        }
        return s
    }
}

class LevelsLineParser(
    private val translations: Translations,
    private val hardcodedAreas: Table<String, MonsterType, Set<String>>
) :
    LineParser<Area?> {
    override fun parseLine(line: List<String>): Area? {
        val id = line[0]
        val name = line[120]
        val level = line[59].toIntOrNull()
        val levelN = line[60].toIntOrNull()
        val levelH = line[61].toIntOrNull()
        if (name.isBlank()) return null
        val monsterClassIds = parseMonsterClassIds(line, id)
        return Area(
            id,
            translations.getTranslation(name),
            EnumMap<Difficulty, Int>(Difficulty::class.java).apply {
                level?.let { put(NORMAL, level) }
                levelN?.let { put(NIGHTMARE, levelN) }
                levelH?.let { put(HELL, levelH) }
            },
            monsterClassIds
        )
    }

    private fun parseMonsterClassIds(
        line: List<String>,
        id: String
    ): Table<Difficulty, MonsterType, Set<String>> {
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
        val hardcodedMonsterClassIds =
            MonsterType.values().associateWith { hardcodedAreas.getOrDefault(id, it, emptySet()) }
        monsterClassIds.putIfNotEmpty(NORMAL, REGULAR, mons + hardcodedMonsterClassIds.getValue(REGULAR))
        monsterClassIds.putIfNotEmpty(NORMAL, CHAMPION, umons + hardcodedMonsterClassIds.getValue(CHAMPION))
        monsterClassIds.putIfNotEmpty(NORMAL, UNIQUE, umons + hardcodedMonsterClassIds.getValue(UNIQUE))
        monsterClassIds.putIfNotEmpty(NORMAL, BOSS, hardcodedMonsterClassIds.getValue(BOSS))
        monsterClassIds.putIfNotEmpty(NIGHTMARE, REGULAR, nmons + hardcodedMonsterClassIds.getValue(REGULAR))
        monsterClassIds.putIfNotEmpty(NIGHTMARE, CHAMPION, nmons + hardcodedMonsterClassIds.getValue(CHAMPION))
        monsterClassIds.putIfNotEmpty(NIGHTMARE, UNIQUE, nmons + hardcodedMonsterClassIds.getValue(UNIQUE))
        monsterClassIds.putIfNotEmpty(NIGHTMARE, BOSS, hardcodedMonsterClassIds.getValue(BOSS))
        monsterClassIds.putIfNotEmpty(HELL, REGULAR, nmons + hardcodedMonsterClassIds.getValue(REGULAR))
        monsterClassIds.putIfNotEmpty(HELL, CHAMPION, nmons + hardcodedMonsterClassIds.getValue(CHAMPION))
        monsterClassIds.putIfNotEmpty(HELL, UNIQUE, nmons + hardcodedMonsterClassIds.getValue(UNIQUE))
        monsterClassIds.putIfNotEmpty(HELL, BOSS, hardcodedMonsterClassIds.getValue(BOSS))
        return monsterClassIds
    }
}

private fun parseNumericBoolean(s: String) = s == "1"

private fun <R, C, V> Table<R, C, V>.getOrDefault(rowKey: R, colKey: C, default: V): V {
    return get(rowKey, colKey) ?: default
}

private fun Table<Difficulty, MonsterType, Set<String>>.putIfNotEmpty(
    rowKey: Difficulty,
    colKey: MonsterType,
    value: Set<String>
) {
    if (value.isNotEmpty()) put(rowKey, colKey, value)
}