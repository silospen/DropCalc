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

@RestController
class ApiResource(
    private val treasureClassCalculator: TreasureClassCalculator,
    private val monsterLibrary: MonsterLibrary,
    private val itemLibrary: ItemLibrary
) {
    @GetMapping("/atomicTcs")
    fun getAtomicTcs(
        @RequestParam("monsterId", required = true) monsterId: String,
        @RequestParam("monsterType", required = true) monsterType: MonsterType,
        @RequestParam("difficulty", required = true) difficulty: Difficulty,
        @RequestParam("players", required = true) nPlayers: Int,
        @RequestParam("party", required = true) partySize: Int
    ): List<ApiResponse> {
        return monsterLibrary.getMonsters(monsterId, difficulty, monsterType)
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
    }

    @GetMapping("/monster")
    fun getMonster(
        @RequestParam("monsterId", required = true) monsterId: String,
        @RequestParam("monsterType", required = true) monsterType: MonsterType,
        @RequestParam("difficulty", required = true) difficulty: Difficulty,
        @RequestParam("players", required = true) nPlayers: Int,
        @RequestParam("party", required = true) partySize: Int,
        @RequestParam("itemQuality", required = true) itemQuality: ItemQuality,
        @RequestParam("magicFind", required = true) magicFind: Int,
    ): List<ApiResponse> {
        return monsterLibrary.getMonsters(monsterId, difficulty, monsterType)
            .asSequence()
            .flatMap { monster ->
                val treasureClassPaths: TreasureClassPaths =
                    treasureClassCalculator.getLeafOutcomes(monster.treasureClass, VIRTUAL, null, nPlayers, partySize)
                treasureClassPaths
                    .asSequence()
                    .flatMap {
                        generateItemQualityResponse(itemQuality, magicFind, treasureClassPaths, monster, it)
                        { item, monster, prob -> ApiResponse(item.name, monster.area.name, prob) }
                    }
            }
            .sortedBy { it.name }
            .toList()
    }

    private fun generateItemQualityResponse(
        itemQuality: ItemQuality,
        magicFind: Int,
        treasureClassPaths: TreasureClassPaths,
        monster: Monster,
        outcomeType: OutcomeType,
        responseGenerator: (Item, Monster, Double) -> ApiResponse
    ): Sequence<ApiResponse> {
        val baseItem = outcomeType as BaseItem
        val eligibleItems = itemLibrary.getItemsForBaseId(itemQuality, baseItem.id)
            .asSequence()
            .filter { if (itemQuality == UNIQUE || itemQuality == SET) it.level <= monster.level else true }
        val raritySum = eligibleItems.sumOf { it.rarity }
        return eligibleItems.map { item ->
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

    //    https://dropcalc.silospen.com/cgi-bin/pyDrop.cgi?
//    type=item&itemName=aegis&diff=A&monClass=regMon&nPlayers=1&nGroup=1&mf=0&quality=regItem&decMode=false&version=112

    @GetMapping("/item")
    fun getItemProbabilities(
        @RequestParam("itemId", required = true) itemId: String,
        @RequestParam("monsterType", required = true) monsterType: MonsterType,
        @RequestParam("itemQuality", required = true) itemQuality: ItemQuality,
        @RequestParam("difficulty", required = true) difficulty: Difficulty,
        @RequestParam("players", required = true) nPlayers: Int,
        @RequestParam("party", required = true) partySize: Int,
        @RequestParam("magicFind", required = true) magicFind: Int,
    ) {
//        val item: Item = itemLibrary.getItem(itemId, itemQuality)
    }
}

data class ApiResponse(val name: String, val area: String, val prob: Double)