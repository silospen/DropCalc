package com.silospen.dropcalc

import org.apache.commons.math3.fraction.BigFraction
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.floor
import kotlin.math.pow

@Component
class TreasureClassCalculator(treasureClassConfigs: List<TreasureClassConfig>) {
    private val treasureClasses: List<TreasureClass> = generateTreasureClasses(treasureClassConfigs)
    private val treasureClassesByName: Map<String, TreasureClass> = treasureClasses.associateBy { it.name }
    private val treasureClassesByGroup = treasureClasses
        .filter { it.properties.group != null && it.properties.level != null }
        .groupBy { it.properties.group }
        .mapValues { entry -> entry.value.sortedBy { it.properties.level } }

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

    fun getLeafOutcomes(
        treasureClassName: String,
        monsterLevel: Int,
        difficulty: Difficulty,
        nPlayers: Int = 1,
        partySize: Int = 1
    ): Map<ItemClass, BigFraction> =
        getLeafOutcomes(getTreasureClass(treasureClassName), monsterLevel, difficulty, nPlayers, partySize)

    fun getLeafOutcomes(
        treasureClass: TreasureClass,
        monsterLevel: Int,
        difficulty: Difficulty,
        nPlayers: Int = 1,
        partySize: Int = 1
    ): Map<ItemClass, BigFraction> {
        val result = mutableMapOf<ItemClass, BigFraction>()
        val possiblyUpgradedTreasureClass = changeTcBasedOnLevel(treasureClass, monsterLevel, difficulty)
        calculatePathSum(Outcome(possiblyUpgradedTreasureClass, 1), BigFraction(1), 1, result, nPlayers, partySize)
        return result
    }

    fun changeTcBasedOnLevel(treasureClass: TreasureClass, monsterLevel: Int, difficulty: Difficulty): TreasureClass =
        if (difficulty == Difficulty.NORMAL) treasureClass else treasureClassesByGroup[treasureClass.properties.group]?.lastOrNull { it.properties.level!! <= monsterLevel }
            ?: treasureClass

    fun getTreasureClass(treasureClassName: String) = treasureClassesByName.getValue(treasureClassName)

    private fun calculatePathSum(
        outcome: Outcome,
        pathProbabilityAccumulator: BigFraction,
        tcProbabilityDenominator: Int,
        leafAccumulator: MutableMap<ItemClass, BigFraction>,
        nPlayers: Int,
        partySize: Int
    ) {
        val outcomeChance = BigFraction(outcome.probability, tcProbabilityDenominator)
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
                leafAccumulator[outcomeType] =
                    selectionProbability.add(leafAccumulator.getOrDefault(outcomeType, BigFraction.ZERO))
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
    val baseNoDropRate: Double = noDrop.toDouble() / (noDrop + tcProbabilityDenominator)
    val newNoDropRateBd: BigDecimal = baseNoDropRate.pow(noDropExponent).toBigDecimal()
    val newNoDropNumBd: BigDecimal =
        (newNoDropRateBd.divide(BigDecimal.ONE.minus(newNoDropRateBd), RoundingMode.HALF_UP)).multiply(
            tcProbabilityDenominator.toBigDecimal()
        )
    return floor(newNoDropNumBd.toDouble()).toInt()
}