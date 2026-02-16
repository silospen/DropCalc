package com.silospen.dropcalc

import com.google.common.collect.Table
import com.silospen.dropcalc.ItemQuality.*
import com.silospen.dropcalc.ItemQualityRatios.Companion.EMPTY
import com.silospen.dropcalc.translations.Translations
import java.util.*
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
    override val nameId: String,
    override val probabilityDenominator: Int,
    override val properties: TreasureClassProperties,
    override val outcomes: Set<Outcome>
) : TreasureClass {
    override fun toString(): String {
        return "DefinedTreasureClass(name='$nameId', probabilityDenominator=$probabilityDenominator, properties=$properties)"
    }

    override fun getDisplayName(translations: Translations, language: Language) = nameId
}

data class VirtualTreasureClass(
    override val nameId: String,
    override val probabilityDenominator: Int = 1,
    override val outcomes: Set<Outcome> = emptySet()
) : TreasureClass {
    override val properties: TreasureClassProperties = TreasureClassProperties(1, EMPTY)

    override fun getDisplayName(translations: Translations, language: Language) = nameId
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
    val nameId: String
    fun getDisplayName(translations: Translations, language: Language): String
}

data class MonsterClass(
    val id: String,
    val nameId: String,
    val monsterClassTreasureClasses: Table<Difficulty, TreasureClassType, String>,
    val monsterLevels: Map<Difficulty, Int>,
    val minionIds: Set<String>,
    val isBoss: Boolean = false
)

data class SuperUniqueMonsterConfig(
    val id: String,
    val nameId: String,
    val areaName: String,
    val monsterClassId: String,
    val hasMinions: Boolean,
    val treasureClasses: Table<Difficulty, TreasureClassType, String>
)

data class Area(
    val id: String,
    private val nameId: String,
    val monsterLevels: Map<Difficulty, Int>,
    val monsterClassIds: Table<Difficulty, MonsterType, Set<String>>
) {
    fun getDisplayName(translations: Translations, language: Language) = translations.getTranslation(nameId, language)
}

data class Item(
    val id: String,
    override val nameId: String,
    val quality: ItemQuality,
    val baseItem: BaseItem,
    val level: Int,
    val rarity: Int,
    val onlyDropsDirectly: Boolean,
    val onlyDropsFromMonsterClass: String?
) : OutcomeType {
    override fun getDisplayName(translations: Translations, language: Language) =
        translations.getTranslation(nameId, language)
}

data class BaseItem(
    val id: String,
    override val nameId: String,
    val itemType: ItemType,
    val itemVersion: ItemVersion,
    val level: Int,
    val treasureClasses: Set<String>
) : OutcomeType {
    override fun getDisplayName(translations: Translations, language: Language) =
        translations.getTranslation(nameId, language)
}

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

enum class ApiItemQuality(val itemQuality: ItemQuality, val additionalFilter: (Item) -> Boolean = { true }) {
    WHITE(ItemQuality.WHITE),
    MAGIC(ItemQuality.MAGIC),
    RARE(ItemQuality.RARE),
    SET(ItemQuality.SET),
    UNIQUE(ItemQuality.UNIQUE),
    RUNE(ItemQuality.WHITE, { it.baseItem.itemType.id == "rune" })
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
    D2R_V1_0("D2R_1.0", "Resurrected"),
    D2R_ROW_3_0("D2R_ROW_3.0", "D2R Reign of the Warlock")
}

enum class Difficulty(val displayString: String) {
    NORMAL("N"),
    NIGHTMARE("NM"),
    HELL("H")
}

