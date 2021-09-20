package com.silospen.dropcalc.treasureclasses

import com.silospen.dropcalc.ItemQualityRatios
import com.silospen.dropcalc.OutcomeType
import org.apache.commons.math3.fraction.BigFraction

class TreasureClassPathAccumulator(
    private val accumulator: MutableMap<OutcomeType, TreasureClassPathOutcome>,
    private val picks: Int,
    private val drops: Int
) {
    constructor(picks: Int, drops: Int) : this(mutableMapOf(), picks, drops)

    fun accumulateProbability(
        probability: BigFraction,
        itemQualityRatios: ItemQualityRatios,
        outcomeType: OutcomeType
    ) {
        val old = accumulator[outcomeType]
        val newProbability = probability.add(old?.probability ?: BigFraction.ZERO)
        val newItemQualityRatios = (old?.itemQualityRatios ?: ItemQualityRatios.EMPTY).merge(itemQualityRatios)
        accumulator[outcomeType] = TreasureClassPathOutcome(newProbability, newItemQualityRatios, picks, drops)
    }

    fun getOutcomes(): Map<OutcomeType, TreasureClassPathOutcome> = accumulator

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TreasureClassPathAccumulator

        if (accumulator != other.accumulator) return false

        return true
    }

    override fun hashCode(): Int {
        return accumulator.hashCode()
    }

    override fun toString(): String {
        return "TreasureClassPathAccumulator(accumulator=$accumulator)"
    }
}

class TreasureClassPaths(private val pathsByOutcomeType: Map<OutcomeType, List<TreasureClassPathOutcome>>) :
    Iterable<OutcomeType> {

    companion object {
        fun forSinglePath(path: Map<OutcomeType, TreasureClassPathOutcome>) =
            TreasureClassPaths(path.mapValues { listOf(it.value) })

        fun forMultiplePaths(paths: List<Map<OutcomeType, TreasureClassPathOutcome>>): TreasureClassPaths {
            return TreasureClassPaths(paths.asSequence().flatMap { path ->
                path.entries.asSequence().map { it.key to it.value }
            }.groupBy({ it.first }) { it.second })
        }
    }

    fun not(bigFraction: BigFraction): BigFraction = BigFraction.ONE.subtract(bigFraction)

    private fun mergeFinals(
        outcomes: List<TreasureClassPathOutcome>,
        additionalFactorGenerator: (ItemQualityRatios) -> BigFraction
    ): BigFraction {
        return not(outcomes
            .map { it.getProbability(additionalFactorGenerator(it.itemQualityRatios)) }
            .map { not(it) }
            .reduce { acc, bigFraction -> acc.multiply(bigFraction) }
        )
    }

    fun getSubPaths(outcomeType: OutcomeType) = pathsByOutcomeType.getOrDefault(outcomeType, emptyList())

    fun getFinalProbability(
        outcomeType: OutcomeType,
        additionalFactorGenerator: (ItemQualityRatios) -> BigFraction = { BigFraction.ONE }
    ) = mergeFinals(pathsByOutcomeType.getOrDefault(outcomeType, emptyList()), additionalFactorGenerator)

    override fun iterator() = pathsByOutcomeType.keys.iterator()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TreasureClassPaths

        if (pathsByOutcomeType != other.pathsByOutcomeType) return false

        return true
    }

    override fun hashCode(): Int {
        return pathsByOutcomeType.hashCode()
    }

    override fun toString(): String {
        return "TreasureClassPath(pathsByOutcomeType=$pathsByOutcomeType)"
    }
}

data class TreasureClassPathOutcome(
    internal val probability: BigFraction,
    val itemQualityRatios: ItemQualityRatios,
    private val picks: Int,
    private val drops: Int
) {
    fun getProbability(additionalFactor: BigFraction = BigFraction.ONE) =
        applyPicks(drops, applyPicks(picks, probability.multiply(additionalFactor)))

    private fun applyPicks(picks: Int, p: BigFraction): BigFraction {
        return when {
            picks == 1 -> p
            picks > 1 -> calculateProbabilityForPicks(p, if (picks > 6) 6 else picks)
            else -> throw IllegalArgumentException("Unexpected picks: $picks")
        }
    }

    private fun calculateProbabilityForPicks(p: BigFraction, picks: Int) =
        BigFraction.ONE.subtract(BigFraction.ONE.subtract(p).pow(picks))
}