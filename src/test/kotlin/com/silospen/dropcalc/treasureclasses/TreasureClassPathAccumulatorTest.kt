package com.silospen.dropcalc.treasureclasses

import com.silospen.dropcalc.VirtualTreasureClass
import org.apache.commons.math3.fraction.BigFraction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TreasureClassPathAccumulatorTest {

    @Test
    fun accumulate() {
        val accumulator = TreasureClassPathAccumulator()
        accumulator.accumulateProbability(BigFraction(1, 2), VirtualTreasureClass("tc1"))
        assertEquals(mapOf(VirtualTreasureClass("tc1") to BigFraction(1, 2)), accumulator.getOutcomes())
        accumulator.accumulateProbability(BigFraction(1, 2), VirtualTreasureClass("tc1"))
        assertEquals(mapOf(VirtualTreasureClass("tc1") to BigFraction(1)), accumulator.getOutcomes())
        accumulator.accumulateProbability(BigFraction(1, 4), VirtualTreasureClass("tc2"))
        assertEquals(
            mapOf(
                VirtualTreasureClass("tc1") to BigFraction(1),
                VirtualTreasureClass("tc2") to BigFraction(1, 4)
            ), accumulator.getOutcomes()
        )
    }

    @Test
    fun merge() {
        val accumulator1 = TreasureClassPathAccumulator()
        val accumulator2 = TreasureClassPathAccumulator(mutableMapOf(VirtualTreasureClass("tc1") to BigFraction(1, 3)))
        val accumulator3 = TreasureClassPathAccumulator(
            mutableMapOf(
                VirtualTreasureClass("tc1") to BigFraction(1, 3),
                VirtualTreasureClass("tc2") to BigFraction(1, 3)
            )
        )
        assertEquals(accumulator2, accumulator1.merge(accumulator2))
        assertEquals(
            TreasureClassPathAccumulator(
                mutableMapOf(
                    VirtualTreasureClass("tc1") to BigFraction(5, 9),
                    VirtualTreasureClass("tc2") to BigFraction(1, 3)
                )
            ),
            accumulator2.merge(accumulator3)
        )
    }

    @Test
    fun applyPicks() {
        val accumulator = TreasureClassPathAccumulator(mutableMapOf(VirtualTreasureClass("tc1") to BigFraction(1, 3)))
        assertEquals(accumulator, accumulator.applyPicks(1))
        assertEquals(
            TreasureClassPathAccumulator(mutableMapOf(VirtualTreasureClass("tc1") to BigFraction(5, 9))),
            accumulator.applyPicks(2)
        )
        assertEquals(
            TreasureClassPathAccumulator(mutableMapOf(VirtualTreasureClass("tc1") to BigFraction(665, 729))),
            accumulator.applyPicks(6)
        )
    }
}