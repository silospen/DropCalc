package com.silospen.dropcalc.treasureclasses

import com.silospen.dropcalc.ItemQualityRatios.Companion.EMPTY
import com.silospen.dropcalc.VirtualTreasureClass
import org.apache.commons.math3.fraction.BigFraction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TreasureClassPathAccumulatorTest {

    @Test
    fun accumulate() {
        val accumulator = TreasureClassPathAccumulator(1, 1)
        accumulator.accumulateProbability(BigFraction(1, 2), EMPTY, VirtualTreasureClass("tc1"))
        assertEquals(
            mapOf(VirtualTreasureClass("tc1") to TreasureClassPathOutcome(BigFraction(1, 2), EMPTY, 1, 1)),
            accumulator.getOutcomes()
        )
        accumulator.accumulateProbability(BigFraction(1, 2), EMPTY, VirtualTreasureClass("tc1"))
        assertEquals(
            mapOf(VirtualTreasureClass("tc1") to TreasureClassPathOutcome(BigFraction(1), EMPTY, 1, 1)),
            accumulator.getOutcomes()
        )
        accumulator.accumulateProbability(BigFraction(1, 4), EMPTY, VirtualTreasureClass("tc2"))
        assertEquals(
            mapOf(
                VirtualTreasureClass("tc1") to TreasureClassPathOutcome(BigFraction(1), EMPTY, 1, 1),
                VirtualTreasureClass("tc2") to TreasureClassPathOutcome(BigFraction(1, 4), EMPTY, 1, 1)
            ), accumulator.getOutcomes()
        )
    }
}