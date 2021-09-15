package com.silospen.dropcalc.items

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ItemTypeCodeLibraryTest {
    @Test
    fun test() {
        val actual = ItemTypeCodeLibrary.fromIncompleteLineages(
            listOf(
                SingleItemTypeCodeEntry("a", setOf("b", "c")),
                SingleItemTypeCodeEntry("b", setOf("d", "e")),
                SingleItemTypeCodeEntry("c", emptySet()),
                SingleItemTypeCodeEntry("d", emptySet()),
                SingleItemTypeCodeEntry("e", emptySet())
            )
        )
        val expected = ItemTypeCodeLibrary(
            listOf(
                ItemTypeCodeWithParents("a", setOf("b", "c", "d", "e")),
                ItemTypeCodeWithParents("b", setOf("d", "e")),
                ItemTypeCodeWithParents("c", emptySet()),
                ItemTypeCodeWithParents("d", emptySet()),
                ItemTypeCodeWithParents("e", emptySet())
            )
        )
        assertEquals(expected, actual)
        assertEquals(setOf("b", "c", "d", "e"), actual.getAllParentCodes("a"))
    }
}