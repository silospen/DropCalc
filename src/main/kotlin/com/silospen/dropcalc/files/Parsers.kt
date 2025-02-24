package com.silospen.dropcalc.files

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import com.silospen.dropcalc.*
import com.silospen.dropcalc.Difficulty.*
import com.silospen.dropcalc.ItemQuality.*
import com.silospen.dropcalc.MonsterType.*
import com.silospen.dropcalc.MonsterType.UNIQUE
import com.silospen.dropcalc.items.ItemTypeCodeLibrary
import com.silospen.dropcalc.items.SingleItemTypeCodeEntry
import org.springframework.util.StringUtils
import java.util.*

class ItemRatioLineParser : LineParser<ItemRatio?> {
    override fun parseLine(line: Line): ItemRatio? {
        val version = line["Version"].toInt()
        if (version != 1) return null
        val isUber = parseNumericBoolean(line["Uber"])
        val isClassSpecific = parseNumericBoolean(line["Class Specific"])
        return ItemRatio(isUber, isClassSpecific, parseItemQualityModifiers(line))
    }

    private fun parseItemQualityModifiers(line: Line) =
        sequenceOf(ItemQuality.UNIQUE, RARE, SET, MAGIC)
            .associateWith { quality ->
                val qualityString = StringUtils.capitalize(quality.name.lowercase())
                val ratio = line[qualityString].toInt()
                val divisor = line["${qualityString}Divisor"].toInt()
                val min = line["${qualityString}Min"].toInt()
                ItemQualityModifiers(ratio, divisor, min)
            }
}

class BaseItemLineParser(
    itemTypes: List<ItemType>,
    private val hardcodedItemVersion: ItemVersion? = null
) : LineParser<BaseItem?> {

    private val itemTypesById = itemTypes.associateBy { it.id }

    override fun parseLine(line: Line): BaseItem? {
        val type = line["type"]
        if (!parseNumericBoolean(line["spawnable"]) || type == "ques") return null
        val level = line["level"].toInt()
        val itemType = itemTypesById.getValue(type)
        val tcLevel = level + (3 - level % 3) % 3
        val id = line["code"]
        return BaseItem(
            id,
            line["namestr"].trim(),
            itemType,
            hardcodedItemVersion ?: getItemVersion(
                line,
                line["normcode"] == id,
                line["ubercode"] == id,
                line["ultracode"] == id,
            ),
            level,
            itemType.itemTypeCodes.map { it + tcLevel }.toSet()
        )
    }

    private fun getItemVersion(line: Line, isNorm: Boolean, isUber: Boolean, isUltra: Boolean): ItemVersion {
        if (isNorm) return ItemVersion.NORMAL
        if (isUber) return ItemVersion.EXCEPTIONAL
        if (isUltra) return ItemVersion.ELITE
        throw IllegalArgumentException("Failed to get item version for $line")
    }
}

class ItemTypeParser(private val itemTypeCodeLibrary: ItemTypeCodeLibrary) : LineParser<ItemType?> {
    override fun parseLine(line: Line): ItemType? {
        val id = line["Code"]
        if (id.isBlank()) return null
//        val isOnlyMagic = parseNumericBoolean(line["Magic"])
        val canBeRare = parseNumericBoolean(line["Rare"])
        val isOnlyNormal = parseNumericBoolean(line["Normal"])
        return ItemType(
            id,
            line["ItemType"],
            line["Class"].isNotBlank(),
            line["Rarity"].toInt(),
            itemTypeCodeLibrary.getAllParentCodes(id) + id,
            canBeRare,
            !isOnlyNormal
        )
    }
}

class ItemTypeCodesParser : LineParser<SingleItemTypeCodeEntry?> {
    override fun parseLine(line: Line): SingleItemTypeCodeEntry? {
        val id = line["Code"]
        val equiv1 = line["Equiv1"]
        val equiv2 = line["Equiv2"]
        if (id.isBlank()) return null
        return SingleItemTypeCodeEntry(id, sequenceOf(equiv1, equiv2).filter { it.isNotBlank() }.toSet())
    }
}

class UniqueItemLineParser(
    baseItems: List<BaseItem>,
    private val version: Version
) : LineParser<Item?> {

    private val baseItemsById = baseItems.associateBy { it.id }

    override fun parseLine(line: Line): Item? {
        val level = line["lvl"].toIntOrNull() ?: 0
        val enabled = parseNumericBoolean(line["enabled"])
        if (level == 0) return null
        if (version != Version.D2R_V1_0 && !enabled) return null
        val id = line["index"].trim()
        val rarity = line["rarity"].toInt()
        return Item(
            id,
            id,
            ItemQuality.UNIQUE,
            baseItemsById.getValue(line["code"]),
            level,
            rarity,
            !enabled,
            null
        )
    }
}

