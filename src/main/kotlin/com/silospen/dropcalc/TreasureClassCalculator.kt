package com.silospen.dropcalc

import org.apache.commons.math3.fraction.Fraction
import java.lang.RuntimeException

class TreasureClassCalculator(treasureClassConfigs: List<TreasureClassConfig>) {

//    private val treasureClassesByName = treasureClassConfigs.associateBy { it.name }

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

    fun getLeafOutcomes(treasureClassName: String): Map<ItemClass, Fraction> {
        val treasureClass = treasureClassesByName.getValue(treasureClassName)
        val result = mutableMapOf<ItemClass, Fraction>()
        calculatePathSum(Outcome(treasureClass, 1), Fraction(1), 1, result)
        return result
    }

    private fun calculatePathSum(
        outcome: Outcome,
        pathProbabilityAccumulator: Fraction,
        tcProbabilityDenominator: Int,
        leafAccumulator: MutableMap<ItemClass, Fraction>
    ) {
        val outcomeChance = Fraction(outcome.probability, tcProbabilityDenominator)
        val selectionProbability = outcomeChance.multiply(pathProbabilityAccumulator)
        when (val outcomeType = outcome.outcomeType) {
            is TreasureClass -> {
                outcomeType.outcomes.forEach {
                    calculatePathSum(
                        it,
                        selectionProbability,
                        calculateDenominatorWithNoDrop(
                            outcomeType.probabilityDenominator,
                            outcomeType.properties.noDrop
                        ),
                        leafAccumulator
                    )
                }
            }
            is ItemClass -> {
                val put = leafAccumulator.put(outcomeType, selectionProbability)
                if (put != null) throw RuntimeException("${outcome.outcomeType} already in map?")
            }
        }
    }

    private fun calculateDenominatorWithNoDrop(tcProbabilityDenominator: Int, noDrop: Int?): Int =
        noDrop?.let { tcProbabilityDenominator + noDrop } ?: tcProbabilityDenominator
}