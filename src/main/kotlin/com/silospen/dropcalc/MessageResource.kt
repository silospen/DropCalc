package com.silospen.dropcalc

import com.silospen.dropcalc.monsters.MonsterLibrary
import org.apache.commons.math3.fraction.Fraction
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class MessageResource(
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
        return monsterLibrary.getMonsterByClassId(monsterId).flatMap { monster ->
            monster.areas.flatMap { area ->
                val treasureClass: TreasureClass? = monster.getTreasureClass(monsterType, difficulty) //NEEDS AREA
                val leafOutcomes: Map<ItemClass, Fraction> =
                    treasureClassCalculator.getLeafOutcomes(treasureClass!!, nPlayers, partySize)
                leafOutcomes.map { leafOutcome ->
                    AtomicTcsResponse(
                        leafOutcome.key.name,
                        area.id,
                        Probability(leafOutcome.value)
                    )
                }
            }
        }
    }
}

data class AtomicTcsResponse(val tc: String, val area: String, val prob: Probability)

data class Probability(val frac: String, val dec: Double) {
    constructor(fraction: Fraction) : this("${fraction.numerator}/${fraction.denominator}", fraction.toDouble())
}