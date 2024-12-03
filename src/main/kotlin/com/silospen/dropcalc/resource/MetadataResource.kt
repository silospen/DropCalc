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

class VersionedMetadataResource(val monsterLibrary: MonsterLibrary, val itemLibrary: ItemLibrary, version: Version) {
    private val monstersResponsesByDifficultyType: Map<Triple<Difficulty, MonsterType, Boolean>, List<MetadataResponse>> =
        processMonsters(version)

    private val itemsResponsesByQualityVersion =
        ImmutableTable.builder<ItemQuality, ItemVersion, Set<MetadataResponse>>().apply {
            itemLibrary.items.groupBy({ it.quality to it.baseItem.itemVersion }) { MetadataResponse(it.name, it.id) }
                .mapValues { it.value.toSet() }
                .forEach { (k, v) ->
                    this.put(k.first, k.second, v)
                }
        }.build()

    private fun processMonsters(version: Version): Map<Triple<Difficulty, MonsterType, Boolean>, List<MetadataResponse>> {
        val nonDesecratedTcs: Map<Triple<Difficulty, MonsterType, Boolean>, List<MetadataResponse>> =
            monsterLibrary.monsters.filter { !it.isDesecrated }
                .groupBy({ Triple(it.difficulty, it.type, false) }) { MetadataResponse(it.name, it.id) }
                .mapValues { it.value.toSet() }
                .mapValues { it.value.sortedBy { monstersResponse -> monstersResponse.name } }

        val desecratedMonstersByRawId = monsterLibrary.monsters
            .filter { it.isDesecrated }
            .associateBy { Triple(it.difficulty, it.type, it.rawId) }
            .mapValues { MetadataResponse(it.value.name, it.value.id) }

        val result = nonDesecratedTcs.toMutableMap()

        if (version == Version.D2R_V1_0) {
            nonDesecratedTcs.forEach { (key, nonDesecratedList) ->
                val updatedList = nonDesecratedList.map { metadataResponse ->
                    desecratedMonstersByRawId[Triple(key.first, key.second, metadataResponse.id)]
                        ?: metadataResponse
                }
                result[Triple(key.first, key.second, true)] = updatedList.sortedBy { it.name }
            }
        }
        return result
    }

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