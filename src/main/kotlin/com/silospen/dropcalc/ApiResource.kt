package com.silospen.dropcalc

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
            println("${monster.monsterClass.id} - ${monster.difficulty.name} - ${monster.type.name} - ${monster.area.name} - ${monster.level}")
            val treasureClass: TreasureClass = monster.treasureClass
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
}

data class AtomicTcsResponse(val tc: String, val area: String, val prob: Probability)

data class Probability(val frac: String, val dec: Double) {
    constructor(fraction: BigFraction) : this(
        "${fraction.numerator}/${fraction.denominator}",
        fraction.bigDecimalValue(20, RoundingMode.HALF_UP.ordinal).toDouble()
    )
}