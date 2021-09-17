package com.silospen.dropcalc

import com.silospen.dropcalc.Difficulty.*
import com.silospen.dropcalc.TreasureClassOutcomeType.DEFINED
import com.silospen.dropcalc.TreasureClassOutcomeType.VIRTUAL
import com.silospen.dropcalc.files.TreasureClassesLineParser
import com.silospen.dropcalc.files.getResource
import com.silospen.dropcalc.files.readTsv
import com.silospen.dropcalc.items.ItemLibrary
import com.silospen.dropcalc.treasureclasses.TreasureClassPathAccumulator
import org.apache.commons.math3.fraction.BigFraction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TreasureClassCalculatorTest {

    private val treasureClassConfigs = readTsv(
        getResource("treasureClassCalculatorTestData/treasureclass.txt"),
        TreasureClassesLineParser()
    ).toList()

    private val itemLibrary = ItemLibrary(listOf(armor1, weapon1, weapon2, ring))
    private val treasureClassCalculator = TreasureClassCalculator(
        treasureClassConfigs, itemLibrary
    )

    @Test
    fun getLeafOutcomes() {
        val actual = treasureClassCalculator.getLeafOutcomes("Act 1 H2H A", 0, NORMAL, DEFINED)
        val expected = TreasureClassPathAccumulator(
            mutableMapOf(
                itemLibrary.getOrConstructVirtualTreasureClass("gld") to BigFraction(21, 160),
                itemLibrary.getOrConstructVirtualTreasureClass("weap3") to BigFraction(1, 20),
                itemLibrary.getOrConstructVirtualTreasureClass("armo3") to BigFraction(1, 20),
                itemLibrary.getOrConstructVirtualTreasureClass("Act 1 Junk") to BigFraction(21, 160),
                itemLibrary.getOrConstructVirtualTreasureClass("rin") to BigFraction(1, 400),
                itemLibrary.getOrConstructVirtualTreasureClass("amu") to BigFraction(1, 800),
                itemLibrary.getOrConstructVirtualTreasureClass("jew") to BigFraction(1, 1600),
                itemLibrary.getOrConstructVirtualTreasureClass("cm3") to BigFraction(1, 1600),
                itemLibrary.getOrConstructVirtualTreasureClass("cm2") to BigFraction(1, 1600),
                itemLibrary.getOrConstructVirtualTreasureClass("cm1") to BigFraction(1, 1600),
                itemLibrary.getOrConstructVirtualTreasureClass("Chipped Gem") to BigFraction(1, 160)
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun getLeafOutcomes_forVirtualTcs() {
        val actual = treasureClassCalculator.getLeafOutcomes("Act 1 H2H A", 0, NORMAL, VIRTUAL)
        val expected = TreasureClassPathAccumulator(
            mutableMapOf(
                weapon1 to BigFraction(1, 30),
                weapon2 to BigFraction(1, 60),
                armor1 to BigFraction(1, 20),
                ring to BigFraction(1, 400)
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun getLeafOutcomes_withTcUpgraded() {
        val actual = treasureClassCalculator.getLeafOutcomes("Act 1 H2H A", 50, HELL, DEFINED)
        val expected = TreasureClassPathAccumulator(
            mutableMapOf(
                itemLibrary.getOrConstructVirtualTreasureClass("gld") to BigFraction(5, 110),
                itemLibrary.getOrConstructVirtualTreasureClass("weap3") to BigFraction(5, 110).multiply(
                    BigFraction(
                        7,
                        14
                    )
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("armo3") to BigFraction(5, 110).multiply(
                    BigFraction(
                        7,
                        14
                    )
                ),
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun getLeafOutcomes_withPartyAndPlayersSet() {
        val actual = treasureClassCalculator.getLeafOutcomes("Act 1 H2H A", 0, NORMAL, DEFINED, 3, 3)
        val expected = TreasureClassPathAccumulator(
            mutableMapOf(
                itemLibrary.getOrConstructVirtualTreasureClass("gld") to BigFraction(21, 79),
                itemLibrary.getOrConstructVirtualTreasureClass("weap3") to BigFraction(8, 79),
                itemLibrary.getOrConstructVirtualTreasureClass("armo3") to BigFraction(8, 79),
                itemLibrary.getOrConstructVirtualTreasureClass("Act 1 Junk") to BigFraction(21, 79),
                itemLibrary.getOrConstructVirtualTreasureClass("rin") to BigFraction(2, 395),
                itemLibrary.getOrConstructVirtualTreasureClass("amu") to BigFraction(1, 395),
                itemLibrary.getOrConstructVirtualTreasureClass("jew") to BigFraction(1, 790),
                itemLibrary.getOrConstructVirtualTreasureClass("cm3") to BigFraction(1, 790),
                itemLibrary.getOrConstructVirtualTreasureClass("cm2") to BigFraction(1, 790),
                itemLibrary.getOrConstructVirtualTreasureClass("cm1") to BigFraction(1, 790),
                itemLibrary.getOrConstructVirtualTreasureClass("Chipped Gem") to BigFraction(1, 79)
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun getLeafOutcomes_withPicks() {
        val actual = treasureClassCalculator.getLeafOutcomes("Radament", 50, NORMAL, DEFINED)
        val expected = TreasureClassPathAccumulator(
            mutableMapOf(
                itemLibrary.getOrConstructVirtualTreasureClass("gld") to BigFraction(799393331, 1350125107),
                itemLibrary.getOrConstructVirtualTreasureClass("Act 3 Equip B") to BigFraction(1095321139, 1350125107),
                itemLibrary.getOrConstructVirtualTreasureClass("Act 3 Junk") to BigFraction(969921075, 1350125107),
                itemLibrary.getOrConstructVirtualTreasureClass("Act 3 Good") to BigFraction(276383283, 1350125107)
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun getLeafOutcomes_withNegativePicks() {
        val actual = treasureClassCalculator.getLeafOutcomes("Act 1 Champ A", 4, NORMAL, DEFINED)
        val expected = TreasureClassPathAccumulator(
            mutableMapOf(
                itemLibrary.getOrConstructVirtualTreasureClass("gld") to BigFraction(60, 100),
                itemLibrary.getOrConstructVirtualTreasureClass("weap3") to BigFraction(37, 200),
                itemLibrary.getOrConstructVirtualTreasureClass("armo3") to BigFraction(37, 200),
                itemLibrary.getOrConstructVirtualTreasureClass("rin") to BigFraction(3, 500),
                itemLibrary.getOrConstructVirtualTreasureClass("amu") to BigFraction(3, 1000),
                itemLibrary.getOrConstructVirtualTreasureClass("jew") to BigFraction(3, 2000),
                itemLibrary.getOrConstructVirtualTreasureClass("cm3") to BigFraction(3, 2000),
                itemLibrary.getOrConstructVirtualTreasureClass("cm2") to BigFraction(3, 2000),
                itemLibrary.getOrConstructVirtualTreasureClass("cm1") to BigFraction(3, 2000),
                itemLibrary.getOrConstructVirtualTreasureClass("Chipped Gem") to BigFraction(3, 200),
                itemLibrary.getOrConstructVirtualTreasureClass("Act 1 Cpot A") to BigFraction(1)
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun getLeafOutcomes_withNegativePicks_unevenPickDistribution() {
        val actual = treasureClassCalculator.getLeafOutcomes("Act 1 Champ B", 4, NORMAL, DEFINED)
        val expected = TreasureClassPathAccumulator(
            mutableMapOf(
                itemLibrary.getOrConstructVirtualTreasureClass("gld") to BigFraction(60, 100),
                itemLibrary.getOrConstructVirtualTreasureClass("weap3") to BigFraction(832772357, 907039232),
                itemLibrary.getOrConstructVirtualTreasureClass("armo3") to BigFraction(832772357, 907039232),
                itemLibrary.getOrConstructVirtualTreasureClass("rin") to BigFraction(3, 500),
                itemLibrary.getOrConstructVirtualTreasureClass("amu") to BigFraction(3, 1000),
                itemLibrary.getOrConstructVirtualTreasureClass("jew") to BigFraction(3, 2000),
                itemLibrary.getOrConstructVirtualTreasureClass("cm3") to BigFraction(3, 2000),
                itemLibrary.getOrConstructVirtualTreasureClass("cm2") to BigFraction(3, 2000),
                itemLibrary.getOrConstructVirtualTreasureClass("cm1") to BigFraction(3, 2000),
                itemLibrary.getOrConstructVirtualTreasureClass("Chipped Gem") to BigFraction(3, 200),
                itemLibrary.getOrConstructVirtualTreasureClass("armo6") to BigFraction(1653912, 1771561)
            )
        )
        assertEquals(expected, actual)
    }


    @Test
    fun calculateNoDrop() {
        assertEquals(100, calculateNoDrop(60, 100, 1, 1))
        assertEquals(100, calculateNoDrop(60, 100, 2, 1))
        assertEquals(19, calculateNoDrop(60, 100, 6, 1))
        assertEquals(19, calculateNoDrop(60, 100, 3, 3))
        assertEquals(1, calculateNoDrop(60, 100, 8, 8))
        assertEquals(0, calculateNoDrop(60, null, 8, 8))
    }

    @Test
    fun treasureClassUpgrades() {
        val levelTwoTc = treasureClassCalculator.getTreasureClass("Act 1 Equip A")
        val levelNineTc = treasureClassCalculator.getTreasureClass("Act 1 Equip B")
        assertEquals(levelTwoTc, treasureClassCalculator.changeTcBasedOnLevel(levelTwoTc, 1, HELL))
        assertEquals(levelTwoTc, treasureClassCalculator.changeTcBasedOnLevel(levelTwoTc, 2, HELL))
        assertEquals(levelNineTc, treasureClassCalculator.changeTcBasedOnLevel(levelNineTc, 8, HELL))
        assertEquals(levelNineTc, treasureClassCalculator.changeTcBasedOnLevel(levelNineTc, 9, HELL))
        assertEquals(levelNineTc, treasureClassCalculator.changeTcBasedOnLevel(levelNineTc, 15, HELL))
        assertEquals(levelNineTc, treasureClassCalculator.changeTcBasedOnLevel(levelTwoTc, 15, HELL))

        assertEquals(levelNineTc, treasureClassCalculator.changeTcBasedOnLevel(levelNineTc, 15, NIGHTMARE))
        assertEquals(levelTwoTc, treasureClassCalculator.changeTcBasedOnLevel(levelTwoTc, 15, NORMAL))
        assertEquals(levelNineTc, treasureClassCalculator.changeTcBasedOnLevel(levelNineTc, 1, HELL))
    }
}