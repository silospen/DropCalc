package com.silospen.dropcalc

import com.google.common.collect.Table
import com.silospen.dropcalc.ItemQuality.*
import com.silospen.dropcalc.ItemQualityRatios.Companion.EMPTY
import kotlin.math.max

data class TreasureClassConfig(
    val name: String,
    val properties: TreasureClassProperties,
    val items: Set<Pair<String, Int>>
)

interface TreasureClass : OutcomeType {
    val probabilityDenominator: Int
    val properties: TreasureClassProperties
    val outcomes: Set<Outcome>
}

data class DefinedTreasureClass(
    override val name: String,
    override val probabilityDenominator: Int,
    override val properties: TreasureClassProperties,
    override val outcomes: Set<Outcome>
) : TreasureClass {
    override fun toString(): String {
        return "DefinedTreasureClass(name='$name', probabilityDenominator=$probabilityDenominator, properties=$properties)"
    }
}

data class VirtualTreasureClass(
    override val name: String,
    override val probabilityDenominator: Int = 1,
    override val outcomes: Set<Outcome> = emptySet()
) : TreasureClass {
    override val properties: TreasureClassProperties = TreasureClassProperties(1, EMPTY)
}

data class TreasureClassProperties(
    val picks: Int,
    val itemQualityRatios: ItemQualityRatios,
    val group: Int? = null,
    val level: Int? = null,
    val noDrop: Int? = null
)

data class Outcome(
    val outcomeType: OutcomeType,
    val probability: Int
)

sealed interface OutcomeType {
    val name: String
//    val probabilitySum: Int
}

data class MonsterClass(
    val id: String,
    val name: String,
    val monsterClassTreasureClasses: Table<Difficulty, TreasureClassType, String>,
    val monsterLevels: Map<Difficulty, Int>,
    val minionIds: Set<String>,
    val isBoss: Boolean = false
)

data class SuperUniqueMonsterConfig(
    val id: String,
    val name: String,
    val areaName: String,
    val monsterClassId: String,
    val hasMinions: Boolean,
    val treasureClasses: Map<Difficulty, String>
)

data class Area(
    val id: String,
    val name: String,
    val monsterLevels: Map<Difficulty, Int>,
    val monsterClassIds: Table<Difficulty, MonsterType, Set<String>>
)

data class Item(
    val id: String,
    val name: String,
    val quality: ItemQuality,
    val baseItem: BaseItem,
    val level: Int,
    val rarity: Int
)

data class BaseItem(
    val id: String,
    override val name: String,
    val itemType: ItemType,
    val itemVersion: ItemVersion,
    val level: Int,
    val treasureClasses: Set<String>
) : OutcomeType

data class ItemType(
    val id: String,
    val name: String,
    val isClassSpecific: Boolean,
    val rarity: Int,
    val itemTypeCodes: Set<String>,
    val canBeRare: Boolean = true,
    val canBeMagic: Boolean = true
)

data class ItemQualityModifiers(
    val ratio: Int,
    val divisor: Int,
    val min: Int,
)

data class ItemRatio(
    val isUber: Boolean,
    val isClassSpecific: Boolean,
    val modifiers: Map<ItemQuality, ItemQualityModifiers>
)

data class ItemQualityRatios(
    private val unique: Int,
    private val set: Int,
    private val rare: Int,
    private val magic: Int
) {

    fun merge(other: ItemQualityRatios): ItemQualityRatios = ItemQualityRatios(
        max(unique, other.unique),
        max(set, other.set),
        max(rare, other.rare),
        max(magic, other.magic),
    )

    fun get(itemQuality: ItemQuality) =
        when (itemQuality) {
            UNIQUE -> unique
            SET -> set
            RARE -> rare
            MAGIC -> magic
            WHITE -> 0
        }

    companion object {
        val EMPTY = ItemQualityRatios(0, 0, 0, 0)
    }
}

enum class ItemQuality {
    WHITE,
    MAGIC,
    RARE,
    SET,
    UNIQUE
}

enum class ItemVersion {
    NORMAL,
    EXCEPTIONAL,
    ELITE,
}

enum class Version(val pathName: String, val displayName: String) {
    V1_12("1.12a", "1.12"),
    V1_13("1.13d", "1.13"),
    V1_14("1.14d", "1.14"),
    D2R_V1_0("D2R_1.0", "Resurrected")
}

enum class Difficulty(val displayString: String) {
    NORMAL("N"),
    NIGHTMARE("NM"),
    HELL("H")
}

enum class MonsterType {
    REGULAR,
    CHAMPION,
    UNIQUE,
    MINION,
    BOSS,
    SUPERUNIQUE
}

enum class TreasureClassType(val validMonsterTypes: List<MonsterType>, val idSuffix: String) {
    REGULAR(listOf(MonsterType.REGULAR, MonsterType.BOSS), ""),
    CHAMPION(listOf(MonsterType.CHAMPION), ""),
    UNIQUE(listOf(MonsterType.UNIQUE), ""),
    QUEST(listOf(MonsterType.REGULAR, MonsterType.BOSS), "q")
}

fun <R, C, V> Table<R, C, V>.getValue(rowKey: R, columnKey: C) =
    this.get(rowKey, columnKey) ?: throw NoSuchElementException("Row $rowKey Col $columnKey is missing in the map.")