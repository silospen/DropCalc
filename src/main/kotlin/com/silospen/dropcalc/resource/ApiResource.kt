package com.silospen.dropcalc.resource

import com.silospen.dropcalc.*
import com.silospen.dropcalc.ItemQuality.*
import com.silospen.dropcalc.items.ItemLibrary
import com.silospen.dropcalc.monsters.Monster
import com.silospen.dropcalc.monsters.MonsterLibrary
import com.silospen.dropcalc.treasureclasses.TreasureClassCalculator
import com.silospen.dropcalc.treasureclasses.TreasureClassOutcomeType
import com.silospen.dropcalc.treasureclasses.TreasureClassOutcomeType.DEFINED
import com.silospen.dropcalc.treasureclasses.TreasureClassOutcomeType.VIRTUAL
import com.silospen.dropcalc.treasureclasses.TreasureClassPaths
import org.apache.commons.math3.fraction.BigFraction
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.RoundingMode

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
        return getTreasureClassPathsForMonsterAndApplyFunction(
            monsterId,
            difficulty,
            monsterType,
            DEFINED,
            nPlayers,
            partySize,
            ::generateBaseTreasureClassResponse
        )
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
    ) = getTreasureClassPathsForMonsterAndApplyFunction(
        monsterId,
        difficulty,
        monsterType,
        VIRTUAL,
        nPlayers,
        partySize,
        if (itemQuality == WHITE) ::generateBaseTreasureClassResponse else generateItemQualityResponse(
            itemQuality,
            magicFind
        )
    )

    private fun generateItemQualityResponse(
        itemQuality: ItemQuality,
        magicFind: Int
    ): (TreasureClassPaths, Monster, OutcomeType) -> List<ApiResponse> =
        { treasureClassPaths, monster, outcomeType ->
            val baseItem = outcomeType as BaseItem
            val eligibleItems = itemLibrary.itemsByQualityAndBaseId
                .getOrDefault(itemQuality to baseItem.id, emptyList())
                .filter { if (itemQuality == UNIQUE || itemQuality == SET) it.level <= monster.level else true }
            val raritySum = eligibleItems.sumOf { it.rarity }
            eligibleItems.map { item ->
                val additionalFactorGenerator: (ItemQualityRatios) -> BigFraction = {
                    itemLibrary.getProbQuality(itemQuality, monster, baseItem, it, magicFind)
                        .multiply(BigFraction(item.rarity, raritySum))
                }
                ApiResponse(
                    item.name,
                    monster.area.name,
                    Probability(
                        treasureClassPaths.getFinalProbability(
                            outcomeType,
                            additionalFactorGenerator
                        )
                    )
                )
            }
        }

    private fun generateBaseTreasureClassResponse(
        treasureClassPaths: TreasureClassPaths,
        monster: Monster,
        outcomeType: OutcomeType
    ) = listOf(
        ApiResponse(
            outcomeType.name,
            monster.area.name,
            Probability(treasureClassPaths.getFinalProbability(outcomeType))
        )
    )

    private fun getTreasureClassPathsForMonsterAndApplyFunction(
        monsterId: String,
        difficulty: Difficulty,
        monsterType: MonsterType,
        treasureClassOutcomeType: TreasureClassOutcomeType,
        nPlayers: Int,
        partySize: Int,
        function: (TreasureClassPaths, Monster, OutcomeType) -> List<ApiResponse>
    ) = monsterLibrary.getMonsters(monsterId, difficulty, monsterType).flatMap { monster ->
        val treasureClassPaths: TreasureClassPaths =
            treasureClassCalculator.getLeafOutcomes(
                monster.treasureClass,
                monster.level,
                difficulty,
                treasureClassOutcomeType,
                nPlayers,
                partySize
            )
        treasureClassPaths.flatMap { function(treasureClassPaths, monster, it) }
    }.sortedBy { it.name }

    //    https://dropcalc.silospen.com/cgi-bin/pyDrop.cgi?
//    type=item&itemName=aegis&diff=A&monClass=regMon&nPlayers=1&nGroup=1&mf=0&quality=regItem&decMode=false&version=112

    @GetMapping("/item")
    fun getItemProbabilities(
        @RequestParam("itemId", required = true) itemId: String,
        @RequestParam("itemQuality", required = true) itemQuality: ItemQuality,
        @RequestParam("difficulty", required = true) difficulty: Difficulty,
        @RequestParam("players", required = true) nPlayers: Int,
        @RequestParam("party", required = true) partySize: Int,
        @RequestParam("magicFind", required = true) magicFind: Int,
    ) {
//        val item: Item = itemLibrary.getItem(itemId, itemQuality)
    }

}

data class ApiResponse(val name: String, val area: String, val prob: Probability)

data class Probability(val frac: String, val dec: Double) {
    constructor(fraction: BigFraction) : this(
        "${fraction.numerator}/${fraction.denominator}",
        fraction.bigDecimalValue(20, RoundingMode.HALF_UP.ordinal).toDouble()
    )
}