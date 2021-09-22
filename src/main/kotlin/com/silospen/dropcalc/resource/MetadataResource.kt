package com.silospen.dropcalc.resource

import com.google.common.collect.ImmutableTable
import com.silospen.dropcalc.Difficulty
import com.silospen.dropcalc.ItemQuality
import com.silospen.dropcalc.ItemVersion
import com.silospen.dropcalc.MonsterType
import com.silospen.dropcalc.files.getOrDefault
import com.silospen.dropcalc.items.ItemLibrary
import com.silospen.dropcalc.monsters.MonsterLibrary
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping(value = ["/metadata"])
@RestController
class MetadataResource(monsterLibrary: MonsterLibrary, private val itemLibrary: ItemLibrary) {

    private val monstersResponsesByDifficultyType =
        monsterLibrary.monsters.groupBy({ it.difficulty to it.type }) { MetadataResponse(it.name, it.id) }
            .mapValues { it.value.toSet() }
            .mapValues { it.value.sortedBy { monstersResponse -> monstersResponse.name } }

    private val itemsResponsesByQualityVersion =
        ImmutableTable.builder<ItemQuality, ItemVersion, Set<MetadataResponse>>().apply {
            itemLibrary.items.groupBy({ it.quality to it.baseItem.itemVersion }) { MetadataResponse(it.name, it.id) }
                .mapValues { it.value.toSet() }
                .forEach { (k, v) ->
                    this.put(k.first, k.second, v)
                }
        }.build()


    @GetMapping("monsters")
    fun getMonsters(
        @RequestParam("difficulty", required = true) difficulty: Difficulty,
        @RequestParam("monsterType", required = true) monsterType: MonsterType,
    ): List<MetadataResponse> {
        return monstersResponsesByDifficultyType.getOrDefault(difficulty to monsterType, emptyList())
    }

    @GetMapping("items")
    fun getItems(
        @RequestParam("itemQuality", required = true) itemQuality: ItemQuality,
        @RequestParam("itemVersion", required = false) itemVersion: ItemVersion?,
    ): List<MetadataResponse> =
        (if (itemVersion == null) itemsResponsesByQualityVersion.row(itemQuality).values.flatten() else (
                itemsResponsesByQualityVersion.getOrDefault(itemQuality, itemVersion, emptySet()))).sortedBy { it.name }
}

data class MetadataResponse(
    val name: String,
    val id: String
)