class SetItemLineParser(
    baseItems: List<BaseItem>
) : LineParser<Item?> {

    private val baseItemsById = baseItems.associateBy { it.id }

    override fun parseLine(line: Line): Item? {
        val level = line["lvl"].toIntOrNull() ?: 0
        if (level == 0) return null
        val id = line["index"].trim()
        val rarity = line["rarity"].toInt()
        return Item(
            id,
            id,
            SET,
            baseItemsById.getValue(line["item"]),
            level,
            rarity,
            false,
            if ("Cow King's Leathers" == line["set"]) "hellbovine" else null
        )
    }
}

class MonstatsLineParser : LineParser<MonsterClass?> {
    override fun parseLine(line: Line): MonsterClass? {
        val isEnabled: Boolean = parseNumericBoolean(line["enabled"])
        val isKillable: Boolean = parseNumericBoolean(line["killable"])
        val treasureClass: String = line["TreasureClass1", "TreasureClass"]
        val treasureClassN: String = line["TreasureClass1(N)", "TreasureClass(N)"]

        val isValid = isEnabled && isKillable && treasureClass.isNotBlank() && treasureClassN.isNotBlank()
        if (!isValid) return null

        val id = line["Id"]
        val isBoss: Boolean = parseNumericBoolean(line["boss"])
        val level: Int = line["Level"].toInt()
        val levelN: Int = line["Level(N)"].toInt()
        val levelH: Int = line["Level(H)"].toInt()

        return MonsterClass(
            id = id,
            nameId = line["NameStr"].trim(),
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

    private fun parseMonsterClassTreasureClasses(line: Line): HashBasedTable<Difficulty, TreasureClassType, String> {
        val treasureClass: String = line["TreasureClass1", "TreasureClass"]
        val treasureClassChamp: String = line["TreasureClass2", "TreasureClassChamp"]
        val treasureClassUnique: String = line["TreasureClass3", "TreasureClassUnique"]
        val treasureClassQuest: String = line["TreasureClass4", "TreasureClassQuest"]
        val treasureClassDesecrated: String? = line.getIfColExistsOrNull("TreasureClassDesecrated")
        val treasureClassDesecratedChamp: String? = line.getIfColExistsOrNull("TreasureClassDesecratedChamp")
        val treasureClassDesecratedUnique: String? = line.getIfColExistsOrNull("TreasureClassDesecratedUnique")
        val treasureClassN: String = line["TreasureClass1(N)", "TreasureClass(N)"]
        val treasureClassChampN: String = line["TreasureClass2(N)", "TreasureClassChamp(N)"]
        val treasureClassUniqueN: String = line["TreasureClass3(N)", "TreasureClassUnique(N)"]
        val treasureClassQuestN: String = line["TreasureClass4(N)", "TreasureClassQuest(N)"]
        val treasureClassDesecratedN: String? = line.getIfColExistsOrNull("TreasureClassDesecrated(N)")
        val treasureClassDesecratedChampN: String? = line.getIfColExistsOrNull("TreasureClassDesecratedChamp(N)")
        val treasureClassDesecratedUniqueN: String? = line.getIfColExistsOrNull("TreasureClassDesecratedUnique(N)")
        val treasureClassH: String = line["TreasureClass1(H)", "TreasureClass(H)"]
        val treasureClassChampH: String = line["TreasureClass2(H)", "TreasureClassChamp(H)"]
        val treasureClassUniqueH: String = line["TreasureClass3(H)", "TreasureClassUnique(H)"]
        val treasureClassQuestH: String = line["TreasureClass4(H)", "TreasureClassQuest(H)"]
        val treasureClassDesecratedH: String? = line.getIfColExistsOrNull("TreasureClassDesecrated(H)")
        val treasureClassDesecratedChampH: String? = line.getIfColExistsOrNull("TreasureClassDesecratedChamp(H)")
        val treasureClassDesecratedUniqueH: String? = line.getIfColExistsOrNull("TreasureClassDesecratedUnique(H)")
        val monsterClassProperties = HashBasedTable.create<Difficulty, TreasureClassType, String>()
        monsterClassProperties.addIfNotBlank(NORMAL, TreasureClassType.REGULAR, treasureClass)
        monsterClassProperties.addIfNotBlank(NORMAL, TreasureClassType.CHAMPION, treasureClassChamp)
        monsterClassProperties.addIfNotBlank(NORMAL, TreasureClassType.UNIQUE, treasureClassUnique)
        monsterClassProperties.addIfNotBlank(NORMAL, TreasureClassType.QUEST, treasureClassQuest)
        monsterClassProperties.addIfNotBlank(NORMAL, TreasureClassType.DESECRATED_REGULAR, treasureClassDesecrated)
        monsterClassProperties.addIfNotBlank(
            NORMAL,
            TreasureClassType.DESECRATED_CHAMPION,
            treasureClassDesecratedChamp
        )
        monsterClassProperties.addIfNotBlank(
            NORMAL,
            TreasureClassType.DESECRATED_UNIQUE,
            treasureClassDesecratedUnique
        )
        monsterClassProperties.addIfNotBlank(NIGHTMARE, TreasureClassType.REGULAR, treasureClassN)
        monsterClassProperties.addIfNotBlank(NIGHTMARE, TreasureClassType.CHAMPION, treasureClassChampN)
        monsterClassProperties.addIfNotBlank(NIGHTMARE, TreasureClassType.UNIQUE, treasureClassUniqueN)
        monsterClassProperties.addIfNotBlank(NIGHTMARE, TreasureClassType.QUEST, treasureClassQuestN)
        monsterClassProperties.addIfNotBlank(NIGHTMARE, TreasureClassType.DESECRATED_REGULAR, treasureClassDesecratedN)
        monsterClassProperties.addIfNotBlank(
            NIGHTMARE,
            TreasureClassType.DESECRATED_CHAMPION,
            treasureClassDesecratedChampN
        )
        monsterClassProperties.addIfNotBlank(
            NIGHTMARE,
            TreasureClassType.DESECRATED_UNIQUE,
            treasureClassDesecratedUniqueN
        )
        monsterClassProperties.addIfNotBlank(HELL, TreasureClassType.REGULAR, treasureClassH)
        monsterClassProperties.addIfNotBlank(HELL, TreasureClassType.CHAMPION, treasureClassChampH)
        monsterClassProperties.addIfNotBlank(HELL, TreasureClassType.UNIQUE, treasureClassUniqueH)
        monsterClassProperties.addIfNotBlank(HELL, TreasureClassType.QUEST, treasureClassQuestH)
        monsterClassProperties.addIfNotBlank(HELL, TreasureClassType.DESECRATED_REGULAR, treasureClassDesecratedH)
        monsterClassProperties.addIfNotBlank(HELL, TreasureClassType.DESECRATED_CHAMPION, treasureClassDesecratedChampH)
        monsterClassProperties.addIfNotBlank(HELL, TreasureClassType.DESECRATED_UNIQUE, treasureClassDesecratedUniqueH)
        return monsterClassProperties
    }

    private fun parseMinions(id: String, line: Line): Set<String> {
        val minion1Id: String = line["minion1"]
        val minion2Id: String = line["minion2"]
        return if (minion1Id.isBlank() && minion2Id.isBlank()) setOf(id)
        else sequenceOf(minion1Id, minion2Id).filter { it.isNotBlank() }.toSet()
    }
}

private fun HashBasedTable<Difficulty, TreasureClassType, String>.addIfNotBlank(
    difficulty: Difficulty,
    treasureClassType: TreasureClassType,
    treasureClassName: String?
) {
    if (!treasureClassName.isNullOrBlank()) {
        put(
            difficulty,
            treasureClassType,
            treasureClassName
        )
    }
}

class SuperUniqueLineParser(
    private val areasBySuperUniqueId: Map<String, String>
) :
    LineParser<SuperUniqueMonsterConfig?> {

    override fun parseLine(line: Line): SuperUniqueMonsterConfig? {
        val id = line["Superunique"]
        val name = line["Name"].trim()
        val monsterClass = line["Class"]
        val hasMinions = line["MaxGrp"].toIntOrNull()?.let { it > 0 } ?: false
        val normalTc = line["TC"]
        val areaName = areasBySuperUniqueId[id]
        if (name.isBlank() || monsterClass.isBlank() || normalTc.isBlank() || areaName.isNullOrBlank()) return null
        val superUniqueTreasureClasses = parseSuperUniqueTreasureClasses(line)
        return SuperUniqueMonsterConfig(
            id,
            name,
            areaName,
            monsterClass,
            hasMinions,
            superUniqueTreasureClasses
        )
    }

    private fun parseSuperUniqueTreasureClasses(line: Line): HashBasedTable<Difficulty, TreasureClassType, String> {
        val tc = line["TC"]
        val tcN = line["TC(N)"]
        val tcH = line["TC(H)"]
        val desecratedTc: String? = line.getIfColExistsOrNull("TC Desecrated")
        val desecratedTcN: String? = line.getIfColExistsOrNull("TC(N) Desecrated")
        val desecratedTcH: String? = line.getIfColExistsOrNull("TC(H) Desecrated")
        val monsterClassProperties = HashBasedTable.create<Difficulty, TreasureClassType, String>()
        monsterClassProperties.addIfNotBlank(NORMAL, TreasureClassType.REGULAR, tc)
        monsterClassProperties.addIfNotBlank(NORMAL, TreasureClassType.DESECRATED_REGULAR, desecratedTc)
        monsterClassProperties.addIfNotBlank(NIGHTMARE, TreasureClassType.REGULAR, tcN)
        monsterClassProperties.addIfNotBlank(NIGHTMARE, TreasureClassType.DESECRATED_REGULAR, desecratedTcN)
        monsterClassProperties.addIfNotBlank(HELL, TreasureClassType.REGULAR, tcH)
        monsterClassProperties.addIfNotBlank(HELL, TreasureClassType.DESECRATED_REGULAR, desecratedTcH)
        return monsterClassProperties
    }
}

class TreasureClassesLineParser : LineParser<TreasureClassConfig?> {
    override fun parseLine(line: Line): TreasureClassConfig? {
        val name = line["Treasure Class"]
        if (name.isBlank()) return null
        val group = line["group"].toIntOrNull()
        val level = line["level"].toIntOrNull()
        val picks = line["Picks"].toInt()
        val unique = line["Unique"].toIntOrNull() ?: 0
        val set = line["Set"].toIntOrNull() ?: 0
        val rare = line["Rare"].toIntOrNull() ?: 0
        val magic = line["Magic"].toIntOrNull() ?: 0
        val noDrop = line["NoDrop"].toIntOrNull()

        val outcomes = parseOutcomes(line)

        return TreasureClassConfig(
            name,
            TreasureClassProperties(
                picks,
                ItemQualityRatios(
                    unique,
                    set,
                    rare,
                    magic,
                ),
                group,
                level,
                noDrop,
            ), outcomes
        )
    }

    private fun parseOutcomes(line: Line): Set<Pair<String, Int>> =
        (1..10).mapNotNull {
            val item = parsePossibleCsv(line["Item$it"])
            val prob = line["Prob$it"]
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
    private val hardcodedAreas: Table<String, MonsterType, Set<String>>
) :
    LineParser<Area?> {
    override fun parseLine(line: Line): Area? {
        val id = line["Name"]
        val name = line["LevelName"].trim()
        val level = line.coalesce("MonLvl1Ex", "MonLvlEx").toIntOrNull()
        val levelN = line.coalesce("MonLvl2Ex", "MonLvlEx(N)").toIntOrNull()
        val levelH = line.coalesce("MonLvl3Ex", "MonLvlEx(H)").toIntOrNull()
        if (name.isBlank()) return null
        val monsterClassIds = parseMonsterClassIds(line, id)
        return Area(
            id,
            name,
            EnumMap<Difficulty, Int>(Difficulty::class.java).apply {
                level?.let { put(NORMAL, level) }
                levelN?.let { put(NIGHTMARE, levelN) }
                levelH?.let { put(HELL, levelH) }
            },
            monsterClassIds
        )
    }

    private fun parseMonsterClassIds(line: Line, id: String): Table<Difficulty, MonsterType, Set<String>> {
        val mons = readMons(line, "mon")
        val nmons = readMons(line, "nmon")
        val umons = readMons(line, "umon")
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

    private fun readMons(line: Line, prefix: String) = (1..10)
        .map { line["$prefix$it"] }
        .filter { it.isNotBlank() }
        .toSet()
}

private fun parseNumericBoolean(s: String) = s == "1"

fun <R, C, V> Table<R, C, V>.getOrDefault(rowKey: R, colKey: C, default: V): V {
    return get(rowKey, colKey) ?: default
}

private fun Table<Difficulty, MonsterType, Set<String>>.putIfNotEmpty(
    rowKey: Difficulty,
    colKey: MonsterType,
    value: Set<String>
) {
    if (value.isNotEmpty()) put(rowKey, colKey, value)
}