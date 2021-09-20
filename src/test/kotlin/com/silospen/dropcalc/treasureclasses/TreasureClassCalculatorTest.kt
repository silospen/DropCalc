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
            expectation("gld", TreasureClassPathOutcome(BigFraction(21, 160), EMPTY, 1, 1)),
            expectation("weap3", TreasureClassPathOutcome(BigFraction(1, 20), EMPTY, 1, 1)),
            expectation("armo3", TreasureClassPathOutcome(BigFraction(1, 20), EMPTY, 1, 1)),
            expectation("Act 1 Junk", TreasureClassPathOutcome(BigFraction(21, 160), EMPTY, 1, 1)),
            expectation("rin", TreasureClassPathOutcome(BigFraction(1, 400), EMPTY, 1, 1)),
            expectation("amu", TreasureClassPathOutcome(BigFraction(1, 800), EMPTY, 1, 1)),
            expectation("jew", TreasureClassPathOutcome(BigFraction(1, 1600), EMPTY, 1, 1)),
            expectation("cm3", TreasureClassPathOutcome(BigFraction(1, 1600), EMPTY, 1, 1)),
            expectation("cm2", TreasureClassPathOutcome(BigFraction(1, 1600), EMPTY, 1, 1)),
            expectation("cm1", TreasureClassPathOutcome(BigFraction(1, 1600), EMPTY, 1, 1)),
            expectation("Chipped Gem", TreasureClassPathOutcome(BigFraction(1, 160), EMPTY, 1, 1)),
        )
        runExpectations(expectations, treasureClassCalculator.getLeafOutcomes("Act 1 H2H A", 0, NORMAL, DEFINED))
    }

    @Test
    fun getLeafOutcomes_forVirtualTcs() {
        val expectations = listOf(
            TcExpectation(
                weapon1,
                listOf(TreasureClassPathOutcome(BigFraction(1, 30), EMPTY, 1, 1)),
                BigFraction(1, 30)
            ),
            TcExpectation(
                weapon2,
                listOf(TreasureClassPathOutcome(BigFraction(1, 60), EMPTY, 1, 1)),
                BigFraction(1, 60)
            ),
            TcExpectation(
                armor1,
                listOf(TreasureClassPathOutcome(BigFraction(1, 20), EMPTY, 1, 1)),
                BigFraction(1, 20)
            ),
            TcExpectation(
                ring,
                listOf(TreasureClassPathOutcome(BigFraction(1, 400), EMPTY, 1, 1)),
                BigFraction(1, 400)
            ),
        )
        runExpectations(expectations, treasureClassCalculator.getLeafOutcomes("Act 1 H2H A", 0, NORMAL, VIRTUAL))
    }

    @Test
    fun getLeafOutcomes_withTcUpgraded() {
        val expectedProbabilities = BigFraction(5, 110).multiply(BigFraction(7, 14))
        val expectations = listOf(
            expectation("gld", TreasureClassPathOutcome(BigFraction(5, 110), EMPTY, 1, 1)),
            expectation("weap3", TreasureClassPathOutcome(expectedProbabilities, EMPTY, 1, 1)),
            expectation("armo3", TreasureClassPathOutcome(expectedProbabilities, EMPTY, 1, 1)),
        )
        runExpectations(expectations, treasureClassCalculator.getLeafOutcomes("Act 1 H2H A", 50, HELL, DEFINED))
    }

    @Test
    fun getLeafOutcomes_withPartyAndPlayersSet() {
        val expectations = listOf(
            expectation("gld", TreasureClassPathOutcome(BigFraction(21, 79), EMPTY, 1, 1)),
            expectation("weap3", TreasureClassPathOutcome(BigFraction(8, 79), EMPTY, 1, 1)),
            expectation("armo3", TreasureClassPathOutcome(BigFraction(8, 79), EMPTY, 1, 1)),
            expectation("Act 1 Junk", TreasureClassPathOutcome(BigFraction(21, 79), EMPTY, 1, 1)),
            expectation("rin", TreasureClassPathOutcome(BigFraction(2, 395), EMPTY, 1, 1)),
            expectation("amu", TreasureClassPathOutcome(BigFraction(1, 395), EMPTY, 1, 1)),
            expectation("jew", TreasureClassPathOutcome(BigFraction(1, 790), EMPTY, 1, 1)),
            expectation("cm3", TreasureClassPathOutcome(BigFraction(1, 790), EMPTY, 1, 1)),
            expectation("cm2", TreasureClassPathOutcome(BigFraction(1, 790), EMPTY, 1, 1)),
            expectation("cm1", TreasureClassPathOutcome(BigFraction(1, 790), EMPTY, 1, 1)),
            expectation("Chipped Gem", TreasureClassPathOutcome(BigFraction(1, 79), EMPTY, 1, 1)),
        )
        runExpectations(expectations, treasureClassCalculator.getLeafOutcomes("Act 1 H2H A", 0, NORMAL, DEFINED, 3, 3))
    }

    @Test
    fun getLeafOutcomes_withPicks() {
        val radamentItemQualityRatios = ItemQualityRatios(900, 900, 900, 1024)
        val expectations = listOf(
            expectation(
                "gld",
                TreasureClassPathOutcome(BigFraction(11, 67), radamentItemQualityRatios, 5, 1),
                BigFraction(799393331, 1350125107)
            ),
            expectation(
                "Act 3 Equip B",
                TreasureClassPathOutcome(BigFraction(19, 67), radamentItemQualityRatios, 5, 1),
                BigFraction(1095321139, 1350125107)
            ),
            expectation(
                "Act 3 Junk",
                TreasureClassPathOutcome(BigFraction(15, 67), radamentItemQualityRatios, 5, 1),
                BigFraction(969921075, 1350125107)
            ),
            expectation(
                "Act 3 Good",
                TreasureClassPathOutcome(BigFraction(3, 67), radamentItemQualityRatios, 5, 1),
                BigFraction(276383283, 1350125107)
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
                TreasureClassPathOutcome(BigFraction(60, 100), topLevelQualityRatios, 1, 1)
            ),
            expectation(
                "weap3",
                TreasureClassPathOutcome(BigFraction(37, 200), subLevelQualityRatios, 1, 1)
            ),
            expectation(
                "armo3",
                TreasureClassPathOutcome(BigFraction(37, 200), subLevelQualityRatios, 1, 1)
            ),
            expectation(
                "rin",
                TreasureClassPathOutcome(BigFraction(3, 500), topLevelQualityRatios, 1, 1)
            ),
            expectation(
                "amu",
                TreasureClassPathOutcome(BigFraction(3, 1000), topLevelQualityRatios, 1, 1)
            ),
            expectation(
                "jew",
                TreasureClassPathOutcome(BigFraction(3, 2000), topLevelQualityRatios, 1, 1)
            ),
            expectation(
                "cm3",
                TreasureClassPathOutcome(BigFraction(3, 2000), topLevelQualityRatios, 1, 1)
            ),
            expectation(
                "cm2",
                TreasureClassPathOutcome(BigFraction(3, 2000), topLevelQualityRatios, 1, 1)
            ),
            expectation(
                "cm1",
                TreasureClassPathOutcome(BigFraction(3, 2000), topLevelQualityRatios, 1, 1)
            ),
            expectation(
                "Chipped Gem",
                TreasureClassPathOutcome(BigFraction(3, 200), topLevelQualityRatios, 1, 1)
            ),
            expectation("Act 1 Cpot A", TreasureClassPathOutcome(BigFraction(1), EMPTY, 1, 2)),
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
                TreasureClassPathOutcome(BigFraction(60, 100), topLevelQualityRatios, 1, 1)
            ),
            expectation(
                "weap3",
                listOf(
                    TreasureClassPathOutcome(BigFraction(37, 200), subLevelQualityRatios, 1, 1),
                    TreasureClassPathOutcome(BigFraction(7, 22), EMPTY, 3, 2),
                ),
                BigFraction(832772357, 907039232)
            ),
            expectation(
                "armo3",
                listOf(
                    TreasureClassPathOutcome(BigFraction(37, 200), subLevelQualityRatios, 1, 1),
                    TreasureClassPathOutcome(BigFraction(7, 22), EMPTY, 3, 2),
                ),
                BigFraction(832772357, 907039232)
            ),
            expectation(
                "rin",
                TreasureClassPathOutcome(BigFraction(3, 500), topLevelQualityRatios, 1, 1)
            ),
            expectation(
                "amu",
                TreasureClassPathOutcome(BigFraction(3, 1000), topLevelQualityRatios, 1, 1)
            ),
            expectation(
                "jew",
                TreasureClassPathOutcome(BigFraction(3, 2000), topLevelQualityRatios, 1, 1)
            ),
            expectation(
                "cm3",
                TreasureClassPathOutcome(BigFraction(3, 2000), topLevelQualityRatios, 1, 1)
            ),
            expectation(
                "cm2",
                TreasureClassPathOutcome(BigFraction(3, 2000), topLevelQualityRatios, 1, 1)
            ),
            expectation(
                "cm1",
                TreasureClassPathOutcome(BigFraction(3, 2000), topLevelQualityRatios, 1, 1)
            ),
            expectation(
                "Chipped Gem",
                TreasureClassPathOutcome(BigFraction(3, 200), topLevelQualityRatios, 1, 1)
            ),//1-((1-(1-((1-(8/22))*(1-(8/22)))))^3)
            expectation(
                "armo6",
                TreasureClassPathOutcome(BigFraction(4, 11), EMPTY, 3, 2),
                BigFraction(1653912, 1771561)
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
        accumulator: TreasureClassPaths
    ) {
        val actual = accumulator.map {
            TcExpectation(it, accumulator.getSubPaths(it), accumulator.getFinalProbability(it))
        }
        assertEquals(expectations.sortedBy { it.outcomeType.name }, actual.sortedBy { it.outcomeType.name })
    }

    private fun expectation(
        tcName: String,
        outcome: TreasureClassPathOutcome,
        finalProbability: BigFraction
    ) = expectation(tcName, listOf(outcome), finalProbability)

    private fun expectation(
        tcName: String,
        outcome: TreasureClassPathOutcome
    ) = expectation(tcName, listOf(outcome), outcome.probability)

    private fun expectation(
        tcName: String,
        outcomes: List<TreasureClassPathOutcome>,
        finalProbability: BigFraction
    ) = TcExpectation(
        itemLibrary.getOrConstructVirtualTreasureClass(tcName),
        outcomes,
        finalProbability
    )
}

private data class TcExpectation(
    val outcomeType: OutcomeType,
    val outcome: List<TreasureClassPathOutcome>,
    val finalProbability: BigFraction
)