package com.silospen.dropcalc

import org.apache.commons.math3.fraction.Fraction
import org.springframework.stereotype.Component
import java.lang.RuntimeException
import kotlin.math.floor
import kotlin.math.pow

@Component
class TreasureClassCalculator(treasureClassConfigs: List<TreasureClassConfig>) {
    private val treasureClasses = generateTreasureClasses(treasureClassConfigs)
    private val treasureClassesByName = treasureClasses.associateBy { it.name }

    private fun generateTreasureClasses(treasureClassConfigs: List<TreasureClassConfig>): List<TreasureClass> {
        val treasureClasses = mutableListOf<TreasureClass>()
        for (treasureClassConfig in treasureClassConfigs) {
            treasureClasses.add(
                TreasureClass(
                    treasureClassConfig.name,
                    treasureClassConfig.items.sumOf { it.second },
                    treasureClassConfig.properties,
                    treasureClassConfig.items.map { item -> toOutcome(item, treasureClasses) }.toSet()
                )
            )
        }
        return treasureClasses
    }

    private fun toOutcome(itemAndProbability: Pair<String, Int>, treasureClasses: List<TreasureClass>): Outcome {
        val treasureClass = treasureClasses.find { it.name == itemAndProbability.first }
        return if (treasureClass != null) {
            Outcome(treasureClass, itemAndProbability.second)
        } else {
            Outcome(ItemClass(itemAndProbability.first), itemAndProbability.second)
        }
    }

    fun getLeafOutcomes(treasureClassName: String, nPlayers: Int = 1, partySize: Int = 1): Map<ItemClass, Fraction> =
        getLeafOutcomes(getTreasureClass(treasureClassName), nPlayers, partySize)

    fun getLeafOutcomes(treasureClass: TreasureClass, nPlayers: Int = 1, partySize: Int = 1): Map<ItemClass, Fraction> {
        val result = mutableMapOf<ItemClass, Fraction>()
        calculatePathSum(Outcome(treasureClass, 1), Fraction(1), 1, result, nPlayers, partySize)
        return result
    }

    fun getTreasureClass(treasureClassName: String) =
        treasureClassesByName.getValue(treasureClassName) //TODO: Add logic for MLVL upgrade here

    private fun calculatePathSum(
        outcome: Outcome,
        pathProbabilityAccumulator: Fraction,
        tcProbabilityDenominator: Int,
        leafAccumulator: MutableMap<ItemClass, Fraction>,
        nPlayers: Int,
        partySize: Int
    ) {
        val outcomeChance = Fraction(outcome.probability, tcProbabilityDenominator)
        val selectionProbability = outcomeChance.multiply(pathProbabilityAccumulator)
        when (val outcomeType = outcome.outcomeType) {
            is TreasureClass -> {
                val denominatorWithNoDrop = calculateDenominatorWithNoDrop(
                    outcomeType.probabilityDenominator,
                    outcomeType.properties.noDrop,
                    nPlayers,
                    partySize
                )
                outcomeType.outcomes.forEach {
                    calculatePathSum(
                        it,
                        selectionProbability,
                        denominatorWithNoDrop,
                        leafAccumulator,
                        nPlayers,
                        partySize
                    )
                }
            }
            is ItemClass -> {
                val put = leafAccumulator.put(outcomeType, selectionProbability)
                if (put != null) throw RuntimeException("${outcome.outcomeType} already in map?") // EG FOR SUMMONER!
            }
        }
    }

    private fun calculateDenominatorWithNoDrop(
        tcProbabilityDenominator: Int,
        noDrop: Int?,
        nPlayers: Int,
        partySize: Int
    ): Int {
        return tcProbabilityDenominator + calculateNoDrop(tcProbabilityDenominator, noDrop, nPlayers, partySize)
    }
}

internal fun calculateNoDrop(
    tcProbabilityDenominator: Int,
    noDrop: Int?,
    nPlayers: Int,
    partySize: Int
): Int {
    if (noDrop == null || noDrop < 1) return 0
    val noDropExponent = floor(1 + ((nPlayers - 1) / 2.0) + ((partySize - 1) / 2.0)).toInt()
    val pow = (noDrop.toDouble() / (tcProbabilityDenominator + noDrop)).pow(noDropExponent)
    return floor((pow / (1 - pow)) * tcProbabilityDenominator).toInt()
}