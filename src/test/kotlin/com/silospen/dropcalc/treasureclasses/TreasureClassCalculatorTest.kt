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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TreasureClassCalculatorTest {

    private val treasureClassConfigs = readTsv(
        getResource("treasureClassCalculatorTestData/treasureclass.txt"),
        TreasureClassesLineParser()
    ).toList()

    private val itemLibrary = ItemLibrary(listOf(armor1, weapon1, weapon2, ring), emptyList(), emptyList())
    private val treasureClassCalculator = TreasureClassCalculator(
        treasureClassConfigs, itemLibrary
    )

    @Test
    fun getLeafOutcomes() {
        val expectations = listOf(
            expectation("gld", TreasureClassPathOutcome(Probability(21, 160), EMPTY, 1, 1)),
            expectation("weap3", TreasureClassPathOutcome(Probability(1, 20), EMPTY, 1, 1)),
            expectation("armo3", TreasureClassPathOutcome(Probability(1, 20), EMPTY, 1, 1)),
            expectation("Act 1 Junk", TreasureClassPathOutcome(Probability(21, 160), EMPTY, 1, 1)),
            expectation("rin", TreasureClassPathOutcome(Probability(1, 400), EMPTY, 1, 1)),
            expectation("amu", TreasureClassPathOutcome(Probability(1, 800), EMPTY, 1, 1)),
            expectation("jew", TreasureClassPathOutcome(Probability(1, 1600), EMPTY, 1, 1)),
            expectation("cm3", TreasureClassPathOutcome(Probability(1, 1600), EMPTY, 1, 1)),
            expectation("cm2", TreasureClassPathOutcome(Probability(1, 1600), EMPTY, 1, 1)),
            expectation("cm1", TreasureClassPathOutcome(Probability(1, 1600), EMPTY, 1, 1)),
            expectation("Chipped Gem", TreasureClassPathOutcome(Probability(1, 160), EMPTY, 1, 1)),
        )
        runExpectations(expectations, treasureClassCalculator.getLeafOutcomes("Act 1 H2H A", DEFINED, null))
    }

    @Test
    fun getLeafOutcomes_withFilter() {
        val expectations = listOf(
            expectation("Act 1 Junk", TreasureClassPathOutcome(Probability(21, 160), EMPTY, 1, 1)),
        )
        runExpectations(
            expectations,
            treasureClassCalculator.getLeafOutcomes(
                "Act 1 H2H A",
                DEFINED,
                itemLibrary.getOrConstructVirtualTreasureClass("Act 1 Junk")
            )
        )
    }

    @Test
    fun getLeafOutcomes_forVirtualTcs() {
        val expectations = listOf(
            TcExpectation(
                weapon1,
                listOf(TreasureClassPathOutcome(Probability(1, 30), EMPTY, 1, 1)),
                Probability(1, 30)
            ),
            TcExpectation(
                weapon2,
                listOf(TreasureClassPathOutcome(Probability(1, 60), EMPTY, 1, 1)),
                Probability(1, 60)
            ),
            TcExpectation(
                armor1,
                listOf(TreasureClassPathOutcome(Probability(1, 20), EMPTY, 1, 1)),
                Probability(1, 20)
            ),
            TcExpectation(
                ring,
                listOf(TreasureClassPathOutcome(Probability(1, 400), EMPTY, 1, 1)),
                Probability(1, 400)
            ),
        )
        runExpectations(expectations, treasureClassCalculator.getLeafOutcomes("Act 1 H2H A", VIRTUAL, null))
    }

    @Test
    fun getLeafOutcomes_withPartyAndPlayersSet() {
        val expectations = listOf(
            expectation("gld", TreasureClassPathOutcome(Probability(21, 79), EMPTY, 1, 1)),
            expectation("weap3", TreasureClassPathOutcome(Probability(8, 79), EMPTY, 1, 1)),
            expectation("armo3", TreasureClassPathOutcome(Probability(8, 79), EMPTY, 1, 1)),
            expectation("Act 1 Junk", TreasureClassPathOutcome(Probability(21, 79), EMPTY, 1, 1)),
            expectation("rin", TreasureClassPathOutcome(Probability(2, 395), EMPTY, 1, 1)),
            expectation("amu", TreasureClassPathOutcome(Probability(1, 395), EMPTY, 1, 1)),
            expectation("jew", TreasureClassPathOutcome(Probability(1, 790), EMPTY, 1, 1)),
            expectation("cm3", TreasureClassPathOutcome(Probability(1, 790), EMPTY, 1, 1)),
            expectation("cm2", TreasureClassPathOutcome(Probability(1, 790), EMPTY, 1, 1)),
            expectation("cm1", TreasureClassPathOutcome(Probability(1, 790), EMPTY, 1, 1)),
            expectation("Chipped Gem", TreasureClassPathOutcome(Probability(1, 79), EMPTY, 1, 1)),
        )
        runExpectations(expectations, treasureClassCalculator.getLeafOutcomes("Act 1 H2H A", DEFINED, null, 3, 3))
    }

    @Test
    fun getLeafOutcomes_withPicks() {
        val radamentItemQualityRatios = ItemQualityRatios(900, 900, 900, 1024)
        val expectations = listOf(
            expectation(
                "gld",
                TreasureClassPathOutcome(Probability(11, 67), radamentItemQualityRatios, 5, 1),
                Probability(799393331, 1350125107)
            ),
            expectation(
                "Act 3 Equip B",
                TreasureClassPathOutcome(Probability(19, 67), radamentItemQualityRatios, 5, 1),
                Probability(1095321139, 1350125107)
            ),
            expectation(
                "Act 3 Junk",
                TreasureClassPathOutcome(Probability(15, 67), radamentItemQualityRatios, 5, 1),
                Probability(969921075, 1350125107)
            ),
            expectation(
                "Act 3 Good",
                TreasureClassPathOutcome(Probability(3, 67), radamentItemQualityRatios, 5, 1),
                Probability(276383283, 1350125107)
            ),
        )
        runExpectations(expectations, treasureClassCalculator.getLeafOutcomes("Radament", DEFINED, null))
    }

    @Test
    fun getLeafOutcomes_withNegativePicks() {
        val topLevelQualityRatios = ItemQualityRatios(800, 800, 800, 1024)
        val subLevelQualityRatios = ItemQualityRatios(800, 1000, 800, 1024)
        val expectations = listOf(
            expectation(
                "gld",
                TreasureClassPathOutcome(Probability(60, 100), topLevelQualityRatios, 1, 1)
            ),
            expectation(
                "weap3",
                TreasureClassPathOutcome(Probability(37, 200), subLevelQualityRatios, 1, 1)
            ),
            expectation(
                "armo3",
                TreasureClassPathOutcome(Probability(37, 200), subLevelQualityRatios, 1, 1)
            ),
            expectation(
                "rin",
                TreasureClassPathOutcome(Probability(3, 500), topLevelQualityRatios, 1, 1)
            ),
            expectation(
                "amu",
                TreasureClassPathOutcome(Probability(3, 1000), topLevelQualityRatios, 1, 1)
            ),
            expectation(
                "jew",
                TreasureClassPathOutcome(Probability(3, 2000), topLevelQualityRatios, 1, 1)
            ),
            expectation(
                "cm3",
                TreasureClassPathOutcome(Probability(3, 2000), topLevelQualityRatios, 1, 1)
            ),
            expectation(
                "cm2",
                TreasureClassPathOutcome(Probability(3, 2000), topLevelQualityRatios, 1, 1)
            ),
            expectation(
                "cm1",
                TreasureClassPathOutcome(Probability(3, 2000), topLevelQualityRatios, 1, 1)
            ),
            expectation(
                "Chipped Gem",
                TreasureClassPathOutcome(Probability(3, 200), topLevelQualityRatios, 1, 1)
            ),
            expectation("Act 1 Cpot A", TreasureClassPathOutcome(Probability.ONE, EMPTY, 1, 2)),
        )
        runExpectations(expectations, treasureClassCalculator.getLeafOutcomes("Act 1 Champ A", DEFINED, null))
    }

    @Test
    fun getLeafOutcomes_withNegativePicks_unevenPickDistribution() {
        val topLevelQualityRatios = ItemQualityRatios(800, 800, 800, 1024)
        val subLevelQualityRatios = ItemQualityRatios(800, 1000, 800, 1024)
        val expectations = listOf(
            expectation(
                "gld",
                TreasureClassPathOutcome(Probability(60, 100), topLevelQualityRatios, 1, 1)
            ),
            expectation(
                "weap3",
                listOf(
                    TreasureClassPathOutcome(Probability(37, 200), subLevelQualityRatios, 1, 1),
                    TreasureClassPathOutcome(Probability(7, 22), EMPTY, 3, 2),
                ),
                Probability(832772357, 907039232)
            ),
            expectation(
                "armo3",
                listOf(
                    TreasureClassPathOutcome(Probability(37, 200), subLevelQualityRatios, 1, 1),
                    TreasureClassPathOutcome(Probability(7, 22), EMPTY, 3, 2),
                ),
                Probability(832772357, 907039232)
            ),
            expectation(
                "rin",
                TreasureClassPathOutcome(Probability(3, 500), topLevelQualityRatios, 1, 1)
            ),
            expectation(
                "amu",
                TreasureClassPathOutcome(Probability(3, 1000), topLevelQualityRatios, 1, 1)
            ),
            expectation(
                "jew",
                TreasureClassPathOutcome(Probability(3, 2000), topLevelQualityRatios, 1, 1)
            ),
            expectation(
                "cm3",
                TreasureClassPathOutcome(Probability(3, 2000), topLevelQualityRatios, 1, 1)
            ),
            expectation(
                "cm2",
                TreasureClassPathOutcome(Probability(3, 2000), topLevelQualityRatios, 1, 1)
            ),
            expectation(
                "cm1",
                TreasureClassPathOutcome(Probability(3, 2000), topLevelQualityRatios, 1, 1)
            ),
            expectation(
                "Chipped Gem",
                TreasureClassPathOutcome(Probability(3, 200), topLevelQualityRatios, 1, 1)
            ),//1-((1-(1-((1-(8/22))*(1-(8/22)))))^3)
            expectation(
                "armo6",
                TreasureClassPathOutcome(Probability(4, 11), EMPTY, 3, 2),
                Probability(1653912, 1771561)
            ),
        )
        runExpectations(expectations, treasureClassCalculator.getLeafOutcomes("Act 1 Champ B", DEFINED, null))
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
        assertEquals(levelTwoTc, treasureClassCalculator.changeTcBasedOnLevel(levelTwoTc, 3, HELL))
        assertEquals(levelNineTc, treasureClassCalculator.changeTcBasedOnLevel(levelNineTc, 8, HELL))
        assertEquals(levelNineTc, treasureClassCalculator.changeTcBasedOnLevel(levelNineTc, 9, HELL))
        assertEquals(levelNineTc, treasureClassCalculator.changeTcBasedOnLevel(levelNineTc, 15, HELL))
        assertEquals(levelNineTc, treasureClassCalculator.changeTcBasedOnLevel(levelTwoTc, 15, HELL))

        assertEquals(levelNineTc, treasureClassCalculator.changeTcBasedOnLevel(levelNineTc, 15, NIGHTMARE))
        assertEquals(levelTwoTc, treasureClassCalculator.changeTcBasedOnLevel(levelTwoTc, 15, NORMAL))
        assertEquals(levelNineTc, treasureClassCalculator.changeTcBasedOnLevel(levelNineTc, 1, HELL))
    }

    @Test
    fun tcUpgradeGroup18Example() {
        val treasureClassConfigs = readTsv(
            getResource("treasureClassCalculatorTestData/group18tcs.txt"),
            TreasureClassesLineParser()
        ).toList()
        val itemLibrary = ItemLibrary(emptyList(), emptyList(), emptyList())
        val treasureClassCalculator = TreasureClassCalculator(treasureClassConfigs, itemLibrary)

        val tc1 = treasureClassCalculator.getTreasureClass("Act 4 (N) Super Cx")
        val tc2 = treasureClassCalculator.getTreasureClass("Act 5 (H) Super C")
        val tc3 = treasureClassCalculator.getTreasureClass("Act 5 (H) Super B")

        assertEquals(
            treasureClassCalculator.getTreasureClass("Act 4 (H) Super Bx"),
            treasureClassCalculator.changeTcBasedOnLevel(tc1, 86, HELL)
        )
        assertEquals(
            treasureClassCalculator.getTreasureClass("Act 4 (H) Super Bx"),
            treasureClassCalculator.changeTcBasedOnLevel(tc2, 86, HELL)
        )
        assertEquals(
            treasureClassCalculator.getTreasureClass("Act 1 Super Cx"),
            treasureClassCalculator.changeTcBasedOnLevel(tc2, 10, HELL)
        )
        assertEquals(
            treasureClassCalculator.getTreasureClass("Act 5 (H) Super B"),
            treasureClassCalculator.changeTcBasedOnLevel(tc3, 93, HELL)
        )
        assertEquals(
            treasureClassCalculator.getTreasureClass("Act 5 (H) Super B"),
            treasureClassCalculator.changeTcBasedOnLevel(tc3, 94, HELL)
        )
        assertEquals(
            treasureClassCalculator.getTreasureClass("Act 5 (H) Super Cx"),
            treasureClassCalculator.changeTcBasedOnLevel(tc3, 96, HELL)
        )
        assertEquals(
            treasureClassCalculator.getTreasureClass("Act 5 (H) Super Cx"),
            treasureClassCalculator.changeTcBasedOnLevel(tc3, 97, HELL)
        )
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
        finalProbability: Probability
    ) = expectation(tcName, listOf(outcome), finalProbability)

    private fun expectation(
        tcName: String,
        outcome: TreasureClassPathOutcome
    ) = expectation(tcName, listOf(outcome), outcome.probability)

    private fun expectation(
        tcName: String,
        outcomes: List<TreasureClassPathOutcome>,
        finalProbability: Probability
    ) = TcExpectation(
        itemLibrary.getOrConstructVirtualTreasureClass(tcName),
        outcomes,
        finalProbability
    )
}

private data class TcExpectation(
    val outcomeType: OutcomeType,
    val outcome: List<TreasureClassPathOutcome>,
    val finalProbability: Probability
)