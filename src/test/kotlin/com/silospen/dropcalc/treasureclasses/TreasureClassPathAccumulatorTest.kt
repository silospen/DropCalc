package com.silospen.dropcalc.treasureclasses

import com.silospen.dropcalc.ItemQualityRatios.Companion.EMPTY
import com.silospen.dropcalc.VirtualTreasureClass
import org.apache.commons.math3.fraction.BigFraction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TreasureClassPathAccumulatorTest {

    @Test
    fun accumulate() {
        val accumulator = TreasureClassPathAccumulator()
        accumulator.accumulateProbability(BigFraction(1, 2), EMPTY, VirtualTreasureClass("tc1"))
        assertEquals(
            mapOf(VirtualTreasureClass("tc1") to TreasureClassPathOutcome(BigFraction(1, 2), EMPTY)),
            accumulator.getOutcomes()
        )
        accumulator.accumulateProbability(BigFraction(1, 2), EMPTY, VirtualTreasureClass("tc1"))
        assertEquals(
            mapOf(VirtualTreasureClass("tc1") to TreasureClassPathOutcome(BigFraction(1), EMPTY)),
            accumulator.getOutcomes()
        )
        accumulator.accumulateProbability(BigFraction(1, 4), EMPTY, VirtualTreasureClass("tc2"))
        assertEquals(
            mapOf(
                VirtualTreasureClass("tc1") to TreasureClassPathOutcome(BigFraction(1), EMPTY),
                VirtualTreasureClass("tc2") to TreasureClassPathOutcome(BigFraction(1, 4), EMPTY)
            ), accumulator.getOutcomes()
        )
    }

    @Test
    fun merge() {
        val accumulator1 = TreasureClassPathAccumulator()
        val accumulator2 = TreasureClassPathAccumulator(
            mutableMapOf(
                VirtualTreasureClass("tc1") to TreasureClassPathOutcome(
                    BigFraction(1, 3),
                    EMPTY
                )
            )
        )
        val accumulator3 = TreasureClassPathAccumulator(
            mutableMapOf(
                VirtualTreasureClass("tc1") to TreasureClassPathOutcome(BigFraction(1, 3), EMPTY),
                VirtualTreasureClass("tc2") to TreasureClassPathOutcome(BigFraction(1, 3), EMPTY)
            )
        )
        assertEquals(accumulator2, accumulator1.merge(accumulator2))
        assertEquals(
            TreasureClassPathAccumulator(
                mutableMapOf(
                    VirtualTreasureClass("tc1") to TreasureClassPathOutcome(BigFraction(5, 9), EMPTY),
                    VirtualTreasureClass("tc2") to TreasureClassPathOutcome(BigFraction(1, 3), EMPTY)
                )
            ),
            accumulator2.merge(accumulator3)
        )
    }

    @Test
    fun applyPicks() {
        val accumulator = TreasureClassPathAccumulator(
            mutableMapOf(
                VirtualTreasureClass("tc1") to TreasureClassPathOutcome(
                    BigFraction(1, 3),
                    EMPTY
                )
            )
        )
        assertEquals(accumulator, accumulator.applyPicks(1))
        assertEquals(
            TreasureClassPathAccumulator(
                mutableMapOf(
                    VirtualTreasureClass("tc1") to TreasureClassPathOutcome(
                        BigFraction(5, 9),
                        EMPTY
                    )
                )
            ),
            accumulator.applyPicks(2)
        )
        assertEquals(
            TreasureClassPathAccumulator(
                mutableMapOf(
                    VirtualTreasureClass("tc1") to TreasureClassPathOutcome(
                        BigFraction(665, 729),
                        EMPTY
                    )
                )
            ),
            accumulator.applyPicks(6)
        )
    }
}