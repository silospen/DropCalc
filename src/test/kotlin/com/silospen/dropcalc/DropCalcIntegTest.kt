package com.silospen.dropcalc

import com.silospen.dropcalc.files.LineParser
import com.silospen.dropcalc.files.readTsv
import io.ktor.client.*
import org.apache.commons.math3.util.Precision
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.math.exp

@SpringBootTest
class DropCalcIntegTest {
    @Autowired
    private lateinit var apiResource: ApiResource
    private val testDataGenerator = TestDataGenerator(HttpClient())

    @Test
    fun test() {
        runAtomicTcTest("snowyeti2", MonsterType.REGULAR, Difficulty.NORMAL, 1, 1)
        runAtomicTcTest("snowyeti2", MonsterType.REGULAR, Difficulty.NORMAL, 3, 3)
        runAtomicTcTest("snowyeti2", MonsterType.REGULAR, Difficulty.NIGHTMARE, 3, 3)
        runAtomicTcTest("snowyeti2", MonsterType.REGULAR, Difficulty.HELL, 1, 1)
        runAtomicTcTest("snowyeti2", MonsterType.REGULAR, Difficulty.HELL, 3, 3)
        runAtomicTcTest("snowyeti2", MonsterType.CHAMPION, Difficulty.NORMAL, 1, 1)
        runAtomicTcTest("snowyeti2", MonsterType.CHAMPION, Difficulty.HELL, 1, 1)
        runAtomicTcTest("snowyeti2", MonsterType.CHAMPION, Difficulty.HELL, 8, 8)
        runAtomicTcTest("snowyeti2", MonsterType.UNIQUE, Difficulty.HELL, 8, 8)
    }

    @Test
    fun primeEvilTest(){
        runAtomicTcTest("mephisto", MonsterType.REGULAR, Difficulty.NORMAL, 1, 1, true)
        runAtomicTcTest("mephisto", MonsterType.REGULAR, Difficulty.NIGHTMARE, 1, 1, true)
        runAtomicTcTest("mephisto", MonsterType.REGULAR, Difficulty.HELL, 6, 6, true)
        runAtomicTcTest("diablo", MonsterType.REGULAR, Difficulty.NORMAL, 1, 1, true)
        runAtomicTcTest("diablo", MonsterType.REGULAR, Difficulty.NIGHTMARE, 1, 1, true)
        runAtomicTcTest("diablo", MonsterType.REGULAR, Difficulty.HELL, 6, 6, true)
        runAtomicTcTest("baalcrab", MonsterType.REGULAR, Difficulty.NORMAL, 1, 1, true)
        runAtomicTcTest("baalcrab", MonsterType.REGULAR, Difficulty.NIGHTMARE, 1, 1, true)
        runAtomicTcTest("baalcrab", MonsterType.REGULAR, Difficulty.HELL, 6, 6, true)
    }

