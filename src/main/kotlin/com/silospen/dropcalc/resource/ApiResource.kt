package com.silospen.dropcalc.resource

import com.silospen.dropcalc.*
import com.silospen.dropcalc.ItemQuality.SET
import com.silospen.dropcalc.ItemQuality.UNIQUE
import com.silospen.dropcalc.items.ItemLibrary
import com.silospen.dropcalc.monsters.Monster
import com.silospen.dropcalc.monsters.MonsterLibrary
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
    @GetMapping("/atomicTcs")
    fun getAtomicTcs(
        @RequestParam("version", required = true) version: Version,
        @RequestParam("monsterId", required = true) monsterId: String,
        @RequestParam("monsterType", required = true) monsterType: MonsterType,
        @RequestParam("difficulty", required = true) difficulty: Difficulty,
        @RequestParam("players", required = true) nPlayers: Int,
        @RequestParam("party", required = true) partySize: Int
    ) = versionedApiResources[version]?.getAtomicTcs(monsterId, monsterType, difficulty, nPlayers, partySize)
        ?: emptyList()

    @GetMapping("/tabularAtomicTcs")
    fun getTabularAtomicTcs(
        @RequestParam("version", required = true) version: Version,
        @RequestParam("monsterId", required = true) monsterId: String,
        @RequestParam("monsterType", required = true) monsterType: MonsterType,
        @RequestParam("difficulty", required = true) difficulty: Difficulty,
        @RequestParam("players", required = true) nPlayers: Int,
        @RequestParam("party", required = true) partySize: Int,
        @RequestParam("decMode", required = true) decimalMode: Boolean,
    ): TabularApiResponse = toTable(
        versionedApiResources[version]?.getAtomicTcs(
            monsterId,
            monsterType,
            difficulty,
            nPlayers,
            partySize
        ), decimalMode
    )

    @GetMapping("/monster")
    fun getMonster(
        @RequestParam("version", required = true) version: Version,
        @RequestParam("monsterId", required = true) monsterId: String,
        @RequestParam("monsterType", required = true) monsterType: MonsterType,
        @RequestParam("difficulty", required = true) difficulty: Difficulty,
        @RequestParam("players", required = true) nPlayers: Int,
        @RequestParam("party", required = true) partySize: Int,
        @RequestParam("itemQuality", required = true) itemQuality: ItemQuality,
        @RequestParam("magicFind", required = true) magicFind: Int,
    ) = versionedApiResources[version]?.getMonster(
        monsterId,
        monsterType,
        difficulty,
        nPlayers,
        partySize,
        itemQuality,
        magicFind
    ) ?: emptyList()

    @GetMapping("/tabularMonster")
    fun getTabularMonster(
        @RequestParam("version", required = true) version: Version,
        @RequestParam("monsterId", required = true) monsterId: String,
        @RequestParam("monsterType", required = true) monsterType: MonsterType,
        @RequestParam("difficulty", required = true) difficulty: Difficulty,
        @RequestParam("players", required = true) nPlayers: Int,
        @RequestParam("party", required = true) partySize: Int,
        @RequestParam("itemQuality", required = true) itemQuality: ItemQuality,
        @RequestParam("magicFind", required = true) magicFind: Int,
        @RequestParam("decMode", required = true) decimalMode: Boolean,
    ) = toTable(
        versionedApiResources[version]?.getMonster(
            monsterId,
            monsterType,
            difficulty,
            nPlayers,
            partySize,
            itemQuality,
            magicFind
        ), decimalMode
    )

    @GetMapping("/item")
    fun getItemProbabilities(
        @RequestParam("version", required = true) version: Version,
        @RequestParam("itemId", required = true) itemId: String,
        @RequestParam("monsterType", required = true) monsterType: MonsterType,
        @RequestParam("itemQuality", required = true) itemQuality: ItemQuality,
        @RequestParam("difficulty", required = false) difficulty: Difficulty?,
        @RequestParam("players", required = true) nPlayers: Int,
        @RequestParam("party", required = true) partySize: Int,
        @RequestParam("magicFind", required = true) magicFind: Int,
    ) = versionedApiResources[version]?.getItemProbabilities(
        itemId,
        monsterType,
        itemQuality,
        difficulty,
        nPlayers,
        partySize,
        magicFind
    ) ?: emptyList()

    @GetMapping("/tabularItem")
    fun getTabularItemProbabilities(
        @RequestParam("version", required = true) version: Version,
        @RequestParam("itemId", required = true) itemId: String,
        @RequestParam("monsterType", required = true) monsterType: MonsterType,
        @RequestParam("itemQuality", required = true) itemQuality: ItemQuality,
        @RequestParam("difficulty", required = false) difficulty: Difficulty?,
        @RequestParam("players", required = true) nPlayers: Int,
        @RequestParam("party", required = true) partySize: Int,
        @RequestParam("magicFind", required = true) magicFind: Int,
        @RequestParam("decMode", required = true) decimalMode: Boolean,
    ) = toTable(
        versionedApiResources[version]?.getItemProbabilities(
            itemId,
            monsterType,
            itemQuality,
            difficulty,
            nPlayers,
            partySize,
            magicFind
        ), decimalMode
    )

    private val decimalModeFormat = DecimalFormat("#.###########")
    private val fractionModeFormat = DecimalFormat("#,###")

    private fun toTable(apiResponse: List<ApiResponse>?, decimalMode: Boolean) =
        TabularApiResponse(
            listOf("Name", "Area", "Prob"),
            apiResponse
                ?.map { listOf(it.name, it.area, formatProbability(decimalMode, it.prob)) }
                ?: emptyList()
        )

    private fun formatProbability(decimalMode: Boolean, prob: Double): String = if (decimalMode) {
        decimalModeFormat.format(prob)
    } else {
        if (1 / prob >= 10000) "1:${fractionModeFormat.format((1 / prob).toInt())}" else "1:${(1 / prob).toInt()}"
    }
}

