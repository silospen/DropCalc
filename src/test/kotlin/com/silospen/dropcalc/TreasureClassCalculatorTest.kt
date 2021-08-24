package com.silospen.dropcalc

import com.silospen.dropcalc.parser.getResource
import com.silospen.dropcalc.parser.treasureClassesParser
import com.silospen.dropcalc.reader.readTsv
import org.apache.commons.math3.fraction.Fraction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TreasureClassCalculatorTest {

    @Test
    fun getLeafOutcomes() {
        val treasureClassConfigs = readTsv(
            getResource("treasureClassCalculatorTestData/treasureclass.txt"),
            ::treasureClassesParser
        ).toList()

        val treasureClassCalculator = TreasureClassCalculator(treasureClassConfigs)
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
}