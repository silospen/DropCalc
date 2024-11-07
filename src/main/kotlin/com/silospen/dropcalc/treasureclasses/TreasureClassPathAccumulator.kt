package com.silospen.dropcalc.treasureclasses

import com.silospen.dropcalc.ItemQualityRatios
import com.silospen.dropcalc.OutcomeType
import com.silospen.dropcalc.Probability


class TreasureClassPathAccumulator {
    private val outcomes: MutableList<MutableMap<OutcomeType, TreasureClassPathOutcome>> = mutableListOf()

    init {
        outcomes.add(mutableMapOf())
    }

    fun accumulateProbability(
        probability: Probability,
        itemQualityRatios: ItemQualityRatios,
        outcomeType: OutcomeType,
        picks: Int
    ) {
        val accumulator = outcomes.last()
        val old = accumulator[outcomeType]
        val newProbability = probability.add(old?.probability ?: Probability.ZERO)
        val newItemQualityRatios = (old?.itemQualityRatios ?: ItemQualityRatios.EMPTY).merge(itemQualityRatios)
        accumulator[outcomeType] = TreasureClassPathOutcome(newProbability, newItemQualityRatios, picks)
    }

    fun forkPath(): TreasureClassPathAccumulator {
        if (outcomes.last().isNotEmpty()) outcomes.add(mutableMapOf())
        return this
    }

    fun getOutcomes(): List<Map<OutcomeType, TreasureClassPathOutcome>> = outcomes

    fun toHumanFriendlyString(): String {
        return outcomes.joinToString { acc ->
            acc.entries.joinToString(
                ",",
                prefix = "\n[",
                postfix = "]"
            ) { "${it.key.name}: ${it.value.probability.toDouble()}" }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TreasureClassPathAccumulator

        return outcomes == other.outcomes
    }

    override fun hashCode(): Int {
        return outcomes.hashCode()
    }

    override fun toString(): String {
        return "TreasureClassPathAccumulator(forkedPaths=$outcomes)"
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

    fun not(p: Probability): Probability = Probability.ONE.subtract(p)

    private fun mergeFinals(
        outcomes: List<TreasureClassPathOutcome>,
        additionalFactorGenerator: (ItemQualityRatios) -> Probability
    ): Probability {
        return not(outcomes
            .map { it.getProbability(additionalFactorGenerator(it.itemQualityRatios)) }
            .map { not(it) }
            .reduce { acc, Probability -> acc.multiply(Probability) }
        )
    }

    fun getSubPaths(outcomeType: OutcomeType) = pathsByOutcomeType.getOrDefault(outcomeType, emptyList())

    fun getFinalProbability(
        outcomeType: OutcomeType,
        additionalFactorGenerator: (ItemQualityRatios) -> Probability = { Probability.ONE }
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
    internal val probability: Probability,
    val itemQualityRatios: ItemQualityRatios,
    private val picks: Int
) {
    fun getProbability(additionalFactor: Probability = Probability.ONE) =
        applyPicks(picks, probability.multiply(additionalFactor))


    private fun applyPicks(picks: Int, p: Probability): Probability {
        return when {
            picks == 1 -> p
            picks > 1 -> calculateProbabilityForPicks(p, if (picks > 6) 6 else picks)
            else -> throw IllegalArgumentException("Unexpected picks: $picks")
        }
    }

    private fun calculateProbabilityForPicks(p: Probability, picks: Int) =
        Probability.ONE.subtract(Probability.ONE.subtract(p).pow(picks))
}