package com.silospen.dropcalc.resource

import com.google.common.collect.ImmutableTable
import com.silospen.dropcalc.*
import com.silospen.dropcalc.files.getOrDefault
import com.silospen.dropcalc.items.ItemLibrary
import com.silospen.dropcalc.monsters.Monster
import com.silospen.dropcalc.monsters.MonsterLibrary
import com.silospen.dropcalc.translations.Translations
import com.silospen.dropcalc.treasureclasses.TreasureClassLibrary
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

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
    ) = versionedMetadataResources[version]?.getMonsters(difficulty, monsterType, desecrated, includeQuest ?: true)
        ?: emptyList()

    @GetMapping("items")
    fun getItems(
        @RequestParam("version", required = true) version: Version,
        @RequestParam("itemQuality", required = true) apiItemQuality: ApiItemQuality,
        @RequestParam("itemVersion", required = false) itemVersion: ItemVersion?,
    ) = versionedMetadataResources[version]?.getItems(apiItemQuality, itemVersion) ?: emptyList()

    @GetMapping("versions")
    fun getVersions() = versionsResponses
}

data class MonstersResponsesKey(
    val difficulty: Difficulty,
    val monsterType: MonsterType,
    val desecrated: Boolean,
    val includesQuest: Boolean
)

class VersionedMetadataResource(
    val monsterLibrary: MonsterLibrary,
    val itemLibrary: ItemLibrary,
    private val translations: Translations,
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
                listOf(true, false).map { desecrated ->
                    MonstersResponsesKey(difficulty, type, desecrated, includesQuest) to monsterLibrary.getMonsters(
                        desecrated,
                        0,
                        difficulty = difficulty,
                        monsterType = type
                    ).filter(filter)
                        .map { MetadataResponse(it.getDisplayName(translations), it.id) }
                        .toSet()
                        .sortedBy { it.name }
                }
            }
        }.toMap()
    }

    private val itemsResponsesByQualityVersion =
        ImmutableTable.builder<ApiItemQuality, ItemVersion, Set<MetadataResponse>>().apply {
            val virtualTreasureClassNames = treasureClassLibrary.treasureClasses.asSequence().flatMap { it.outcomes }
                .map { it.outcomeType }
                .filter { it is VirtualTreasureClass }
                .map { it.nameId }
                .toSet()

            val associateWith = ApiItemQuality.values().associateWith {
                retrieveItems(virtualTreasureClassNames, it)
            }
            associateWith.forEach { (itemQuality, it) ->
                it.forEach { (version, metadataResponses) ->
                    this.put(itemQuality, version, metadataResponses)
                }
            }
        }.build()

    private fun retrieveItems(virtualTreasureClassNames: Set<String>, apiItemQuality: ApiItemQuality) =
        itemLibrary.items
            .filter { !it.onlyDropsDirectly || virtualTreasureClassNames.contains(it.id) }
            .filter { apiItemQuality.itemQuality == it.quality && apiItemQuality.additionalFilter(it) }
            .groupBy({ it.baseItem.itemVersion }) { MetadataResponse(it.getDisplayName(translations), it.id) }
            .mapValues { it.value.toSet() }

    fun getMonsters(
        difficulty: Difficulty,
        monsterType: MonsterType,
        desecrated: Boolean,
        includeQuest: Boolean,
    ): List<MetadataResponse> {
        return monstersResponses.getOrDefault(
            MonstersResponsesKey(difficulty, monsterType, desecrated, includeQuest),
            emptyList()
        )
    }

    fun getItems(
        apiItemQuality: ApiItemQuality,
        itemVersion: ItemVersion?,
    ): List<MetadataResponse> =
        (if (itemVersion == null) itemsResponsesByQualityVersion.row(apiItemQuality).values.flatten() else (
                itemsResponsesByQualityVersion.getOrDefault(
                    apiItemQuality,
                    itemVersion,
                    emptySet()
                ))).sortedBy { it.name }
}

data class MetadataResponse(
    val name: String,
    val id: String
)