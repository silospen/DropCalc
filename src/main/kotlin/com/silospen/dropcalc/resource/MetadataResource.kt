package com.silospen.dropcalc.resource

import com.google.common.collect.ImmutableTable
import com.silospen.dropcalc.*
import com.silospen.dropcalc.files.getOrDefault
import com.silospen.dropcalc.items.ItemLibrary
import com.silospen.dropcalc.monsters.MonsterLibrary
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
        @RequestParam("itemQuality", required = true) itemQuality: ItemQuality,
        @RequestParam("itemVersion", required = false) itemVersion: ItemVersion?,
    ) = versionedMetadataResources[version]?.getItems(itemQuality, itemVersion) ?: emptyList()

    @GetMapping("versions")
    fun getVersions() = versionsResponses
}

class VersionedMetadataResource(val monsterLibrary: MonsterLibrary, val itemLibrary: ItemLibrary) {
    private val monstersResponsesByDifficultyType =
        monsterLibrary.monstersByDifficultyType.mapValues { (_, monsters) ->
            monsters.map { MetadataResponse(it.name, it.id) }.toSet().sortedBy { it.name }
        }.mapKeys { Triple(it.key.difficulty, it.key.type, it.key.desecrated) }

    private val itemsResponsesByQualityVersion =
        ImmutableTable.builder<ItemQuality, ItemVersion, Set<MetadataResponse>>().apply {
            itemLibrary.items.groupBy({ it.quality to it.baseItem.itemVersion }) { MetadataResponse(it.name, it.id) }
                .mapValues { it.value.toSet() }
                .forEach { (k, v) ->
                    this.put(k.first, k.second, v)
                }
        }.build()

    fun getMonsters(
        difficulty: Difficulty,
        monsterType: MonsterType,
        desecrated: Boolean,
    ): List<MetadataResponse> {
        return monstersResponsesByDifficultyType.getOrDefault(Triple(difficulty, monsterType, desecrated), emptyList())
    }

    fun getItems(
        itemQuality: ItemQuality,
        itemVersion: ItemVersion?,
    ): List<MetadataResponse> =
        (if (itemVersion == null) itemsResponsesByQualityVersion.row(itemQuality).values.flatten() else (
                itemsResponsesByQualityVersion.getOrDefault(itemQuality, itemVersion, emptySet()))).sortedBy { it.name }
}

data class MetadataResponse(
    val name: String,
    val id: String
)