class VersionedApiResource(
    private val treasureClassCalculator: TreasureClassCalculator,
    private val monsterLibrary: MonsterLibrary,
    private val itemLibrary: ItemLibrary
) {
    fun getAtomicTcs(
        monsterId: String,
        monsterType: MonsterType,
        difficulty: Difficulty,
        nPlayers: Int,
        partySize: Int
    ): List<ApiResponse> = monsterLibrary.getMonsters(monsterId, difficulty, monsterType)
        .asSequence()
        .flatMap { monster ->
            val treasureClassPaths: TreasureClassPaths =
                treasureClassCalculator.getLeafOutcomes(monster.treasureClass, DEFINED, null, nPlayers, partySize)
            treasureClassPaths
                .asSequence()
                .map {
                    ApiResponse(
                        it.name,
                        monster.area.name,
                        treasureClassPaths.getFinalProbability(it).toDouble()
                    )
                }
        }
        .sortedBy { it.name }
        .toList()

    fun getMonster(
        monsterId: String,
        monsterType: MonsterType,
        difficulty: Difficulty,
        nPlayers: Int,
        partySize: Int,
        itemQuality: ItemQuality,
        magicFind: Int,
    ): List<ApiResponse> = monsterLibrary.getMonsters(monsterId, difficulty, monsterType)
        .asSequence()
        .flatMap { monster ->
            val treasureClassPaths: TreasureClassPaths =
                treasureClassCalculator.getLeafOutcomes(monster.treasureClass, VIRTUAL, null, nPlayers, partySize)
            treasureClassPaths
                .asSequence()
                .flatMap {
                    generateItemQualityResponse(itemQuality, magicFind, treasureClassPaths, monster, it, null)
                    { item, monster, prob -> ApiResponse(item.name, monster.area.name, prob) }
                }
        }
        .sortedBy { it.name }
        .toList()

    private fun generateItemQualityResponse(
        itemQuality: ItemQuality,
        magicFind: Int,
        treasureClassPaths: TreasureClassPaths,
        monster: Monster,
        outcomeType: OutcomeType,
        itemToFilterTo: Item?,
        responseGenerator: (Item, Monster, Double) -> ApiResponse
    ): Sequence<ApiResponse> {
        val baseItem = outcomeType as BaseItem
        val eligibleItems = itemLibrary.getItemsForBaseId(itemQuality, baseItem.id)
            .asSequence()
            .filter { if (itemQuality == UNIQUE || itemQuality == SET) it.level <= monster.level else true }
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
        }
    }

    fun getItemProbabilities(
        itemId: String,
        monsterType: MonsterType,
        itemQuality: ItemQuality,
        difficulty: Difficulty?,
        nPlayers: Int,
        partySize: Int,
        magicFind: Int,
    ): List<ApiResponse> {
        val item: Item = itemLibrary.getItem(itemQuality, itemId) ?: return emptyList()
        val treasureClassPathsCache = mutableMapOf<String, TreasureClassPaths>()
        return (difficulty?.let { monsterLibrary.getMonsters(difficulty, monsterType) } ?: monsterLibrary.getMonsters(
            monsterType
        ))
            .asSequence()
            .flatMap { monster ->
                val treasureClassPaths: TreasureClassPaths = treasureClassPathsCache.getOrPut(
                    monster.treasureClass
                ) {
                    treasureClassCalculator.getLeafOutcomes(
                        monster.treasureClass,
                        VIRTUAL,
                        item.baseItem,
                        nPlayers,
                        partySize
                    )
                }
                treasureClassPaths
                    .asSequence()
                    .flatMap {
                        generateItemQualityResponse(itemQuality, magicFind, treasureClassPaths, monster, it, item)
                        { _, monster, prob ->
                            ApiResponse(
                                "${monster.name} - ${monster.monsterClass.id} (${monster.difficulty.displayString})",
                                monster.area.name,
                                prob
                            )
                        }
                    }
            }
            .toSet()
            .sortedBy { it.name }
            .toList()
    }
}

data class ApiResponse(val name: String, val area: String, val prob: Double)
data class TabularApiResponse(val columns: List<String>, val rows: List<List<String>>)
