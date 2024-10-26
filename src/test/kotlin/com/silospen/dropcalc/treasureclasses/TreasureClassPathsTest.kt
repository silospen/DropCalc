package com.silospen.dropcalc.treasureclasses

import com.silospen.dropcalc.ItemQualityRatios.Companion.EMPTY
import com.silospen.dropcalc.Probability
import com.silospen.dropcalc.VirtualTreasureClass
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TreasureClassPathsTest {
    @Test
    fun forSinglePath() {
        val actual = TreasureClassPaths.forSinglePath(
            mapOf(
                VirtualTreasureClass("tc1") to TreasureClassPathOutcome(
                    Probability(1, 3),
                    EMPTY,
                    6
                )
            )
        )

        val expected = TreasureClassPaths(
            mapOf(
                VirtualTreasureClass("tc1") to listOf(
                    TreasureClassPathOutcome(
                        Probability(1, 3),
                        EMPTY,
                        6
                    )
                )
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun forMultiplePaths() {
        val actual = TreasureClassPaths.forMultiplePaths(
            listOf(
                mapOf(
                    VirtualTreasureClass("tc1") to TreasureClassPathOutcome(
                        Probability(1, 3),
                        EMPTY,
                        6
                    )
                ),
                mapOf(
                    VirtualTreasureClass("tc1") to TreasureClassPathOutcome(
                        Probability(2, 3),
                        EMPTY,
                        12
                    ),
                    VirtualTreasureClass("tc2") to TreasureClassPathOutcome(
                        Probability(3, 4),
                        EMPTY,
                        12
                    )
                )
            )
        )

        val expected = TreasureClassPaths(
            mapOf(
                VirtualTreasureClass("tc1") to listOf(
                    TreasureClassPathOutcome(
                        Probability(1, 3),
                        EMPTY,
                        6
                    ),
                    TreasureClassPathOutcome(
                        Probability(2, 3),
                        EMPTY,
                        12
                    )
                ),
                VirtualTreasureClass("tc2") to listOf(
                    TreasureClassPathOutcome(
                        Probability(3, 4),
                        EMPTY,
                        12
                    )
                )
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun getProbability() {
        val paths = TreasureClassPaths(
            mapOf(
                VirtualTreasureClass("tc1") to listOf(
                    TreasureClassPathOutcome(
                        Probability(1, 3),
                        EMPTY,
                        6
                    ),
                    TreasureClassPathOutcome(
                        Probability(2, 3),
                        EMPTY,
                        12
                    )
                ),
                VirtualTreasureClass("tc2") to listOf(
                    TreasureClassPathOutcome(
                        Probability(3, 4),
                        EMPTY,
                        12
                    )
                )
            )
        )

        assertEquals(Probability(531377, 531441), paths.getFinalProbability(VirtualTreasureClass("tc1")))
        assertEquals(Probability(515816, 531441), paths.getFinalProbability(VirtualTreasureClass("tc1")) {
            Probability(
                1,
                2
            )
        })
        assertEquals(Probability(4095, 4096), paths.getFinalProbability(VirtualTreasureClass("tc2")))
    }
}