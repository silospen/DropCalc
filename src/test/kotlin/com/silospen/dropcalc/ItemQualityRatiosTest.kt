package com.silospen.dropcalc

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ItemQualityRatiosTest {
    @Test
    fun construct() {
        val itemQualityRatios = ItemQualityRatios(1, 2, 3, 4)
        assertEquals(ItemQualityRatios(1, 2, 3, 4), itemQualityRatios)
    }

    @Test
    fun merge() {
        assertEquals(
            ItemQualityRatios(3, 3, 3, 3),
            ItemQualityRatios(2, 2, 2, 2).merge(ItemQualityRatios(3, 3, 3, 3))
        )

        assertEquals(
            ItemQualityRatios(4, 3, 3, 4),
            ItemQualityRatios(1, 2, 3, 4).merge(ItemQualityRatios(4, 3, 1, 1))
        )
    }
}