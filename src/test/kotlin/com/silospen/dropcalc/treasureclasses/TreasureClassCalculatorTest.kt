package com.silospen.dropcalc.treasureclasses

import com.silospen.dropcalc.*
import com.silospen.dropcalc.Difficulty.*
import com.silospen.dropcalc.ItemQualityRatios.Companion.EMPTY
import com.silospen.dropcalc.files.TreasureClassesLineParser
import com.silospen.dropcalc.files.getResource
import com.silospen.dropcalc.files.readTsv
import com.silospen.dropcalc.items.ItemLibrary
import com.silospen.dropcalc.treasureclasses.TreasureClassOutcomeType.DEFINED
import com.silospen.dropcalc.treasureclasses.TreasureClassOutcomeType.VIRTUAL
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
                itemLibrary.getOrConstructVirtualTreasureClass("gld") to TreasureClassPathOutcome(
                    BigFraction(21, 160),
                    EMPTY
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("weap3") to TreasureClassPathOutcome(
                    BigFraction(1, 20),
                    EMPTY
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("armo3") to TreasureClassPathOutcome(
                    BigFraction(1, 20),
                    EMPTY
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("Act 1 Junk") to TreasureClassPathOutcome(
                    BigFraction(
                        21,
                        160
                    ), EMPTY
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("rin") to TreasureClassPathOutcome(
                    BigFraction(1, 400),
                    EMPTY
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("amu") to TreasureClassPathOutcome(
                    BigFraction(1, 800),
                    EMPTY
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("jew") to TreasureClassPathOutcome(
                    BigFraction(1, 1600),
                    EMPTY
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("cm3") to TreasureClassPathOutcome(
                    BigFraction(1, 1600),
                    EMPTY
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("cm2") to TreasureClassPathOutcome(
                    BigFraction(1, 1600),
                    EMPTY
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("cm1") to TreasureClassPathOutcome(
                    BigFraction(1, 1600),
                    EMPTY
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("Chipped Gem") to TreasureClassPathOutcome(
                    BigFraction(
                        1,
                        160
                    ), EMPTY
                )
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun getLeafOutcomes_forVirtualTcs() {
        val actual = treasureClassCalculator.getLeafOutcomes("Act 1 H2H A", 0, NORMAL, VIRTUAL)
        val expected = TreasureClassPathAccumulator(
            mutableMapOf(
                weapon1 to TreasureClassPathOutcome(BigFraction(1, 30), EMPTY),
                weapon2 to TreasureClassPathOutcome(BigFraction(1, 60), EMPTY),
                armor1 to TreasureClassPathOutcome(BigFraction(1, 20), EMPTY),
                ring to TreasureClassPathOutcome(BigFraction(1, 400), EMPTY)
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun getLeafOutcomes_withTcUpgraded() {
        val actual = treasureClassCalculator.getLeafOutcomes("Act 1 H2H A", 50, HELL, DEFINED)
        val expectedProbabilities = BigFraction(5, 110).multiply(BigFraction(7, 14))
        val expected = TreasureClassPathAccumulator(
            mutableMapOf(
                itemLibrary.getOrConstructVirtualTreasureClass("gld") to TreasureClassPathOutcome(
                    BigFraction(5, 110),
                    EMPTY
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("weap3") to TreasureClassPathOutcome(
                    expectedProbabilities,
                    EMPTY
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("armo3") to TreasureClassPathOutcome(
                    expectedProbabilities,
                    EMPTY
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
                itemLibrary.getOrConstructVirtualTreasureClass("gld") to TreasureClassPathOutcome(
                    BigFraction(21, 79),
                    EMPTY
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("weap3") to TreasureClassPathOutcome(
                    BigFraction(8, 79),
                    EMPTY
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("armo3") to TreasureClassPathOutcome(
                    BigFraction(8, 79),
                    EMPTY
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("Act 1 Junk") to TreasureClassPathOutcome(
                    BigFraction(
                        21,
                        79
                    ), EMPTY
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("rin") to TreasureClassPathOutcome(
                    BigFraction(2, 395),
                    EMPTY
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("amu") to TreasureClassPathOutcome(
                    BigFraction(1, 395),
                    EMPTY
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("jew") to TreasureClassPathOutcome(
                    BigFraction(1, 790),
                    EMPTY
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("cm3") to TreasureClassPathOutcome(
                    BigFraction(1, 790),
                    EMPTY
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("cm2") to TreasureClassPathOutcome(
                    BigFraction(1, 790),
                    EMPTY
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("cm1") to TreasureClassPathOutcome(
                    BigFraction(1, 790),
                    EMPTY
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("Chipped Gem") to TreasureClassPathOutcome(
                    BigFraction(
                        1,
                        79
                    ), EMPTY
                )
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun getLeafOutcomes_withPicks() {
        val actual = treasureClassCalculator.getLeafOutcomes("Radament", 50, NORMAL, DEFINED)
        val radamentItemQualityRatios = ItemQualityRatios(900, 900, 900, 1024)
        val expected = TreasureClassPathAccumulator(
            mutableMapOf(
                itemLibrary.getOrConstructVirtualTreasureClass("gld") to TreasureClassPathOutcome(
                    BigFraction(
                        799393331,
                        1350125107
                    ), radamentItemQualityRatios
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("Act 3 Equip B") to TreasureClassPathOutcome(
                    BigFraction(
                        1095321139,
                        1350125107
                    ), radamentItemQualityRatios
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("Act 3 Junk") to TreasureClassPathOutcome(
                    BigFraction(
                        969921075,
                        1350125107
                    ), radamentItemQualityRatios
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("Act 3 Good") to TreasureClassPathOutcome(
                    BigFraction(
                        276383283,
                        1350125107
                    ), radamentItemQualityRatios
                )
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun getLeafOutcomes_withNegativePicks() {
        val actual = treasureClassCalculator.getLeafOutcomes("Act 1 Champ A", 4, NORMAL, DEFINED)
        val expected = TreasureClassPathAccumulator(
            mutableMapOf(
                itemLibrary.getOrConstructVirtualTreasureClass("gld") to TreasureClassPathOutcome(
                    BigFraction(60, 100),
                    ItemQualityRatios(800, 800, 800, 1024)
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("weap3") to TreasureClassPathOutcome(
                    BigFraction(
                        37,
                        200
                    ), ItemQualityRatios(800, 1000, 800, 1024)
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("armo3") to TreasureClassPathOutcome(
                    BigFraction(
                        37,
                        200
                    ), ItemQualityRatios(800, 1000, 800, 1024)
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("rin") to TreasureClassPathOutcome(
                    BigFraction(3, 500),
                    ItemQualityRatios(800, 800, 800, 1024)
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("amu") to TreasureClassPathOutcome(
                    BigFraction(3, 1000),
                    ItemQualityRatios(800, 800, 800, 1024)
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("jew") to TreasureClassPathOutcome(
                    BigFraction(3, 2000),
                    ItemQualityRatios(800, 800, 800, 1024)
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("cm3") to TreasureClassPathOutcome(
                    BigFraction(3, 2000),
                    ItemQualityRatios(800, 800, 800, 1024)
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("cm2") to TreasureClassPathOutcome(
                    BigFraction(3, 2000),
                    ItemQualityRatios(800, 800, 800, 1024)
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("cm1") to TreasureClassPathOutcome(
                    BigFraction(3, 2000),
                    ItemQualityRatios(800, 800, 800, 1024)
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("Chipped Gem") to TreasureClassPathOutcome(
                    BigFraction(
                        3,
                        200
                    ), ItemQualityRatios(800, 800, 800, 1024)
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("Act 1 Cpot A") to TreasureClassPathOutcome(
                    BigFraction(1),
                    EMPTY
                )
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun getLeafOutcomes_withNegativePicks_unevenPickDistribution() {
        val actual = treasureClassCalculator.getLeafOutcomes("Act 1 Champ B", 4, NORMAL, DEFINED)
        val expected = TreasureClassPathAccumulator(
            mutableMapOf(
                itemLibrary.getOrConstructVirtualTreasureClass("gld") to TreasureClassPathOutcome(
                    BigFraction(60, 100),
                    ItemQualityRatios(800, 800, 800, 1024)
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("weap3") to TreasureClassPathOutcome(
                    BigFraction(
                        832772357,
                        907039232
                    ), ItemQualityRatios(800, 1000, 800, 1024)
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("armo3") to TreasureClassPathOutcome(
                    BigFraction(
                        832772357,
                        907039232
                    ), ItemQualityRatios(800, 1000, 800, 1024)
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("rin") to TreasureClassPathOutcome(
                    BigFraction(3, 500),
                    ItemQualityRatios(800, 800, 800, 1024)
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("amu") to TreasureClassPathOutcome(
                    BigFraction(3, 1000),
                    ItemQualityRatios(800, 800, 800, 1024)
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("jew") to TreasureClassPathOutcome(
                    BigFraction(3, 2000),
                    ItemQualityRatios(800, 800, 800, 1024)
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("cm3") to TreasureClassPathOutcome(
                    BigFraction(3, 2000),
                    ItemQualityRatios(800, 800, 800, 1024)
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("cm2") to TreasureClassPathOutcome(
                    BigFraction(3, 2000),
                    ItemQualityRatios(800, 800, 800, 1024)
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("cm1") to TreasureClassPathOutcome(
                    BigFraction(3, 2000),
                    ItemQualityRatios(800, 800, 800, 1024)
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("Chipped Gem") to TreasureClassPathOutcome(
                    BigFraction(
                        3,
                        200
                    ), ItemQualityRatios(800, 800, 800, 1024)
                ),
                itemLibrary.getOrConstructVirtualTreasureClass("armo6") to TreasureClassPathOutcome(
                    BigFraction(
                        1653912,
                        1771561
                    ), EMPTY
                )
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