package com.silospen.dropcalc

import com.silospen.dropcalc.files.TreasureClassesLineParser
import com.silospen.dropcalc.files.getResource
import com.silospen.dropcalc.files.readTsv
import org.apache.commons.math3.fraction.Fraction
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
        val actual = treasureClassCalculator.getLeafOutcomes("Act 1 H2H A")
        val expected = mapOf(
            ItemClass(name = "gld") to Fraction(21, 160),
            ItemClass(name = "weap3") to Fraction(1, 20),
            ItemClass(name = "armo3") to Fraction(1, 20),
            ItemClass(name = "Act 1 Junk") to Fraction(21, 160),
            ItemClass(name = "rin") to Fraction(1, 400),
            ItemClass(name = "amu") to Fraction(1, 800),
            ItemClass(name = "jew") to Fraction(1, 1600),
            ItemClass(name = "cm3") to Fraction(1, 1600),
            ItemClass(name = "cm2") to Fraction(1, 1600),
            ItemClass(name = "cm1") to Fraction(1, 1600),
            ItemClass(name = "Chipped Gem") to Fraction(1, 160)
        )
        assertEquals(expected, actual)
    }

    @Test
    fun getLeafOutcomes_withPartyAndPlayersSet() {
        val actual = treasureClassCalculator.getLeafOutcomes("Act 1 H2H A", 3, 3)

        val expected = mapOf(
            ItemClass(name = "gld") to Fraction(21, 79),
            ItemClass(name = "weap3") to Fraction(8, 79),
            ItemClass(name = "armo3") to Fraction(8, 79),
            ItemClass(name = "Act 1 Junk") to Fraction(21, 79),
            ItemClass(name = "rin") to Fraction(2, 395),
            ItemClass(name = "amu") to Fraction(1, 395),
            ItemClass(name = "jew") to Fraction(1, 790),
            ItemClass(name = "cm3") to Fraction(1, 790),
            ItemClass(name = "cm2") to Fraction(1, 790),
            ItemClass(name = "cm1") to Fraction(1, 790),
            ItemClass(name = "Chipped Gem") to Fraction(1, 79)
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
}