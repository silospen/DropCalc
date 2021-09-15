package com.silospen.dropcalc.items

import com.silospen.dropcalc.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ItemLibraryTest {
    @Test
    fun test() {
        val itemLibrary = ItemLibrary(listOf(armor1, weapon1, weapon2, ring))
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
}