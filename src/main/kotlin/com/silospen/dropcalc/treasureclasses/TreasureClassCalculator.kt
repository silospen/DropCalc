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
        val initialOutcome = Outcome(
            treasureClass,
            1
        )
        val leafAccumulator = TreasureClassPathAccumulator()
        doRecursiveStuff(
            initialOutcome,
            Probability.ONE,
            Probability.ONE,
            treasureClass.properties.itemQualityRatios,
            leafAccumulator,
            treasureClassOutcomeType,
            nPlayers,
            partySize,
            1,
            filterToOutcomeType
        )
        return TreasureClassPaths.forMultiplePaths(leafAccumulator.getOutcomes())
    }

    fun changeTcBasedOnLevel(
        baseTreasureClass: TreasureClass,
        monsterLevel: Int,
        difficulty: Difficulty
    ): TreasureClass {
        if (difficulty == Difficulty.NORMAL) return baseTreasureClass
        val treasureClassGroup = treasureClassesByGroup[baseTreasureClass.properties.group] ?: return baseTreasureClass
        val baseTcIndex = treasureClassGroup.indexOf(baseTreasureClass)
        var nextIndex = baseTcIndex + 1
        var result = baseTreasureClass
        while (nextIndex < treasureClassGroup.size) {
            if (treasureClassGroup[nextIndex].properties.level!! > monsterLevel) break
            result = treasureClassGroup[nextIndex]
            nextIndex++
        }
        return result
    }

    fun getTreasureClass(treasureClassName: String) =
        treasureClassesByName.getOrDefault(treasureClassName, VirtualTreasureClass(treasureClassName))

    private fun doRecursiveStuff(
        outcome: Outcome,
        outcomeChance: Probability,
        pathProbabilityAccumulator: Probability,
        itemQualityRatiosAccumulator: ItemQualityRatios,
        leafAccumulator: TreasureClassPathAccumulator,
        treasureClassOutcomeType: TreasureClassOutcomeType,
        nPlayers: Int,
        partySize: Int,
        accumulatedPicks: Int,
        filterToOutcomeType: OutcomeType?
    ) {
        val selectionProbability = outcomeChance.multiply(pathProbabilityAccumulator)
        val outcomeType = outcome.outcomeType
//        println("RECURSE: ${outcomeType.name}, picks: $accumulatedPicks, chance: ${outcomeChance.toDouble()}, prob: ${selectionProbability.toDouble()}")
//        println("ACC: ${leafAccumulator.getOutcomes().map { it.mapKeys { it.key.name }.mapValues { it.value.probability.toDouble() } }}" )
        if ((outcomeType is VirtualTreasureClass && treasureClassOutcomeType == DEFINED) || (outcomeType is BaseItem && treasureClassOutcomeType == VIRTUAL)) {
            if (filterToOutcomeType == null || filterToOutcomeType == outcomeType) leafAccumulator.accumulateProbability(
                selectionProbability,
                itemQualityRatiosAccumulator,
                outcomeType,
                accumulatedPicks
            )
        } else if (outcomeType is TreasureClass) {
            val denominatorWithNoDrop = calculateDenominatorWithNoDrop(
                outcomeType.probabilityDenominator,
                outcomeType.properties.noDrop,
                nPlayers,
                partySize
            )
            val itemQualityRatios = itemQualityRatiosAccumulator.merge(outcomeType.properties.itemQualityRatios)
            val negativePicks = outcomeType.properties.picks < 0
            outcomeType.outcomes.forEach {
                val adjustedPicks =
                    if (negativePicks) it.probability else outcomeType.properties.picks * accumulatedPicks
                val adjustedOutcomeChance =
                    if (negativePicks) Probability.ONE else Probability(it.probability, denominatorWithNoDrop)
                val adjustedLeafAccumulator = if (negativePicks) leafAccumulator.forkPath() else leafAccumulator
                doRecursiveStuff(
                    it,
                    adjustedOutcomeChance,
                    selectionProbability,
                    itemQualityRatios,
                    adjustedLeafAccumulator,
                    treasureClassOutcomeType,
                    nPlayers,
                    partySize,
                    adjustedPicks,
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