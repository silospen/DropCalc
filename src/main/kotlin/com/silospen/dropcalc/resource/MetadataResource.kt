package com.silospen.dropcalc.resource

import com.silospen.dropcalc.*
import com.silospen.dropcalc.Language.ENGLISH
import com.silospen.dropcalc.items.ItemLibrary
import com.silospen.dropcalc.monsters.Monster
import com.silospen.dropcalc.monsters.MonsterLibrary
import com.silospen.dropcalc.translations.Translations
import com.silospen.dropcalc.treasureclasses.TreasureClassLibrary
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.text.Collator

@RequestMapping(value = ["/metadata"])
@RestController
class MetadataResource(private val versionedMetadataResources: Map<Version, VersionedMetadataResource>) {

    val versionsResponses = Version.values().map { MetadataResponse(it.displayName, it.name) }

    @GetMapping("monsters")
    fun getMonsters(
        @RequestParam("version", required = true) version: Version,
        @RequestParam("difficulty", required = true) difficulty: Difficulty,
        @RequestParam("monsterType", required = true) monsterType: MonsterType,
        @RequestParam("desecrated", required = true) desecrated: Boolean,
        @RequestParam("includeQuest", required = false) includeQuest: Boolean?,
        @RequestParam("language", required = false) language: String?,
    ) = versionedMetadataResources[version]?.getMonsters(
        difficulty,
        monsterType,
        desecrated,
        includeQuest ?: true,
        Language.forLangAttribute(language)
    )
        ?: emptyList()

    @GetMapping("items")
    fun getItems(
        @RequestParam("version", required = true) version: Version,
        @RequestParam("itemQuality", required = true) apiItemQuality: ApiItemQuality,
        @RequestParam("itemVersion", required = false) itemVersion: ItemVersion?,
        @RequestParam("language", required = false) language: String?,
    ) = versionedMetadataResources[version]?.getItems(apiItemQuality, itemVersion, Language.forLangAttribute(language))
        ?: emptyList()

    @GetMapping("versions")
    fun getVersions() = versionsResponses
}

data class MonstersResponsesKey(
    val difficulty: Difficulty,
    val monsterType: MonsterType,
    val desecrated: Boolean,
    val includesQuest: Boolean,
    val language: Language
)

data class ItemsResponsesKey(
    val apiItemQuality: ApiItemQuality,
    val language: Language,
    val itemVersion: ItemVersion?,
)

class VersionedMetadataResource(
    val monsterLibrary: MonsterLibrary,
    val itemLibrary: ItemLibrary,
    private val translations: Translations,
    private val multilingual: Boolean,
    treasureClassLibrary: TreasureClassLibrary,
) {
    private val monstersResponses =
        generateMonstersResponses(true) { true } +
                generateMonstersResponses(false) { it.treasureClassType != TreasureClassType.QUEST }

    private fun generateMonstersResponses(
        includesQuest: Boolean,
        filter: (Monster) -> Boolean
    ): Map<MonstersResponsesKey, List<MetadataResponse>> {
        return Difficulty.values().flatMap { difficulty ->
            MonsterType.values().flatMap { type ->
                listOf(true, false).flatMap { desecrated ->
                    getSupportedLanguages().map { language ->
                        MonstersResponsesKey(
                            difficulty,
                            type,
                            desecrated,
                            includesQuest,
                            language
                        ) to monsterLibrary.getMonsters(
                            desecrated,
                            0,
                            difficulty = difficulty,
                            monsterType = type
                        ).filter(filter)
                            .map { MetadataResponse(it.getDisplayName(translations, language), it.id) }
                            .toSet()
                            .sortedWith(MetadataResponse.comparator(language))
                    }
                }
            }
        }.toMap()
    }

    private val itemsResponses =
        generateItemsResponses(treasureClassLibrary)

    private fun generateItemsResponses(treasureClassLibrary: TreasureClassLibrary): Map<ItemsResponsesKey, List<MetadataResponse>> {
        val virtualTreasureClassNames: Set<String> =
            treasureClassLibrary.treasureClasses.asSequence().flatMap { it.outcomes }
                .map { it.outcomeType }
                .filter { it is VirtualTreasureClass }
                .map { it.nameId }
                .toSet()

        val items: Map<ApiItemQuality, List<Item>> = ApiItemQuality.values().associateWith { apiItemQuality ->
            retrieveItems(virtualTreasureClassNames, apiItemQuality)
        }
        return ApiItemQuality.values().flatMap { apiItemQuality ->
            (arrayOf<ItemVersion?>(null) + ItemVersion.values()).flatMap { itemVersion ->
                val itemList: List<Item> =
                    items.getValue(apiItemQuality)
                        .filter { if (itemVersion == null) true else it.baseItem.itemVersion == itemVersion }
                getSupportedLanguages().map { language ->
                    ItemsResponsesKey(apiItemQuality, language, itemVersion) to itemList.map {
                        MetadataResponse(
                            it.getDisplayName(translations, language),
                            it.id
                        )
                    }.toSet().sortedWith(MetadataResponse.comparator(language))
                }
            }
        }.toMap()
    }

    private fun getSupportedLanguages() = (if (multilingual) Language.values() else arrayOf(ENGLISH))

    private fun retrieveItems(virtualTreasureClassNames: Set<String>, apiItemQuality: ApiItemQuality) =
        itemLibrary.items
            .filter { !it.onlyDropsDirectly || virtualTreasureClassNames.contains(it.id) }
            .filter { apiItemQuality.itemQuality == it.quality && apiItemQuality.additionalFilter(it) }

    fun getMonsters(
        difficulty: Difficulty,
        monsterType: MonsterType,
        desecrated: Boolean,
        includeQuest: Boolean,
        language: Language,
    ): List<MetadataResponse> {
        return monstersResponses.getOrDefault(
            MonstersResponsesKey(difficulty, monsterType, desecrated, includeQuest, language),
            emptyList()
        )
    }

    fun getItems(
        apiItemQuality: ApiItemQuality,
        itemVersion: ItemVersion?,
        language: Language,
    ): List<MetadataResponse> =
        itemsResponses.getOrDefault(ItemsResponsesKey(apiItemQuality, language, itemVersion), emptyList())
}

data class MetadataResponse(
    val name: String,
    val id: String
) {
    companion object {
        fun comparator(language: Language): Comparator<MetadataResponse> =
            compareBy(Collator.getInstance(language.locale)) { it.name }
    }
}