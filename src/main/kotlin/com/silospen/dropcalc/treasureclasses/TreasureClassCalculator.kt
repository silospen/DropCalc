package com.silospen.dropcalc.treasureclasses

import com.silospen.dropcalc.*
import com.silospen.dropcalc.treasureclasses.TreasureClassOutcomeType.DEFINED
import com.silospen.dropcalc.treasureclasses.TreasureClassOutcomeType.VIRTUAL
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.floor
import kotlin.math.pow

class TreasureClassCalculator(private val treasureClassLibrary: TreasureClassLibrary) {

    fun getLeafOutcomes(
        treasureClassName: String,
        treasureClassOutcomeType: TreasureClassOutcomeType,
        filterToOutcomeType: OutcomeType?,
        nPlayers: Int = 1,
        partySize: Int = 1
    ): TreasureClassPaths {
        val treasureClass = treasureClassLibrary.getTreasureClass(treasureClassName)
        val initialOutcome = Outcome(
            treasureClass,
            1
        )
        val leafAccumulator = TreasureClassPathAccumulator()
        recurseToLeaves(
            initialOutcome,
            1,
            1,
            1,
            Probability.ONE,
            treasureClass.properties.itemQualityRatios,
            leafAccumulator,
            treasureClassOutcomeType,
            nPlayers,
            partySize,
            1,
            filterToOutcomeType
        )
        return TreasureClassPaths.of(leafAccumulator.getOutcomes())
    }

    private fun recurseToLeaves(
        currentOutcome: Outcome,
        selectionNumerator: Int,
        selectionDenominator: Int,
        parentPicks: Int,
        pathProbabilityAccumulator: Probability,
        itemQualityRatiosAccumulator: ItemQualityRatios,
        leafAccumulator: TreasureClassPathAccumulator,
        treasureClassOutcomeType: TreasureClassOutcomeType,
        nPlayers: Int,
        partySize: Int,
        accumulatedPicks: Int,
        filterToOutcomeType: OutcomeType?
    ) {

        val outcomeType = currentOutcome.outcomeType
        val configuredPicks =
            if (outcomeType is TreasureClass) outcomeType.properties.picks else 1
//        println("INPUTS: ${currentOutcome.outcomeType.name}, picks:$configuredPicks, parentPicks:$parentPicks, accPicks:$accumulatedPicks, numerator: $selectionNumerator, denominator: $selectionDenominator")

        val parentPicksNegative = parentPicks < 0
        val adjustedPicks =
            if (configuredPicks < 0) accumulatedPicks else configuredPicks
        val updatedAccumulatedPicks =
            if (parentPicks < 0) selectionNumerator * accumulatedPicks * adjustedPicks else accumulatedPicks * adjustedPicks
        val selectionProbability =
            if (parentPicksNegative) Probability.ONE else Probability(
                selectionNumerator,
                selectionDenominator
            ).multiply(
                pathProbabilityAccumulator
            )
        if (parentPicksNegative) leafAccumulator.forkPath()
        val itemQualityRatios =
            if (outcomeType is TreasureClass) itemQualityRatiosAccumulator.merge(outcomeType.properties.itemQualityRatios) else itemQualityRatiosAccumulator
//        println("VALUES: ${currentOutcome.outcomeType.name}, actualPicks: $adjustedPicks, upAccPicks: $updatedAccumulatedPicks, actualChance: $selectionProbability")

        if ((outcomeType is VirtualTreasureClass && treasureClassOutcomeType == DEFINED) || ((outcomeType is BaseItem || outcomeType is Item) && treasureClassOutcomeType == VIRTUAL)) {
            if (filterToOutcomeType == null || filterToOutcomeType == outcomeType) leafAccumulator.accumulateProbability(
                selectionProbability,
                itemQualityRatios,
                outcomeType,
                updatedAccumulatedPicks
            )
//            println(
//                "ACCUMU: ${
//                    leafAccumulator.getOutcomes()
//                        .map { it.mapKeys { it.key.name }.mapValues { it.value.probability.toDouble() } }
//                }\n"
//            )
        } else if (outcomeType is TreasureClass) {
            val denominatorWithNoDrop = calculateDenominatorWithNoDrop(
                outcomeType.probabilityDenominator,
                outcomeType.properties.noDrop,
                nPlayers,
                partySize
            )
            outcomeType.outcomes.forEach {
                recurseToLeaves(
                    it,
                    it.probability,
                    denominatorWithNoDrop,
                    configuredPicks,
                    selectionProbability,
                    itemQualityRatios,
                    leafAccumulator,
                    treasureClassOutcomeType,
                    nPlayers,
                    partySize,
                    updatedAccumulatedPicks,
                    filterToOutcomeType,
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