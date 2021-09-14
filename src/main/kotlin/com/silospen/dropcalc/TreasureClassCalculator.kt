package com.silospen.dropcalc

import org.apache.commons.math3.fraction.BigFraction
import org.apache.commons.math3.fraction.BigFraction.ONE
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
            Outcome(ItemTreasureClass(itemAndProbability.first), itemAndProbability.second)
        }
    }

    fun getLeafOutcomes(
        treasureClassName: String,
        monsterLevel: Int,
        difficulty: Difficulty,
        nPlayers: Int = 1,
        partySize: Int = 1
    ): Map<ItemTreasureClass, BigFraction> =
        getLeafOutcomes(getTreasureClass(treasureClassName), monsterLevel, difficulty, nPlayers, partySize)

    fun getLeafOutcomes(
        treasureClass: TreasureClass,
        monsterLevel: Int,
        difficulty: Difficulty,
        nPlayers: Int = 1,
        partySize: Int = 1
    ): Map<ItemTreasureClass, BigFraction> {
        val possiblyUpgradedTreasureClass = changeTcBasedOnLevel(treasureClass, monsterLevel, difficulty)
        return if (possiblyUpgradedTreasureClass.properties.picks > 0) getLeafOutcomesForPositivePicks(
            possiblyUpgradedTreasureClass,
            nPlayers,
            partySize
        ) else getLeafOutcomesForNegativePicks(possiblyUpgradedTreasureClass, nPlayers, partySize)
    }

    private fun getLeafOutcomesForPositivePicks(
        treasureClass: TreasureClass,
        nPlayers: Int,
        partySize: Int
    ): Map<ItemTreasureClass, BigFraction> {
        val result = calculatePathSum(Outcome(treasureClass, 1), BigFraction(1), 1, nPlayers, partySize)
        val picks = treasureClass.properties.picks
        return applyPositivePicks(picks, result)
    }

    private fun applyPositivePicks(
        picks: Int,
        result: Map<ItemTreasureClass, BigFraction>
    ): Map<ItemTreasureClass, BigFraction> {
        return when {
            picks == 1 -> result
            picks > 1 -> result.mapValues { calculateProbabilityForPicks(it.value, if (picks > 6) 6 else picks) }
            else -> throw IllegalArgumentException("Unexpected picks: $picks")
        }
    }

    private fun calculateProbabilityForPicks(baseProb: BigFraction, picks: Int) =
        ONE.subtract(ONE.subtract(baseProb).pow(picks))

    private fun getLeafOutcomesForNegativePicks(
        treasureClass: TreasureClass,
        nPlayers: Int,
        partySize: Int
    ): Map<ItemTreasureClass, BigFraction> {
        val results = mutableListOf<Map<ItemTreasureClass, BigFraction>>()
        var picksCounter = treasureClass.properties.picks //TODO: Use this!
        for (outcome in treasureClass.outcomes) {
            val pathSumsForOutcome = calculatePathSum(outcome, BigFraction(1), outcome.probability, nPlayers, partySize)
                .mapValues { calculateProbabilityForPicks(it.value, outcome.probability) }
            val picks = if (outcome.outcomeType is TreasureClass) outcome.outcomeType.properties.picks else 1
            results.add(
                if (picks > 1) applyPositivePicks(picks, pathSumsForOutcome) else pathSumsForOutcome
            )
        }
        val finalResult = mutableMapOf<ItemTreasureClass, BigFraction>()
        for (result in results) {
            for (entry in result) {
                finalResult.merge(entry.key, entry.value) { old, new ->
                    ONE.subtract(old.subtract(1).negate().multiply(ONE.subtract(new)))
                }
            }
        }
        return finalResult
    }

    fun changeTcBasedOnLevel(
        baseTreasureClass: TreasureClass,
        monsterLevel: Int,
        difficulty: Difficulty
    ): TreasureClass {
        if (difficulty == Difficulty.NORMAL) return baseTreasureClass
        val treasureClassGroup = treasureClassesByGroup[baseTreasureClass.properties.group] ?: return baseTreasureClass
        var isAfterBaseTreasureClass = false
        for (treasureClass in treasureClassGroup) {
            if (treasureClass == baseTreasureClass) isAfterBaseTreasureClass = true
            if (treasureClass.properties.level!! >= monsterLevel && isAfterBaseTreasureClass) return treasureClass
        }
        return treasureClassGroup.last()
    }

    fun getTreasureClass(treasureClassName: String) = treasureClassesByName.getValue(treasureClassName)

    private fun calculatePathSum(
        outcome: Outcome,
        pathProbabilityAccumulator: BigFraction,
        tcProbabilityDenominator: Int,
        nPlayers: Int,
        partySize: Int
    ): Map<ItemTreasureClass, BigFraction> =
        mutableMapOf<ItemTreasureClass, BigFraction>().apply {
            calculatePathSumRecurse(
                outcome,
                pathProbabilityAccumulator,
                tcProbabilityDenominator,
                this,
                nPlayers,
                partySize
            )
        }

    private fun calculatePathSumRecurse(
        outcome: Outcome,
        pathProbabilityAccumulator: BigFraction,
        tcProbabilityDenominator: Int,
        leafAccumulator: MutableMap<ItemTreasureClass, BigFraction>,
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
                    calculatePathSumRecurse(
                        it,
                        selectionProbability,
                        denominatorWithNoDrop,
                        leafAccumulator,
                        nPlayers,
                        partySize
                    )
                }
            }
            is ItemTreasureClass -> {
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