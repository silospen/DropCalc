package com.silospen.dropcalc.resource

import com.silospen.dropcalc.*
import com.silospen.dropcalc.monsters.MonsterLibrary
import org.apache.commons.math3.fraction.BigFraction
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.RoundingMode

@RestController
class ApiResource(
    private val treasureClassCalculator: TreasureClassCalculator,
    private val monsterLibrary: MonsterLibrary
) {
    @GetMapping("/atomicTcs")
    fun getAtomicTcs(
        @RequestParam("monsterId", required = true) monsterId: String,
        @RequestParam("monsterType", required = true) monsterType: MonsterType,
        @RequestParam("difficulty", required = true) difficulty: Difficulty,
        @RequestParam("players", required = true) nPlayers: Int,
        @RequestParam("party", required = true) partySize: Int
    ): List<AtomicTcsResponse> {
        return monsterLibrary.getMonsters(monsterId, difficulty, monsterType).flatMap { monster ->
            val treasureClass: TreasureClass = monster.treasureClass
            println("${monster.monsterClass.id} - ${monster.difficulty.name} - ${monster.type.name} - ${monster.area.name} - ${monster.level} - $treasureClass")
            val leafOutcomes: Map<ItemClass, BigFraction> =
                treasureClassCalculator.getLeafOutcomes(treasureClass, monster.level, difficulty, nPlayers, partySize)
            leafOutcomes.map { leafOutcome ->
                AtomicTcsResponse(
                    leafOutcome.key.name,
                    monster.area.name,
                    Probability(leafOutcome.value)
                )
            }
        }.sortedBy { it.tc }
    }

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

data class AtomicTcsResponse(val tc: String, val area: String, val prob: Probability)

data class Probability(val frac: String, val dec: Double) {
    constructor(fraction: BigFraction) : this(
        "${fraction.numerator}/${fraction.denominator}",
        fraction.bigDecimalValue(20, RoundingMode.HALF_UP.ordinal).toDouble()
    )
}