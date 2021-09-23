package com.silospen.dropcalc.treasureclasses

import com.silospen.dropcalc.*
import com.silospen.dropcalc.items.ItemLibrary
import com.silospen.dropcalc.treasureclasses.TreasureClassOutcomeType.DEFINED
import com.silospen.dropcalc.treasureclasses.TreasureClassOutcomeType.VIRTUAL
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.floor
import kotlin.math.pow

class TreasureClassCalculator(treasureClassConfigs: List<TreasureClassConfig>, private val itemLibrary: ItemLibrary) {
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
                DefinedTreasureClass(
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
            Outcome(itemLibrary.getOrConstructVirtualTreasureClass(itemAndProbability.first), itemAndProbability.second)
        }
    }

    fun getLeafOutcomes(
        treasureClassName: String,
        treasureClassOutcomeType: TreasureClassOutcomeType,
        filterToOutcomeType: OutcomeType?,
        nPlayers: Int = 1,
        partySize: Int = 1
    ): TreasureClassPaths {
        val treasureClass = getTreasureClass(treasureClassName)
        return if (treasureClass.properties.picks > 0) getLeafOutcomesForPositivePicks(
            treasureClass,
            treasureClassOutcomeType,
            nPlayers,
            partySize,
            treasureClass.properties.picks,
            filterToOutcomeType
        ) else getLeafOutcomesForNegativePicks(
            treasureClass,
            treasureClassOutcomeType,
            nPlayers,
            partySize,
            filterToOutcomeType
        )
    }

    private fun getLeafOutcomesForPositivePicks(
        treasureClass: TreasureClass,
        treasureClassOutcomeType: TreasureClassOutcomeType,
        nPlayers: Int,
        partySize: Int,
        picks: Int,
        filterToOutcomeType: OutcomeType?
    ) = TreasureClassPaths.forSinglePath(
        calculatePathSum(
            Outcome(treasureClass, 1),
            Probability.ONE,
            treasureClass.properties.itemQualityRatios,
            1,
            treasureClassOutcomeType,
            nPlayers,
            partySize,
            picks,
            1,
            filterToOutcomeType
        )
    )

    private fun getLeafOutcomesForNegativePicks(
        treasureClass: TreasureClass,
        treasureClassOutcomeType: TreasureClassOutcomeType,
        nPlayers: Int,
        partySize: Int,
        filterToOutcomeType: OutcomeType?
    ): TreasureClassPaths =
//        var picksCounter = treasureClass.properties.picks //TODO: Use this!
        TreasureClassPaths.forMultiplePaths(
            treasureClass.outcomes
                .map { outcome ->
                    val (picks, itemQualityRatios) = if (outcome.outcomeType is TreasureClass) {
                        outcome.outcomeType.properties.picks to outcome.outcomeType.properties.itemQualityRatios
                    } else {
                        1 to ItemQualityRatios.EMPTY
                    }
                    calculatePathSum(
                        outcome,
                        Probability.ONE,
                        treasureClass.properties.itemQualityRatios.merge(itemQualityRatios),
                        outcome.probability,
                        treasureClassOutcomeType,
                        nPlayers,
                        partySize,
                        picks,
                        outcome.probability,
                        filterToOutcomeType
                    )
                }
        )

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

    fun getTreasureClass(treasureClassName: String) =
        treasureClassesByName.getOrDefault(treasureClassName, VirtualTreasureClass(treasureClassName))

    private fun calculatePathSum(
        outcome: Outcome,
        pathProbabilityAccumulator: Probability,
        itemQualityRatiosAccumulator: ItemQualityRatios,
        tcProbabilityDenominator: Int,
        treasureClassOutcomeType: TreasureClassOutcomeType,
        nPlayers: Int,
        partySize: Int,
        picks: Int,
        drops: Int,
        filterToOutcomeType: OutcomeType?
    ): Map<OutcomeType, TreasureClassPathOutcome> =
        TreasureClassPathAccumulator(picks, drops).apply {
            calculatePathSumRecurse(
                outcome,
                pathProbabilityAccumulator,
                itemQualityRatiosAccumulator,
                tcProbabilityDenominator,
                this,
                treasureClassOutcomeType,
                nPlayers,
                partySize,
                filterToOutcomeType
            )
        }.getOutcomes()

    private fun calculatePathSumRecurse(
        outcome: Outcome,
        pathProbabilityAccumulator: Probability,
        itemQualityRatiosAccumulator: ItemQualityRatios,
        tcProbabilityDenominator: Int,
        leafAccumulator: TreasureClassPathAccumulator,
        treasureClassOutcomeType: TreasureClassOutcomeType,
        nPlayers: Int,
        partySize: Int,
        filterToOutcomeType: OutcomeType?
    ) {
        val outcomeChance = Probability(outcome.probability, tcProbabilityDenominator)
        val selectionProbability = outcomeChance.multiply(pathProbabilityAccumulator)
        val outcomeType = outcome.outcomeType
        if ((outcomeType is VirtualTreasureClass && treasureClassOutcomeType == DEFINED) || (outcomeType is BaseItem && treasureClassOutcomeType == VIRTUAL)) {
            if (filterToOutcomeType == null || filterToOutcomeType == outcomeType) leafAccumulator.accumulateProbability(
                selectionProbability,
                itemQualityRatiosAccumulator,
                outcomeType
            )
        } else if (outcomeType is TreasureClass) {
            val denominatorWithNoDrop = calculateDenominatorWithNoDrop(
                outcomeType.probabilityDenominator,
                outcomeType.properties.noDrop,
                nPlayers,
                partySize
            )
            val itemQualityRatios = itemQualityRatiosAccumulator.merge(outcomeType.properties.itemQualityRatios)
            outcomeType.outcomes.forEach {
                calculatePathSumRecurse(
                    it,
                    selectionProbability,
                    itemQualityRatios,
                    denominatorWithNoDrop,
                    leafAccumulator,
                    treasureClassOutcomeType,
                    nPlayers,
                    partySize,
                    filterToOutcomeType
                )
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

enum class TreasureClassOutcomeType {
    DEFINED,
    VIRTUAL
}

internal fun calculateNoDrop(
    tcProbabilityDenominator: Int,
    noDrop: Int?,
    nPlayers: Int,
    partySize: Int
): Int {
    if (noDrop == null || noDrop < 1) return 0
    if (nPlayers <= 1) return noDrop
    val noDropExponent = floor(1 + ((nPlayers - 1) / 2.0) + ((partySize - 1) / 2.0)).toInt()
    val baseNoDropRate: Double = noDrop.toDouble() / (noDrop + tcProbabilityDenominator)
    val newNoDropRateBd: BigDecimal = baseNoDropRate.pow(noDropExponent).toBigDecimal()
    val newNoDropNumBd: BigDecimal =
        (newNoDropRateBd.divide(BigDecimal.ONE.minus(newNoDropRateBd), RoundingMode.HALF_UP)).multiply(
            tcProbabilityDenominator.toBigDecimal()
        )
    return floor(newNoDropNumBd.toDouble()).toInt()
}