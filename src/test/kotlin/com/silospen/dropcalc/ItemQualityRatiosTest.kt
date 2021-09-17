package com.silospen.dropcalc

import com.silospen.dropcalc.ItemQuality.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ItemQualityRatiosTest {
    @Test
    fun construct() {
        val itemQualityRatios = ItemQualityRatios(1, 2, 3, 4)
        assertEquals(ItemQualityRatios(mapOf(UNIQUE to 1, SET to 2, RARE to 3, MAGIC to 4)), itemQualityRatios)
        assertEquals(mapOf(UNIQUE to 1, SET to 2, RARE to 3, MAGIC to 4), itemQualityRatios.values)
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