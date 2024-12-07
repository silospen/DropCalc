package com.silospen.dropcalc.treasureclasses

import com.silospen.dropcalc.Difficulty.*
import com.silospen.dropcalc.armor1
import com.silospen.dropcalc.files.TreasureClassesLineParser
import com.silospen.dropcalc.files.getResource
import com.silospen.dropcalc.files.readTsv
import com.silospen.dropcalc.items.ItemLibrary
import com.silospen.dropcalc.ring
import com.silospen.dropcalc.weapon1
import com.silospen.dropcalc.weapon2
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TreasureClassLibraryTest {

    private val treasureClassConfigs = readTsv(
        getResource("treasureClassCalculatorTestData/treasureclass.txt"),
        TreasureClassesLineParser()
    ).toList()
    private val itemLibrary = ItemLibrary(listOf(armor1, weapon1, weapon2, ring), emptyList(), emptyList())
    private val treasureClassLibrary = TreasureClassLibrary(treasureClassConfigs, itemLibrary)

    @Test
    fun treasureClassUpgrades() {
        val levelTwoTc = treasureClassLibrary.getTreasureClass("Act 1 Equip A")
        val levelNineTc = treasureClassLibrary.getTreasureClass("Act 1 Equip B")
        assertEquals(levelTwoTc, treasureClassLibrary.changeTcBasedOnLevel(levelTwoTc, 1, HELL, false))
        assertEquals(levelTwoTc, treasureClassLibrary.changeTcBasedOnLevel(levelTwoTc, 2, HELL, false))
        assertEquals(levelTwoTc, treasureClassLibrary.changeTcBasedOnLevel(levelTwoTc, 3, HELL, false))
        assertEquals(levelNineTc, treasureClassLibrary.changeTcBasedOnLevel(levelNineTc, 8, HELL, false))
        assertEquals(levelNineTc, treasureClassLibrary.changeTcBasedOnLevel(levelNineTc, 9, HELL, false))
        assertEquals(levelNineTc, treasureClassLibrary.changeTcBasedOnLevel(levelNineTc, 15, HELL, false))
        assertEquals(levelNineTc, treasureClassLibrary.changeTcBasedOnLevel(levelTwoTc, 15, HELL, false))
        assertEquals(levelNineTc, treasureClassLibrary.changeTcBasedOnLevel(levelNineTc, 15, NIGHTMARE, false))
        assertEquals(levelTwoTc, treasureClassLibrary.changeTcBasedOnLevel(levelTwoTc, 15, NORMAL, false))
        assertEquals(levelNineTc, treasureClassLibrary.changeTcBasedOnLevel(levelTwoTc, 15, NORMAL, true))
        assertEquals(levelNineTc, treasureClassLibrary.changeTcBasedOnLevel(levelNineTc, 1, HELL, false))
    }

    @Test
    fun tcUpgradeGroup18Example() {
        val treasureClassConfigs = readTsv(
            getResource("treasureClassCalculatorTestData/group18tcs.txt"),
            TreasureClassesLineParser()
        ).toList()
        val itemLibrary = ItemLibrary(emptyList(), emptyList(), emptyList())
        val treasureClassLibrary = TreasureClassLibrary(treasureClassConfigs, itemLibrary)

        val tc1 = treasureClassLibrary.getTreasureClass("Act 4 (N) Super Cx")
        val tc2 = treasureClassLibrary.getTreasureClass("Act 5 (H) Super C")
        val tc3 = treasureClassLibrary.getTreasureClass("Act 5 (H) Super B")

        assertEquals(
            treasureClassLibrary.getTreasureClass("Act 4 (H) Super Bx"),
            treasureClassLibrary.changeTcBasedOnLevel(tc1, 86, HELL, false)
        )
        assertEquals(
            treasureClassLibrary.getTreasureClass("Act 4 (H) Super Bx"),
            treasureClassLibrary.changeTcBasedOnLevel(tc2, 86, HELL, false)
        )
        assertEquals(
            treasureClassLibrary.getTreasureClass("Act 1 Super Cx"),
            treasureClassLibrary.changeTcBasedOnLevel(tc2, 10, HELL, false)
        )
        assertEquals(
            treasureClassLibrary.getTreasureClass("Act 5 (H) Super B"),
            treasureClassLibrary.changeTcBasedOnLevel(tc3, 93, HELL, false)
        )
        assertEquals(
            treasureClassLibrary.getTreasureClass("Act 5 (H) Super B"),
            treasureClassLibrary.changeTcBasedOnLevel(tc3, 94, HELL, false)
        )
        assertEquals(
            treasureClassLibrary.getTreasureClass("Act 5 (H) Super Cx"),
            treasureClassLibrary.changeTcBasedOnLevel(tc3, 96, HELL, false)
        )
        assertEquals(
            treasureClassLibrary.getTreasureClass("Act 5 (H) Super Cx"),
            treasureClassLibrary.changeTcBasedOnLevel(tc3, 97, HELL, false)
        )
    }

}