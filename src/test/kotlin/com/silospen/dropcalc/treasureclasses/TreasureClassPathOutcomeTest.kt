package com.silospen.dropcalc.treasureclasses

import com.silospen.dropcalc.ItemQualityRatios.Companion.EMPTY
import org.apache.commons.math3.fraction.BigFraction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TreasureClassPathOutcomeTest {
    @Test
    fun onePickOneDrop() {
        val outcome = TreasureClassPathOutcome(BigFraction(1, 3), EMPTY, 1, 1)
        assertEquals(BigFraction(1, 3), outcome.getProbability())
        assertEquals(BigFraction(1, 6), outcome.getProbability(BigFraction(1, 2)))
    }

    @Test
    fun onePickMultipleDrops() {
        val outcome = TreasureClassPathOutcome(BigFraction(1, 3), EMPTY, 1, 2)
        assertEquals(BigFraction(5, 9), outcome.getProbability())
        assertEquals(BigFraction(11, 36), outcome.getProbability(BigFraction(1, 2)))
    }

    @Test
    fun multiplePicksOneDrop() {
        val outcome = TreasureClassPathOutcome(BigFraction(1, 3), EMPTY, 2, 1)
        assertEquals(BigFraction(5, 9), outcome.getProbability())
        assertEquals(BigFraction(11, 36), outcome.getProbability(BigFraction(1, 2)))
    }

    @Test
    fun multiplePicksMultipleDrops() {
        val outcome = TreasureClassPathOutcome(BigFraction(1, 3), EMPTY, 2, 3)
        assertEquals(BigFraction(665, 729), outcome.getProbability())
        assertEquals(BigFraction(31031, 46656), outcome.getProbability(BigFraction(1, 2)))
    }
}