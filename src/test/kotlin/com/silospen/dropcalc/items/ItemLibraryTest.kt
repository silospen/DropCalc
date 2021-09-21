package com.silospen.dropcalc.items

import com.silospen.dropcalc.*
import com.silospen.dropcalc.ItemQuality.*
import org.apache.commons.math3.fraction.BigFraction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ItemLibraryTest {
    private val itemLibrary = ItemLibrary(
        listOf(armor1, weapon1, weapon2, ring),
        listOf(
            ItemRatio(
                isUber = false,
                isClassSpecific = false,
                modifiers = mapOf(
                    UNIQUE to ItemQualityModifiers(400, 2, 10),
                    RARE to ItemQualityModifiers(100, 4, 10),
                    SET to ItemQualityModifiers(100, 6, 10),
                    MAGIC to ItemQualityModifiers(10, 8, 10)
                )
            )
        ),
        emptyList()
    )

    private val itemQualityRatios = ItemQualityRatios(400, 300, 200, 100)

    @Test
    fun getOrConstructVirtualTreasureClass() {
        assertEquals(
            VirtualTreasureClass("armo3", 2, setOf(Outcome(armor1, 2))),
            itemLibrary.getOrConstructVirtualTreasureClass("armo3")
        )
        assertEquals(
            VirtualTreasureClass("weap3", 6, setOf(Outcome(weapon1, 4), Outcome(weapon2, 2))),
            itemLibrary.getOrConstructVirtualTreasureClass("weap3")
        )
        assertEquals(
            VirtualTreasureClass("mele3", 4, setOf(Outcome(weapon1, 4))),
            itemLibrary.getOrConstructVirtualTreasureClass("mele3")
        )
        assertEquals(
            VirtualTreasureClass("item1", 1, setOf(Outcome(armor1, 1))),
            itemLibrary.getOrConstructVirtualTreasureClass("item1")
        )
        assertEquals(
            VirtualTreasureClass("a-random-not-listed-item", 1, emptySet()),
            itemLibrary.getOrConstructVirtualTreasureClass("a-random-not-listed-item")
        )
    }

    @Test
    fun getProbQuality() {
        assertEquals(
            BigFraction(4, 975),
            itemLibrary.getProbQuality(UNIQUE, skeletonMonster, armor1, itemQualityRatios, 0)
        )
        assertEquals(
            BigFraction(62144, 4411875),
            itemLibrary.getProbQuality(SET, skeletonMonster, armor1, itemQualityRatios, 0)
        )
        assertEquals(
            BigFraction(46204064, 3786859375),
            itemLibrary.getProbQuality(RARE, skeletonMonster, armor1, itemQualityRatios, 0)
        )
        assertEquals(
            BigFraction(469987739008, 4373822578125),
            itemLibrary.getProbQuality(MAGIC, skeletonMonster, armor1, itemQualityRatios, 0)
        )
    }

    @Test
    fun getProbQuality_withMf() {
        assertEquals(
            BigFraction(32, 3083),
            itemLibrary.getProbQuality(UNIQUE, skeletonMonster, armor1, itemQualityRatios, 400)
        )
        assertEquals(
            BigFraction(130176, 2888771),
            itemLibrary.getProbQuality(SET, skeletonMonster, armor1, itemQualityRatios, 400)
        )
        assertEquals(
            BigFraction(349262208, 8750087359),
            itemLibrary.getProbQuality(RARE, skeletonMonster, armor1, itemQualityRatios, 400)
        )
        assertEquals(
            BigFraction(337736555136, 673756726643),
            itemLibrary.getProbQuality(MAGIC, skeletonMonster, armor1, itemQualityRatios, 400)
        )
    }

    @Test
    fun getProbabilityQualityWhenGuaranteedDrop() {
        assertEquals(
            BigFraction.ONE,
            itemLibrary.getProbQuality(UNIQUE, skeletonMonster, armor1, ItemQualityRatios(1024, 1, 1, 1), 400)
        )
    }
}