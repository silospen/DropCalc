package com.silospen.dropcalc.treasureclasses

import com.silospen.dropcalc.ItemQualityRatios
import com.silospen.dropcalc.OutcomeType
import org.apache.commons.math3.fraction.BigFraction

class TreasureClassPathAccumulator(private val accumulator: MutableMap<OutcomeType, TreasureClassPathOutcome>) {
    constructor() : this(mutableMapOf())

    fun accumulateProbability(
        probability: BigFraction,
        itemQualityRatios: ItemQualityRatios,
        outcomeType: OutcomeType
    ) {
        val old = accumulator[outcomeType]
        val newProbability = probability.add(old?.probability ?: BigFraction.ZERO)
        val newItemQualityRatios = (old?.itemQualityRatios ?: ItemQualityRatios.EMPTY).merge(itemQualityRatios)
        accumulator[outcomeType] = TreasureClassPathOutcome(newProbability, newItemQualityRatios)
    }

    fun merge(other: TreasureClassPathAccumulator): TreasureClassPathAccumulator {
        val merged = accumulator.toMutableMap()
        for (entry in other.accumulator) {
            merged.merge(entry.key, entry.value) { old, new ->
                TreasureClassPathOutcome(
                    BigFraction.ONE.subtract(
                        old.probability.subtract(1).negate().multiply(BigFraction.ONE.subtract(new.probability))
                    ),
                    old.itemQualityRatios.merge(new.itemQualityRatios)
                )
            }
        }
        return TreasureClassPathAccumulator(merged)
    }

    fun applyPicks(picks: Int): TreasureClassPathAccumulator {
        return when {
            picks == 1 -> this
            picks > 1 -> TreasureClassPathAccumulator(accumulator.mapValuesTo(mutableMapOf()) {
                TreasureClassPathOutcome(
                    calculateProbabilityForPicks(
                        it.value.probability,
                        if (picks > 6) 6 else picks
                    ), it.value.itemQualityRatios
                )
            })
            else -> throw IllegalArgumentException("Unexpected picks: $picks")
        }
    }

    fun getOutcomes(): Map<OutcomeType, TreasureClassPathOutcome> = accumulator

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

data class TreasureClassPathOutcome(
    val probability: BigFraction,
    val itemQualityRatios: ItemQualityRatios
)