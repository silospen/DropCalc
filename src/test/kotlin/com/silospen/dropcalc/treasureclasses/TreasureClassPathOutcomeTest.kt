package com.silospen.dropcalc.treasureclasses

import com.silospen.dropcalc.ItemQualityRatios.Companion.EMPTY
import com.silospen.dropcalc.Probability
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TreasureClassPathOutcomeTest {
    @Test
    fun onePick() {
        val outcome = TreasureClassPathOutcome(Probability(1, 3), EMPTY, 1)
        assertEquals(Probability(1, 3), outcome.getProbability())
        assertEquals(Probability(1, 6), outcome.getProbability(Probability(1, 2)))
    }

    @Test
    fun twoPicks() {
        val outcome = TreasureClassPathOutcome(Probability(1, 3), EMPTY, 2)
        assertEquals(Probability(5, 9), outcome.getProbability())
        assertEquals(Probability(11, 36), outcome.getProbability(Probability(1, 2)))
    }

    @Test
    fun sixPicks() {
        val outcome = TreasureClassPathOutcome(Probability(1, 3), EMPTY, 6)
        assertEquals(Probability(665, 729), outcome.getProbability())
        assertEquals(Probability(31031, 46656), outcome.getProbability(Probability(1, 2)))
    }

    @Test
    fun eightPicks() {
        val outcome = TreasureClassPathOutcome(Probability(1, 3), EMPTY, 8)
        assertEquals(Probability(665, 729), outcome.getProbability())
        assertEquals(Probability(31031, 46656), outcome.getProbability(Probability(1, 2)))
    }
}