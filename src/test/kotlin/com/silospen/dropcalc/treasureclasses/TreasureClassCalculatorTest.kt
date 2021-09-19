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
        val expectations = listOf(
            expectation("gld", TreasureClassPathOutcome(BigFraction(21, 160), EMPTY)),
            expectation("weap3", TreasureClassPathOutcome(BigFraction(1, 20), EMPTY)),
            expectation("armo3", TreasureClassPathOutcome(BigFraction(1, 20), EMPTY)),
            expectation("Act 1 Junk", TreasureClassPathOutcome(BigFraction(21, 160), EMPTY)),
            expectation("rin", TreasureClassPathOutcome(BigFraction(1, 400), EMPTY)),
            expectation("amu", TreasureClassPathOutcome(BigFraction(1, 800), EMPTY)),
            expectation("jew", TreasureClassPathOutcome(BigFraction(1, 1600), EMPTY)),
            expectation("cm3", TreasureClassPathOutcome(BigFraction(1, 1600), EMPTY)),
            expectation("cm2", TreasureClassPathOutcome(BigFraction(1, 1600), EMPTY)),
            expectation("cm1", TreasureClassPathOutcome(BigFraction(1, 1600), EMPTY)),
            expectation("Chipped Gem", TreasureClassPathOutcome(BigFraction(1, 160), EMPTY)),
        )
        runExpectations(expectations, treasureClassCalculator.getLeafOutcomes("Act 1 H2H A", 0, NORMAL, DEFINED))
    }

    @Test
    fun getLeafOutcomes_forVirtualTcs() {
        val expectations = listOf(
            TcExpectation(weapon1, TreasureClassPathOutcome(BigFraction(1, 30), EMPTY)),
            TcExpectation(weapon2, TreasureClassPathOutcome(BigFraction(1, 60), EMPTY)),
            TcExpectation(armor1, TreasureClassPathOutcome(BigFraction(1, 20), EMPTY)),
            TcExpectation(ring, TreasureClassPathOutcome(BigFraction(1, 400), EMPTY)),
        )
        runExpectations(expectations, treasureClassCalculator.getLeafOutcomes("Act 1 H2H A", 0, NORMAL, VIRTUAL))
    }

    @Test
    fun getLeafOutcomes_withTcUpgraded() {
        val expectedProbabilities = BigFraction(5, 110).multiply(BigFraction(7, 14))
        val expectations = listOf(
            expectation("gld", TreasureClassPathOutcome(BigFraction(5, 110), EMPTY)),
            expectation("weap3", TreasureClassPathOutcome(expectedProbabilities, EMPTY)),
            expectation("armo3", TreasureClassPathOutcome(expectedProbabilities, EMPTY)),
        )
        runExpectations(expectations, treasureClassCalculator.getLeafOutcomes("Act 1 H2H A", 50, HELL, DEFINED))
    }

    @Test
    fun getLeafOutcomes_withPartyAndPlayersSet() {
        val expectations = listOf(
            expectation("gld", TreasureClassPathOutcome(BigFraction(21, 79), EMPTY)),
            expectation("weap3", TreasureClassPathOutcome(BigFraction(8, 79), EMPTY)),
            expectation("armo3", TreasureClassPathOutcome(BigFraction(8, 79), EMPTY)),
            expectation("Act 1 Junk", TreasureClassPathOutcome(BigFraction(21, 79), EMPTY)),
            expectation("rin", TreasureClassPathOutcome(BigFraction(2, 395), EMPTY)),
            expectation("amu", TreasureClassPathOutcome(BigFraction(1, 395), EMPTY)),
            expectation("jew", TreasureClassPathOutcome(BigFraction(1, 790), EMPTY)),
            expectation("cm3", TreasureClassPathOutcome(BigFraction(1, 790), EMPTY)),
            expectation("cm2", TreasureClassPathOutcome(BigFraction(1, 790), EMPTY)),
            expectation("cm1", TreasureClassPathOutcome(BigFraction(1, 790), EMPTY)),
            expectation("Chipped Gem", TreasureClassPathOutcome(BigFraction(1, 79), EMPTY)),
        )
        runExpectations(expectations, treasureClassCalculator.getLeafOutcomes("Act 1 H2H A", 0, NORMAL, DEFINED, 3, 3))
    }

    @Test
    fun getLeafOutcomes_withPicks() {
        val radamentItemQualityRatios = ItemQualityRatios(900, 900, 900, 1024)
        val expectations = listOf(
            expectation(
                "gld",
                TreasureClassPathOutcome(BigFraction(799393331, 1350125107), radamentItemQualityRatios)
            ),
            expectation(
                "Act 3 Equip B",
                TreasureClassPathOutcome(BigFraction(1095321139, 1350125107), radamentItemQualityRatios)
            ),
            expectation(
                "Act 3 Junk",
                TreasureClassPathOutcome(BigFraction(969921075, 1350125107), radamentItemQualityRatios)
            ),
            expectation(
                "Act 3 Good",
                TreasureClassPathOutcome(BigFraction(276383283, 1350125107), radamentItemQualityRatios)
            ),
        )
        runExpectations(expectations, treasureClassCalculator.getLeafOutcomes("Radament", 50, NORMAL, DEFINED))
    }

    @Test
    fun getLeafOutcomes_withNegativePicks() {
        val topLevelQualityRatios = ItemQualityRatios(800, 800, 800, 1024)
        val subLevelQualityRatios = ItemQualityRatios(800, 1000, 800, 1024)
        val expectations = listOf(
            expectation(
                "gld",
                TreasureClassPathOutcome(BigFraction(60, 100), topLevelQualityRatios)
            ),
            expectation(
                "weap3",
                TreasureClassPathOutcome(BigFraction(37, 200), subLevelQualityRatios)
            ),
            expectation(
                "armo3",
                TreasureClassPathOutcome(BigFraction(37, 200), subLevelQualityRatios)
            ),
            expectation(
                "rin",
                TreasureClassPathOutcome(BigFraction(3, 500), topLevelQualityRatios)
            ),
            expectation(
                "amu",
                TreasureClassPathOutcome(BigFraction(3, 1000), topLevelQualityRatios)
            ),
            expectation(
                "jew",
                TreasureClassPathOutcome(BigFraction(3, 2000), topLevelQualityRatios)
            ),
            expectation(
                "cm3",
                TreasureClassPathOutcome(BigFraction(3, 2000), topLevelQualityRatios)
            ),
            expectation(
                "cm2",
                TreasureClassPathOutcome(BigFraction(3, 2000), topLevelQualityRatios)
            ),
            expectation(
                "cm1",
                TreasureClassPathOutcome(BigFraction(3, 2000), topLevelQualityRatios)
            ),
            expectation(
                "Chipped Gem",
                TreasureClassPathOutcome(BigFraction(3, 200), topLevelQualityRatios)
            ),
            expectation("Act 1 Cpot A", TreasureClassPathOutcome(BigFraction(1), EMPTY)),
        )
        runExpectations(expectations, treasureClassCalculator.getLeafOutcomes("Act 1 Champ A", 4, NORMAL, DEFINED))
    }

    @Test
    fun getLeafOutcomes_withNegativePicks_unevenPickDistribution() {
        val topLevelQualityRatios = ItemQualityRatios(800, 800, 800, 1024)
        val subLevelQualityRatios = ItemQualityRatios(800, 1000, 800, 1024)
        val expectations = listOf(
            expectation(
                "gld",
                TreasureClassPathOutcome(BigFraction(60, 100), topLevelQualityRatios)
            ),
            expectation(
                "weap3",
                TreasureClassPathOutcome(BigFraction(832772357, 907039232), subLevelQualityRatios)
            ),
            expectation(
                "armo3",
                TreasureClassPathOutcome(BigFraction(832772357, 907039232), subLevelQualityRatios)
            ),
            expectation(
                "rin",
                TreasureClassPathOutcome(BigFraction(3, 500), topLevelQualityRatios)
            ),
            expectation(
                "amu",
                TreasureClassPathOutcome(BigFraction(3, 1000), topLevelQualityRatios)
            ),
            expectation(
                "jew",
                TreasureClassPathOutcome(BigFraction(3, 2000), topLevelQualityRatios)
            ),
            expectation(
                "cm3",
                TreasureClassPathOutcome(BigFraction(3, 2000), topLevelQualityRatios)
            ),
            expectation(
                "cm2",
                TreasureClassPathOutcome(BigFraction(3, 2000), topLevelQualityRatios)
            ),
            expectation(
                "cm1",
                TreasureClassPathOutcome(BigFraction(3, 2000), topLevelQualityRatios)
            ),
            expectation(
                "Chipped Gem",
                TreasureClassPathOutcome(BigFraction(3, 200), topLevelQualityRatios)
            ),//1-((1-(1-((1-(8/22))*(1-(8/22)))))^3)
            expectation(
                "armo6",
                TreasureClassPathOutcome(BigFraction(1653912, 1771561), EMPTY)
            ),
        )
        runExpectations(expectations, treasureClassCalculator.getLeafOutcomes("Act 1 Champ B", 4, NORMAL, DEFINED))
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

    private fun runExpectations(
        expectations: List<TcExpectation>,
        accumulator: TreasureClassPathAccumulator
    ) {
        val actual = accumulator.getOutcomes().map {
            TcExpectation(it.key, it.value)
        }
        assertEquals(expectations.sortedBy { it.outcomeType.name }, actual.sortedBy { it.outcomeType.name })
    }

    private fun expectation(
        tcName: String,
        outcome: TreasureClassPathOutcome
    ) = TcExpectation(
        itemLibrary.getOrConstructVirtualTreasureClass(tcName),
        outcome
    )
}

private data class TcExpectation(
    val outcomeType: OutcomeType,
    val outcome: TreasureClassPathOutcome
)