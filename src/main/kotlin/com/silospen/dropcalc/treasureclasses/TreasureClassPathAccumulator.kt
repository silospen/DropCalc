package com.silospen.dropcalc.treasureclasses

import com.silospen.dropcalc.OutcomeType
import org.apache.commons.math3.fraction.BigFraction

class TreasureClassPathAccumulator(private val accumulator: MutableMap<OutcomeType, BigFraction>) {
    constructor() : this(mutableMapOf())

    fun accumulateProbability(probability: BigFraction, outcomeType: OutcomeType) {
        accumulator[outcomeType] =
            probability.add(accumulator.getOrDefault(outcomeType, BigFraction.ZERO))
    }

    fun merge(other: TreasureClassPathAccumulator): TreasureClassPathAccumulator {
        val merged = accumulator.toMutableMap()
        for (entry in other.accumulator) {
            merged.merge(entry.key, entry.value) { old, new ->
                BigFraction.ONE.subtract(old.subtract(1).negate().multiply(BigFraction.ONE.subtract(new)))
            }
        }
        return TreasureClassPathAccumulator(merged)
    }

    fun applyPicks(picks: Int): TreasureClassPathAccumulator {
        return when {
            picks == 1 -> this
            picks > 1 -> TreasureClassPathAccumulator(accumulator.mapValuesTo(mutableMapOf()) {
                calculateProbabilityForPicks(
                    it.value,
                    if (picks > 6) 6 else picks
                )
            })
            else -> throw IllegalArgumentException("Unexpected picks: $picks")
        }
    }

    fun getOutcomes(): Map<OutcomeType, BigFraction> = accumulator

    private fun calculateProbabilityForPicks(baseProb: BigFraction, picks: Int) =
        BigFraction.ONE.subtract(BigFraction.ONE.subtract(baseProb).pow(picks))

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