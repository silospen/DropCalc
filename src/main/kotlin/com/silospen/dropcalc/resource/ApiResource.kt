package com.silospen.dropcalc.resource

import com.fasterxml.jackson.annotation.JsonValue
import com.silospen.dropcalc.*
import com.silospen.dropcalc.ItemQuality.SET
import com.silospen.dropcalc.ItemQuality.UNIQUE
import com.silospen.dropcalc.TreasureClassType.QUEST
import com.silospen.dropcalc.items.ItemLibrary
import com.silospen.dropcalc.monsters.Monster
import com.silospen.dropcalc.monsters.MonsterLibrary
import com.silospen.dropcalc.resource.ApiResponseContext.*
import com.silospen.dropcalc.translations.Translations
import com.silospen.dropcalc.treasureclasses.TreasureClassCalculator
import com.silospen.dropcalc.treasureclasses.TreasureClassOutcomeType.DEFINED
import com.silospen.dropcalc.treasureclasses.TreasureClassOutcomeType.VIRTUAL
import com.silospen.dropcalc.treasureclasses.TreasureClassPaths
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.text.DecimalFormat

@RestController
class ApiResource(private val versionedApiResources: Map<Version, VersionedApiResource>) {
    @GetMapping("/tabularAtomicTcs")
    fun getTabularAtomicTcs(
        @RequestParam("version", required = true) version: Version,
        @RequestParam("monsterId", required = true) monsterId: String,
        @RequestParam("monsterType", required = true) monsterType: MonsterType,
        @RequestParam("difficulty", required = true) difficulty: Difficulty,
        @RequestParam("players", required = true) nPlayers: Int,
        @RequestParam("party", required = true) partySize: Int,
        @RequestParam("decMode", required = true) decimalMode: Boolean,
        @RequestParam("desecrated", required = true) desecrated: Boolean,
        @RequestParam("desecratedLevel", required = true) desecratedLevel: Int,
    ): TabularApiResponse = versionedApiResources[version]?.getAtomicTcs(
        monsterId,
        monsterType,
        difficulty,
        nPlayers,
        partySize,
        desecrated,
        desecratedLevel,
        decimalMode,
    ) ?: emptyApiResponse

    @GetMapping("/tabularMonster")
    fun getTabularMonster(
        @RequestParam("version", required = true) version: Version,
        @RequestParam("monsterId", required = true) monsterId: String,
        @RequestParam("monsterType", required = true) monsterType: MonsterType,
        @RequestParam("difficulty", required = true) difficulty: Difficulty,
        @RequestParam("players", required = true) nPlayers: Int,
        @RequestParam("party", required = true) partySize: Int,
        @RequestParam("itemQuality", required = true) apiItemQuality: ApiItemQuality,
        @RequestParam("magicFind", required = true) magicFind: Int,
        @RequestParam("decMode", required = true) decimalMode: Boolean,
        @RequestParam("desecrated", required = true) desecrated: Boolean,
        @RequestParam("desecratedLevel", required = true) desecratedLevel: Int,
    ): TabularApiResponse = versionedApiResources[version]?.getMonster(
        monsterId,
        monsterType,
        difficulty,
        nPlayers,
        partySize,
        apiItemQuality,
        magicFind,
        desecrated,
        desecratedLevel,
        decimalMode,
    ) ?: emptyApiResponse

    @GetMapping("/tabularItem")
    fun getTabularItemProbabilities(
        @RequestParam("version", required = true) version: Version,
        @RequestParam("itemId", required = true) itemId: String,
        @RequestParam("monsterType", required = false) monsterType: MonsterType?,
        @RequestParam("itemQuality", required = true) apiItemQuality: ApiItemQuality,
        @RequestParam("difficulty", required = false) difficulty: Difficulty?,
        @RequestParam("players", required = true) nPlayers: Int,
        @RequestParam("party", required = true) partySize: Int,
        @RequestParam("magicFind", required = true) magicFind: Int,
        @RequestParam("decMode", required = true) decimalMode: Boolean,
        @RequestParam("desecrated", required = true) desecrated: Boolean,
        @RequestParam("desecratedLevel", required = true) desecratedLevel: Int,
        @RequestParam("includeQuest", required = false) includeQuest: Boolean?,
    ): TabularApiResponse = versionedApiResources[version]?.getItemProbabilities(
        itemId,
        monsterType,
        apiItemQuality,
        difficulty,
        nPlayers,
        partySize,
        magicFind,
        desecrated,
        desecratedLevel,
        includeQuest ?: true,
        decimalMode,
    ) ?: emptyApiResponse
}