    @Test
    fun bossTest() {
        runAtomicTcTest("putriddefiler1", MonsterType.REGULAR, Difficulty.NORMAL, 1, 1, true)
        runAtomicTcTest("putriddefiler2", MonsterType.REGULAR, Difficulty.NORMAL, 1, 1, true)
        runAtomicTcTest("putriddefiler3", MonsterType.REGULAR, Difficulty.NORMAL, 1, 1, true)
        runAtomicTcTest("putriddefiler4", MonsterType.REGULAR, Difficulty.NORMAL, 1, 1, true)
        runAtomicTcTest("putriddefiler5", MonsterType.REGULAR, Difficulty.NORMAL, 1, 1, true)
        runAtomicTcTest("putriddefiler1", MonsterType.REGULAR, Difficulty.NIGHTMARE, 5, 3, true)
        runAtomicTcTest("putriddefiler2", MonsterType.REGULAR, Difficulty.NIGHTMARE, 5, 3, true)
        runAtomicTcTest("putriddefiler3", MonsterType.REGULAR, Difficulty.NIGHTMARE, 5, 3, true)
        runAtomicTcTest("putriddefiler4", MonsterType.REGULAR, Difficulty.NIGHTMARE, 5, 3, true)
        runAtomicTcTest("putriddefiler5", MonsterType.REGULAR, Difficulty.NIGHTMARE, 5, 3, true)
        runAtomicTcTest("putriddefiler1", MonsterType.REGULAR, Difficulty.HELL, 8, 8, true)
        runAtomicTcTest("putriddefiler2", MonsterType.REGULAR, Difficulty.HELL, 8, 8, true)
        runAtomicTcTest("putriddefiler3", MonsterType.REGULAR, Difficulty.HELL, 8, 8, true)
        runAtomicTcTest("putriddefiler4", MonsterType.REGULAR, Difficulty.HELL, 8, 8, true)
        runAtomicTcTest("putriddefiler5", MonsterType.REGULAR, Difficulty.HELL, 8, 8, true)


//        runAtomicTcTest("andariel", MonsterType.REGULAR, Difficulty.HELL, 8, 8, true)


//        runAtomicTcTest("radament", MonsterType.REGULAR, Difficulty.NORMAL, 1, 1, true)
//        runAtomicTcTest("radament", MonsterType.REGULAR, Difficulty.HELL, 1, 1, true)
//        runAtomicTcTest("radament", MonsterType.REGULAR, Difficulty.HELL, 6, 6, true)

//        runAtomicTcTest("summoner", MonsterType.REGULAR, Difficulty.NORMAL, 1, 1, true)
//        runAtomicTcTest("summoner", MonsterType.REGULAR, Difficulty.HELL, 1, 1, true)
//        runAtomicTcTest("summoner", MonsterType.REGULAR, Difficulty.HELL, 6, 6, true)



//        "duriel"
//        "radament"
//        "summoner"
//        "izual"
//        "bloodraven"
//        "griswold"
//        "nihlathakboss"

    }

    fun runAtomicTcTest(
        monsterId: String,
        monsterType: MonsterType,
        difficulty: Difficulty,
        nPlayers: Int,
        partySize: Int,
        isBoss: Boolean = false
    ) {
        val actual = apiResource.getAtomicTcs(monsterId, monsterType, difficulty, nPlayers, partySize)
        val expected = readTsv(
            testDataGenerator.generateTcExpectationDataToFile(
                monsterId,
                monsterType,
                difficulty,
                nPlayers,
                partySize,
                isBoss
            ),
            tcExpectationDataLineParser
        )
        assertTrue(expected.isNotEmpty(), "Expected is empty")
        assertTrue(actual.isNotEmpty(), "Actual is empty")
        runAtomicTcAsserts(actual, expected, "$monsterId, $monsterType, $difficulty, $nPlayers, $partySize")
    }

    private fun runAtomicTcAsserts(
        actual: List<AtomicTcsResponse>,
        expected: List<AtomicTcsResponse>,
        testInput: String
    ) {
        val actualsByTcAndArea: Map<Pair<String, String>, List<AtomicTcsResponse>> =
            actual.groupBy { it.tc to it.area }
        val outcomes = expected
            .filterNot { brokenTreasureClasses.contains(it.tc) }
            .map {
                val key = it.tc to it.area
                val value = actualsByTcAndArea.getValue(key)
                if (value.size != 1) throw RuntimeException("Multiple probs found for $it and $value")
                val actualProb = value[0].prob
                val expectedProb = it.prob
                key to Precision.equals(actualProb.dec, expectedProb.dec, 0.00000000001)
            }
        val failedAsserts = outcomes.filter { !it.second }
        if (failedAsserts.isNotEmpty()) {
            println("Failed: $testInput on ${failedAsserts.joinToString("\n")}")
            fail()
        } else {
            println("Succeeded: $testInput")
        }
    }

    private val tcExpectationDataLineParser = object : LineParser<AtomicTcsResponse?> {
        override fun parseLine(line: List<String>): AtomicTcsResponse =
            AtomicTcsResponse(line[0], line[1], Probability("", line[2].toDouble()))
    }

    private val brokenTreasureClasses = setOf(
        //TODO: CM, JEW, RIN don't appear to have changes in drop rate due to player changes? Is this correct?
        "cm1",
        "cm2",
        "cm3",
        "jew",
        "rin",
        "amu"
    )
}