enum class MonsterType(
    val desecratedLevelAdjustment: Int,
    private val desecratedLevelLimit: Map<Difficulty, Int>
) {
    REGULAR(2, mapOf(Difficulty.NORMAL to 45, Difficulty.NIGHTMARE to 71, Difficulty.HELL to 96)),
    CHAMPION(4, mapOf(Difficulty.NORMAL to 47, Difficulty.NIGHTMARE to 73, Difficulty.HELL to 98)),
    UNIQUE(5, mapOf(Difficulty.NORMAL to 48, Difficulty.NIGHTMARE to 74, Difficulty.HELL to 99)),
    MINION(5, mapOf(Difficulty.NORMAL to 48, Difficulty.NIGHTMARE to 74, Difficulty.HELL to 99)),
    BOSS(5, mapOf(Difficulty.NORMAL to 48, Difficulty.NIGHTMARE to 74, Difficulty.HELL to 99)),
    SUPERUNIQUE(5, mapOf(Difficulty.NORMAL to 48, Difficulty.NIGHTMARE to 74, Difficulty.HELL to 99));

    fun getDesecratedLevelLimit(difficulty: Difficulty): Int = desecratedLevelLimit.getValue(difficulty)
}

enum class TreasureClassType(
    val validMonsterTypes: List<MonsterType>,
    val idSuffix: String,
    val isDesecrated: Boolean
) {
    REGULAR(listOf(MonsterType.REGULAR, MonsterType.BOSS, MonsterType.SUPERUNIQUE), "", false),
    CHAMPION(listOf(MonsterType.CHAMPION), "", false),
    UNIQUE(listOf(MonsterType.UNIQUE), "", false),
    QUEST(listOf(MonsterType.REGULAR, MonsterType.BOSS), "q", false),
    DESECRATED_REGULAR(listOf(MonsterType.REGULAR, MonsterType.BOSS, MonsterType.SUPERUNIQUE), "d", true),
    DESECRATED_CHAMPION(listOf(MonsterType.CHAMPION), "d", true),
    DESECRATED_UNIQUE(listOf(MonsterType.UNIQUE), "d", true),

}

enum class Language(val d2String: String, val locale: Locale) {
    ENGLISH("enUS", Locale.US),
    TAIWANESE("zhTW", Locale.TAIWAN),
    GERMAN("deDE", Locale.GERMAN),
    SPANISH("esES", Locale("es", "ES")),
    FRENCH("frFR", Locale.FRENCH),
    ITALIAN("itIT", Locale.ITALIAN),
    KOREAN("koKR", Locale.KOREAN),
    POLISH("plPL", Locale("pl", "PL")),
    MEXICAN("esMX", Locale("es", "MX")),
    JAPANESE("jaJP", Locale.JAPANESE),
    BRAZILIAN("ptBR", Locale("pt", "BR")),
    RUSSIAN("ruRU", Locale("ru", "RU")),
    CHINESE("zhCN", Locale.SIMPLIFIED_CHINESE),
    ;

    companion object {

        private val localeLookup = mapOf(
            Locale.US to ENGLISH,
            Locale.TAIWAN to TAIWANESE,
            Locale.GERMAN to GERMAN,
            Locale("es", "ES") to SPANISH,
            Locale.FRENCH to FRENCH,
            Locale.ITALIAN to ITALIAN,
            Locale.KOREAN to KOREAN,
            Locale("pl", "PL") to POLISH,
            Locale("es", "MX") to MEXICAN,
            Locale.JAPANESE to JAPANESE,
            Locale("pt", "BR") to BRAZILIAN,
            Locale("ru", "RU") to RUSSIAN,
            Locale.SIMPLIFIED_CHINESE to CHINESE,
        )

        private val languageLookup = mapOf(
            "en" to ENGLISH,
            "de" to GERMAN,
            "es" to SPANISH,
            "fr" to FRENCH,
            "it" to ITALIAN,
            "ko" to KOREAN,
            "pl" to POLISH,
            "ja" to JAPANESE,
            "ru" to RUSSIAN,
            "ZH" to CHINESE,
        )

        fun forD2String(fieldName: String): Language? {
            return values().firstOrNull { it.d2String == fieldName }
        }

        fun forLangAttribute(langAttribute: String?, default: Language = ENGLISH): Language {
            val parsedLocale = langAttribute?.let { Locale.forLanguageTag(langAttribute) }
            return parsedLocale?.let { locale -> localeLookup.getOrDefault(locale, languageLookup[locale.language]) }
                ?: default
        }
    }
}

fun <R, C, V> Table<R, C, V>.getValue(rowKey: R, columnKey: C) =
    this.get(rowKey, columnKey) ?: throw NoSuchElementException("Row $rowKey Col $columnKey is missing in the map.")