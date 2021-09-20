package com.silospen.dropcalc.treasureclasses

import com.silospen.dropcalc.ItemQualityRatios.Companion.EMPTY
import com.silospen.dropcalc.VirtualTreasureClass
import org.apache.commons.math3.fraction.BigFraction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TreasureClassPathsTest {
    @Test
    fun forSinglePath() {
        val actual = TreasureClassPaths.forSinglePath(
            mapOf(
                VirtualTreasureClass("tc1") to TreasureClassPathOutcome(
                    BigFraction(1, 3),
                    EMPTY,
                    2,
                    3
                )
            )
        )

        val expected = TreasureClassPaths(
            mapOf(
                VirtualTreasureClass("tc1") to listOf(
                    TreasureClassPathOutcome(
                        BigFraction(1, 3),
                        EMPTY,
                        2,
                        3
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
                        BigFraction(1, 3),
                        EMPTY,
                        2,
                        3
                    )
                ),
                mapOf(
                    VirtualTreasureClass("tc1") to TreasureClassPathOutcome(
                        BigFraction(2, 3),
                        EMPTY,
                        3,
                        4
                    ),
                    VirtualTreasureClass("tc2") to TreasureClassPathOutcome(
                        BigFraction(3, 4),
                        EMPTY,
                        3,
                        4
                    )
                )
            )
        )

        val expected = TreasureClassPaths(
            mapOf(
                VirtualTreasureClass("tc1") to listOf(
                    TreasureClassPathOutcome(
                        BigFraction(1, 3),
                        EMPTY,
                        2,
                        3
                    ),
                    TreasureClassPathOutcome(
                        BigFraction(2, 3),
                        EMPTY,
                        3,
                        4
                    )
                ),
                VirtualTreasureClass("tc2") to listOf(
                    TreasureClassPathOutcome(
                        BigFraction(3, 4),
                        EMPTY,
                        3,
                        4
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
                        BigFraction(1, 3),
                        EMPTY,
                        2,
                        3
                    ),
                    TreasureClassPathOutcome(
                        BigFraction(2, 3),
                        EMPTY,
                        3,
                        4
                    )
                ),
                VirtualTreasureClass("tc2") to listOf(
                    TreasureClassPathOutcome(
                        BigFraction(3, 4),
                        EMPTY,
                        3,
                        4
                    )
                )
            )
        )

        assertEquals(BigFraction(387420425, 387420489), paths.getFinalProbability(VirtualTreasureClass("tc1")))
        assertEquals(BigFraction(386420489, 387420489), paths.getFinalProbability(VirtualTreasureClass("tc1")) {
            BigFraction(
                1,
                2
            )
        })
        assertEquals(BigFraction(16777215, 16777216), paths.getFinalProbability(VirtualTreasureClass("tc2")))
    }
}