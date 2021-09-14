package com.silospen.dropcalc

import com.silospen.dropcalc.Difficulty.*
import com.silospen.dropcalc.files.TreasureClassesLineParser
import com.silospen.dropcalc.files.getResource
import com.silospen.dropcalc.files.readTsv
import org.apache.commons.math3.fraction.BigFraction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TreasureClassCalculatorTest {

    private val treasureClassConfigs = readTsv(
        getResource("treasureClassCalculatorTestData/treasureclass.txt"),
        TreasureClassesLineParser()
    ).toList()

    private val treasureClassCalculator = TreasureClassCalculator(treasureClassConfigs)

    @Test
    fun getLeafOutcomes() {
        val actual = treasureClassCalculator.getLeafOutcomes("Act 1 H2H A", 0, NORMAL)
        val expected = mapOf(
            ItemTreasureClass(name = "gld") to BigFraction(21, 160),
            ItemTreasureClass(name = "weap3") to BigFraction(1, 20),
            ItemTreasureClass(name = "armo3") to BigFraction(1, 20),
            ItemTreasureClass(name = "Act 1 Junk") to BigFraction(21, 160),
            ItemTreasureClass(name = "rin") to BigFraction(1, 400),
            ItemTreasureClass(name = "amu") to BigFraction(1, 800),
            ItemTreasureClass(name = "jew") to BigFraction(1, 1600),
            ItemTreasureClass(name = "cm3") to BigFraction(1, 1600),
            ItemTreasureClass(name = "cm2") to BigFraction(1, 1600),
            ItemTreasureClass(name = "cm1") to BigFraction(1, 1600),
            ItemTreasureClass(name = "Chipped Gem") to BigFraction(1, 160)
        )
        assertEquals(expected, actual)
    }

    @Test
    fun getLeafOutcomes_withTcUpgraded() {
        val actual = treasureClassCalculator.getLeafOutcomes("Act 1 H2H A", 50, HELL)
        val expected = mapOf(
            ItemTreasureClass(name = "gld") to BigFraction(5, 110),
            ItemTreasureClass(name = "weap3") to BigFraction(5, 110).multiply(BigFraction(7, 14)),
            ItemTreasureClass(name = "armo3") to BigFraction(5, 110).multiply(BigFraction(7, 14)),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun getLeafOutcomes_withPartyAndPlayersSet() {
        val actual = treasureClassCalculator.getLeafOutcomes("Act 1 H2H A", 0, NORMAL, 3, 3)
        val expected = mapOf(
            ItemTreasureClass(name = "gld") to BigFraction(21, 79),
            ItemTreasureClass(name = "weap3") to BigFraction(8, 79),
            ItemTreasureClass(name = "armo3") to BigFraction(8, 79),
            ItemTreasureClass(name = "Act 1 Junk") to BigFraction(21, 79),
            ItemTreasureClass(name = "rin") to BigFraction(2, 395),
            ItemTreasureClass(name = "amu") to BigFraction(1, 395),
            ItemTreasureClass(name = "jew") to BigFraction(1, 790),
            ItemTreasureClass(name = "cm3") to BigFraction(1, 790),
            ItemTreasureClass(name = "cm2") to BigFraction(1, 790),
            ItemTreasureClass(name = "cm1") to BigFraction(1, 790),
            ItemTreasureClass(name = "Chipped Gem") to BigFraction(1, 79)
        )
        assertEquals(expected, actual)
    }

    @Test
    fun getLeafOutcomes_withPicks() {
        val actual = treasureClassCalculator.getLeafOutcomes("Radament", 50, NORMAL)
        val expected = mapOf(
            ItemTreasureClass(name = "\"gld,mul=1280\"") to BigFraction(799393331, 1350125107),
            ItemTreasureClass(name = "Act 3 Equip B") to BigFraction(1095321139, 1350125107),
            ItemTreasureClass(name = "Act 3 Junk") to BigFraction(969921075, 1350125107),
            ItemTreasureClass(name = "Act 3 Good") to BigFraction(276383283, 1350125107)
        )
        assertEquals(expected, actual)
    }

    @Test
    fun getLeafOutcomes_withNegativePicks() {
        val actual = treasureClassCalculator.getLeafOutcomes("Act 1 Champ A", 4, NORMAL)
        val expected = mapOf(
            ItemTreasureClass(name = "\"gld,mul=1280\"") to BigFraction(60, 100),
            ItemTreasureClass(name = "weap3") to BigFraction(37, 200),
            ItemTreasureClass(name = "armo3") to BigFraction(37, 200),
            ItemTreasureClass(name = "rin") to BigFraction(3, 500),
            ItemTreasureClass(name = "amu") to BigFraction(3, 1000),
            ItemTreasureClass(name = "jew") to BigFraction(3, 2000),
            ItemTreasureClass(name = "cm3") to BigFraction(3, 2000),
            ItemTreasureClass(name = "cm2") to BigFraction(3, 2000),
            ItemTreasureClass(name = "cm1") to BigFraction(3, 2000),
            ItemTreasureClass(name = "Chipped Gem") to BigFraction(3, 200),
            ItemTreasureClass(name = "Act 1 Cpot A") to BigFraction(1)
        )
        assertEquals(expected, actual)
    }

    @Test
    fun getLeafOutcomes_withNegativePicks_unevenPickDistribution() {
        val actual = treasureClassCalculator.getLeafOutcomes("Act 1 Champ B", 4, NORMAL)
        val expected = mapOf(
            ItemTreasureClass(name = "\"gld,mul=1280\"") to BigFraction(60, 100),
            ItemTreasureClass(name = "weap3") to BigFraction(832772357, 907039232),
            ItemTreasureClass(name = "armo3") to BigFraction(832772357, 907039232),
            ItemTreasureClass(name = "rin") to BigFraction(3, 500),
            ItemTreasureClass(name = "amu") to BigFraction(3, 1000),
            ItemTreasureClass(name = "jew") to BigFraction(3, 2000),
            ItemTreasureClass(name = "cm3") to BigFraction(3, 2000),
            ItemTreasureClass(name = "cm2") to BigFraction(3, 2000),
            ItemTreasureClass(name = "cm1") to BigFraction(3, 2000),
            ItemTreasureClass(name = "Chipped Gem") to BigFraction(3, 200),
            ItemTreasureClass(name = "armo6") to BigFraction(1653912, 1771561)
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