class VersionedApiResource(
    private val treasureClassCalculator: TreasureClassCalculator,
    private val monsterLibrary: MonsterLibrary,
    private val itemLibrary: ItemLibrary,
    private val translations: Translations,
) {

    private val decimalModeFormat = DecimalFormat("#.###########")
    private val fractionModeFormat = DecimalFormat("#,###")

    private fun formatProbability(decimalMode: Boolean, prob: Double): String = if (decimalMode) {
        decimalModeFormat.format(prob) //TODO: Black cleft should be 1:8000 instead of 7999
    } else {
        if (1 / prob >= 10000) "1:${fractionModeFormat.format((1 / prob).toInt())}" else "1:${(1 / prob).toInt()}"
    }

    fun getAtomicTcs(
        monsterId: String,
        monsterType: MonsterType,
        difficulty: Difficulty,
        nPlayers: Int,
        partySize: Int,
        desecrated: Boolean,
        desecratedLevel: Int,
        decimalMode: Boolean
    ): TabularApiResponse {
        val monsters = monsterLibrary.getMonsters(desecrated, desecratedLevel, monsterId, difficulty, monsterType)
        return TabularApiResponse(
            monsters
                .asSequence()
                .flatMap { monster ->
                    val treasureClassPaths: TreasureClassPaths =
                        treasureClassCalculator.getLeafOutcomes(
                            monster.treasureClass,
                            DEFINED,
                            null,
                            nPlayers,
                            partySize
                        )
                    treasureClassPaths
                        .asSequence()
                        .map {
                            listOf(
                                it.getDisplayName(translations),
                                monster.area.getDisplayName(translations),
                                formatProbability(decimalMode, treasureClassPaths.getFinalProbability(it).toDouble())
                            )
                        }
                }
                .sortedBy { it[0] }
                .toList(),
            createApiResponseContext(monsters)
        )
    }

    private fun createApiResponseContext(monsters: List<Monster>) =
        MonsterApiResponseContext(monsters.map {
            MonsterApiResponseDetails(
                it.id,
                it.area.id,
                it.area.getDisplayName(translations),
                it.level,
                it.treasureClass
            )
        })

    fun getMonster(
        monsterId: String,
        monsterType: MonsterType,
        difficulty: Difficulty,
        nPlayers: Int,
        partySize: Int,
        apiItemQuality: ApiItemQuality,
        magicFind: Int,
        desecrated: Boolean,
        desecratedLevel: Int,
        decimalMode: Boolean
    ): TabularApiResponse {
        val monsters = monsterLibrary.getMonsters(desecrated, desecratedLevel, monsterId, difficulty, monsterType)
        return TabularApiResponse(
            monsters
            .asSequence()
            .flatMap { monster ->
                val treasureClassPaths: TreasureClassPaths =
                    treasureClassCalculator.getLeafOutcomes(
                        monster.treasureClass,
                        VIRTUAL,
                        null,
                        nPlayers,
                        partySize
                    )
                treasureClassPaths
                    .asSequence()
                    .flatMap {
                        generateItemQualityResponse(
                            apiItemQuality.itemQuality,
                            magicFind,
                            treasureClassPaths,
                            monster,
                            it,
                            null
                        )
                        { item, monster, prob ->
                            if ((item.onlyDropsFromMonsterClass != null && monster.monsterClass.id != item.onlyDropsFromMonsterClass) ||
                                !apiItemQuality.additionalFilter(item)
                            ) {
                                null
                            } else {
                                listOf(
                                    item.getDisplayName(translations),
                                    monster.area.getDisplayName(translations),
                                    formatProbability(decimalMode, prob)
                                )
                            }
                        }
                    }
            }
                .sortedBy { it[0] }
            .toList(), createApiResponseContext(monsters))
    }

    private fun generateItemQualityResponseForBaseItem(
        itemQuality: ItemQuality,
        magicFind: Int,
        treasureClassPaths: TreasureClassPaths,
        monster: Monster,
        outcomeType: OutcomeType,
        itemToFilterTo: Item?,
        responseGenerator: (Item, Monster, Double) -> List<String>?
    ): Sequence<List<String>> {
        val baseItem = outcomeType as BaseItem
        val eligibleItems = itemLibrary.getItemsForBaseId(itemQuality, baseItem.id)
            .asSequence()
            .filter { if (itemQuality == UNIQUE || itemQuality == SET) it.level <= monster.level else true }
            .filter { !it.onlyDropsDirectly }
        val raritySum = eligibleItems.sumOf { it.rarity }
        return eligibleItems.filter { itemToFilterTo == null || itemToFilterTo == it }.map { item ->
            val additionalFactorGenerator: (ItemQualityRatios) -> Probability = {
                itemLibrary.getProbQuality(itemQuality, monster, baseItem, it, magicFind)
                    .multiply(Probability(item.rarity, raritySum))
            }
            responseGenerator(
                item,
                monster,
                treasureClassPaths.getFinalProbability(outcomeType, additionalFactorGenerator).toDouble()
            )
        }.filterNotNull()
    }

    private fun generateItemQualityResponse(
        itemQuality: ItemQuality,
        magicFind: Int,
        treasureClassPaths: TreasureClassPaths,
        monster: Monster,
        outcomeType: OutcomeType,
        itemToFilterTo: Item?,
        responseGenerator: (Item, Monster, Double) -> List<String>?
    ): Sequence<List<String>> {
        when (outcomeType) {
            is BaseItem -> {
                return generateItemQualityResponseForBaseItem(
                    itemQuality,
                    magicFind,
                    treasureClassPaths,
                    monster,
                    outcomeType,
                    itemToFilterTo,
                    responseGenerator
                )
            }

            is Item -> {
                if ((itemQuality == UNIQUE || itemQuality == SET) && outcomeType.level > monster.level) return emptySequence()
                return sequenceOf(
                    responseGenerator(
                        outcomeType,
                        monster,
                        treasureClassPaths.getFinalProbability(outcomeType).toDouble()
                    )
                ).filterNotNull()
            }

            else -> {
                throw RuntimeException("Unexpected outcomeType $outcomeType")
            }
        }
    }

    fun getItemProbabilities(
        itemId: String,
        monsterType: MonsterType?,
        apiItemQuality: ApiItemQuality,
        difficulty: Difficulty?,
        nPlayers: Int,
        partySize: Int,
        magicFind: Int,
        desecrated: Boolean,
        desecratedLevel: Int,
        includeQuest: Boolean,
        decimalMode: Boolean
    ): TabularApiResponse {
        val item: Item =
            itemLibrary.getItem(apiItemQuality.itemQuality, itemId)?.takeIf { apiItemQuality.additionalFilter(it) }
                ?: return emptyApiResponse
        val treasureClassPathsCache = mutableMapOf<String, TreasureClassPaths>()
        val monsters =
            monsterLibrary.getMonsters(desecrated, desecratedLevel, difficulty = difficulty, monsterType = monsterType)
        return TabularApiResponse(
            monsters.asSequence()
                .filter { item.onlyDropsFromMonsterClass == null || it.monsterClass.id == item.onlyDropsFromMonsterClass }
                .filter { includeQuest || it.treasureClassType != QUEST }
                .flatMap { monster ->
                    val treasureClassPaths: TreasureClassPaths = treasureClassPathsCache.getOrPut(
                        monster.treasureClass
                    ) {
                        treasureClassCalculator.getLeafOutcomes(
                            monster.treasureClass,
                            VIRTUAL,
                            if (item.onlyDropsDirectly) item else item.baseItem,
                            nPlayers,
                            partySize
                        )
                    }
                    treasureClassPaths
                        .asSequence()
                        .flatMap {
                            generateItemQualityResponse(
                                apiItemQuality.itemQuality,
                                magicFind,
                                treasureClassPaths,
                                monster,
                                it,
                                item
                            )
                            { _, monster, prob ->
                                listOf(
                                    "${monster.getDisplayName(translations)} - ${monster.monsterClass.id} (${monster.difficulty.displayString})",
                                    monster.area.getDisplayName(translations),
                                    formatProbability(decimalMode, prob)
                                )
                            }
                        }
                }
                .toSet()
                .sortedBy { it[0] }
                .toList(), ItemApiResponseContext(
                item.id,
                item.level,
                item.baseItem.id,
                item.baseItem.getDisplayName(translations),
                item.baseItem.itemType.id,
                item.baseItem.itemType.name,
                item.baseItem.level,
            ))
    }
}

sealed interface ApiResponseContext {
    object EmptyApiResponseContext : ApiResponseContext

    data class ItemApiResponseContext(
        val id: String,
        val level: Int,
        val baseItemId: String,
        val baseItemName: String,
        val baseItemTypeId: String,
        val baseItemTypeName: String,
        val baseItemLevel: Int
    ) :
        ApiResponseContext

    data class MonsterApiResponseContext(
        val monsters: List<MonsterApiResponseDetails>
    ) : ApiResponseContext {
        @JsonValue
        fun getMonstersAsList() = monsters
    }

    data class MonsterApiResponseDetails(
        val monsterId: String,
        val areaId: String,
        val areaName: String,
        val monsterLevel: Int,
        val treasureClass: String
    )
}

private val emptyApiResponse = TabularApiResponse(emptyList(), EmptyApiResponseContext)

data class TabularApiResponse(val rows: List<List<String>>, val context: ApiResponseContext) {
    val columns: List<String> = listOf("Name", "Area", "Prob")
}