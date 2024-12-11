package com.silospen.dropcalc.resource

import com.google.common.collect.ImmutableTable
import com.silospen.dropcalc.*
import com.silospen.dropcalc.files.getOrDefault
import com.silospen.dropcalc.items.ItemLibrary
import com.silospen.dropcalc.monsters.MonsterLibrary
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
    ) = versionedMetadataResources[version]?.getMonsters(difficulty, monsterType, desecrated) ?: emptyList()

    @GetMapping("items")
    fun getItems(
        @RequestParam("version", required = true) version: Version,
        @RequestParam("itemQuality", required = true) apiItemQuality: ApiItemQuality,
        @RequestParam("itemVersion", required = false) itemVersion: ItemVersion?,
    ) = versionedMetadataResources[version]?.getItems(apiItemQuality, itemVersion) ?: emptyList()

    @GetMapping("versions")
    fun getVersions() = versionsResponses
}

class VersionedMetadataResource(
    val monsterLibrary: MonsterLibrary,
    val itemLibrary: ItemLibrary,
    treasureClassLibrary: TreasureClassLibrary,
) {
    private val monstersResponsesByDifficultyType =
        monsterLibrary.monstersByDifficultyType.mapValues { (_, monsters) ->
            monsters.map { MetadataResponse(it.name, it.id) }.toSet().sortedBy { it.name }
        }.mapKeys { Triple(it.key.difficulty, it.key.type, it.key.desecrated) }

    private val itemsResponsesByQualityVersion =
        ImmutableTable.builder<ApiItemQuality, ItemVersion, Set<MetadataResponse>>().apply {
            val virtualTreasureClassNames = treasureClassLibrary.treasureClasses.asSequence().flatMap { it.outcomes }
                .map { it.outcomeType }
                .filter { it is VirtualTreasureClass }
                .map { it.name }
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
            .groupBy({ it.baseItem.itemVersion }) { MetadataResponse(it.name, it.id) }
            .mapValues { it.value.toSet() }

    fun getMonsters(
        difficulty: Difficulty,
        monsterType: MonsterType,
        desecrated: Boolean,
    ): List<MetadataResponse> {
        return monstersResponsesByDifficultyType.getOrDefault(Triple(difficulty, monsterType, desecrated), emptyList())
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