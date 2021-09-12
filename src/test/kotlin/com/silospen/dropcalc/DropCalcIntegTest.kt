package com.silospen.dropcalc

import com.silospen.dropcalc.files.LineParser
import com.silospen.dropcalc.files.getResource
import com.silospen.dropcalc.files.readTsv
import io.ktor.client.*
import org.apache.commons.math3.util.Precision
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.File

@SpringBootTest
class DropCalcIntegTest {
    @Autowired
    private lateinit var apiResource: ApiResource
    private val testDataGenerator = TestDataGenerator(HttpClient())

    @Test
    fun test() {
        runAtomicTcTestWithRemoteExpectations("snowyeti2", MonsterType.REGULAR, Difficulty.NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("snowyeti2", MonsterType.REGULAR, Difficulty.NORMAL, 3, 3)
        runAtomicTcTestWithRemoteExpectations("snowyeti2", MonsterType.REGULAR, Difficulty.NIGHTMARE, 3, 3)
        runAtomicTcTestWithRemoteExpectations("snowyeti2", MonsterType.REGULAR, Difficulty.HELL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("snowyeti2", MonsterType.REGULAR, Difficulty.HELL, 3, 3)
        runAtomicTcTestWithRemoteExpectations("snowyeti2", MonsterType.CHAMPION, Difficulty.NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("snowyeti2", MonsterType.CHAMPION, Difficulty.HELL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("snowyeti2", MonsterType.CHAMPION, Difficulty.HELL, 8, 8)
        runAtomicTcTestWithRemoteExpectations("snowyeti2", MonsterType.UNIQUE, Difficulty.HELL, 8, 8)
    }

    @Test
    fun primeEvilTest() {
        runAtomicTcTestWithRemoteExpectations("mephisto", MonsterType.REGULAR, Difficulty.NORMAL, 1, 1, true)
        runAtomicTcTestWithRemoteExpectations("mephisto", MonsterType.REGULAR, Difficulty.NIGHTMARE, 1, 1, true)
        runAtomicTcTestWithRemoteExpectations("mephisto", MonsterType.REGULAR, Difficulty.HELL, 6, 6, true)
        runAtomicTcTestWithRemoteExpectations("mephistoq", MonsterType.REGULAR, Difficulty.HELL, 6, 6, true)
        runAtomicTcTestWithRemoteExpectations("diablo", MonsterType.REGULAR, Difficulty.NORMAL, 1, 1, true)
        runAtomicTcTestWithRemoteExpectations("diablo", MonsterType.REGULAR, Difficulty.NIGHTMARE, 1, 1, true)
        runAtomicTcTestWithRemoteExpectations("diablo", MonsterType.REGULAR, Difficulty.HELL, 6, 6, true)
        runAtomicTcTestWithRemoteExpectations("diabloq", MonsterType.REGULAR, Difficulty.HELL, 6, 6, true)
        runAtomicTcTestWithRemoteExpectations("baalcrab", MonsterType.REGULAR, Difficulty.NORMAL, 1, 1, true)
        runAtomicTcTestWithRemoteExpectations("baalcrab", MonsterType.REGULAR, Difficulty.NIGHTMARE, 1, 1, true)
        runAtomicTcTestWithRemoteExpectations("baalcrab", MonsterType.REGULAR, Difficulty.HELL, 6, 6, true)
        runAtomicTcTestWithRemoteExpectations("baalcrabq", MonsterType.REGULAR, Difficulty.HELL, 6, 6, true)
    }

    @Test
    fun bossTest() {
        runAtomicTcTestWithRemoteExpectations("radament", MonsterType.REGULAR, Difficulty.NORMAL, 1, 1, true)
        runAtomicTcTestWithRemoteExpectations("radament", MonsterType.REGULAR, Difficulty.NIGHTMARE, 1, 1, true)
        runAtomicTcTestWithRemoteExpectations("radament", MonsterType.REGULAR, Difficulty.NIGHTMARE, 6, 6, true)
        runAtomicTcTestWithRemoteExpectations("radament", MonsterType.REGULAR, Difficulty.HELL, 1, 1, true)

        runAtomicTcTestWithRemoteExpectations("summoner", MonsterType.REGULAR, Difficulty.NORMAL, 1, 1, true)
        runAtomicTcTestWithRemoteExpectations("summoner", MonsterType.REGULAR, Difficulty.NIGHTMARE, 1, 1, true)
        runAtomicTcTestWithRemoteExpectations("summoner", MonsterType.REGULAR, Difficulty.HELL, 1, 1, true)
        runAtomicTcTestWithRemoteExpectations("summoner", MonsterType.REGULAR, Difficulty.HELL, 7, 7, true)

        runAtomicTcTestWithRemoteExpectations("izual", MonsterType.REGULAR, Difficulty.NORMAL, 1, 1, true)
        runAtomicTcTestWithRemoteExpectations("izual", MonsterType.REGULAR, Difficulty.NIGHTMARE, 1, 1, true)
        runAtomicTcTestWithRemoteExpectations("izual", MonsterType.REGULAR, Difficulty.HELL, 1, 1, true)
        runAtomicTcTestWithRemoteExpectations("izual", MonsterType.REGULAR, Difficulty.HELL, 7, 7, true)

        runAtomicTcTestWithRemoteExpectations("bloodraven", MonsterType.REGULAR, Difficulty.NORMAL, 1, 1, true)
        runAtomicTcTestWithRemoteExpectations("bloodraven", MonsterType.REGULAR, Difficulty.NIGHTMARE, 1, 1, true)
        runAtomicTcTestWithRemoteExpectations("bloodraven", MonsterType.REGULAR, Difficulty.HELL, 1, 1, true)
        runAtomicTcTestWithRemoteExpectations("bloodraven", MonsterType.REGULAR, Difficulty.HELL, 7, 7, true)

        runAtomicTcTestWithRemoteExpectations("nihlathakboss", MonsterType.REGULAR, Difficulty.NORMAL, 1, 1, true)
        runAtomicTcTestWithRemoteExpectations("nihlathakboss", MonsterType.REGULAR, Difficulty.NIGHTMARE, 1, 1, true)
        runAtomicTcTestWithRemoteExpectations("nihlathakboss", MonsterType.REGULAR, Difficulty.HELL, 1, 1, true)
        runAtomicTcTestWithRemoteExpectations("nihlathakboss", MonsterType.REGULAR, Difficulty.HELL, 7, 7, true)

        runAtomicTcTestWithRemoteExpectations("andariel", MonsterType.REGULAR, Difficulty.NORMAL, 1, 1, true)
        runAtomicTcTestWithRemoteExpectations("andariel", MonsterType.REGULAR, Difficulty.NIGHTMARE, 1, 1, true)
        runAtomicTcTestWithRemoteExpectations("andariel", MonsterType.REGULAR, Difficulty.HELL, 1, 1, true)
        runAtomicTcTestWithRemoteExpectations("andariel", MonsterType.REGULAR, Difficulty.HELL, 7, 7, true)

        runAtomicTcTestWithRemoteExpectations("putriddefiler1", MonsterType.REGULAR, Difficulty.NORMAL, 1, 1, true)
        runAtomicTcTestWithRemoteExpectations("putriddefiler2", MonsterType.REGULAR, Difficulty.NORMAL, 1, 1, true)
        runAtomicTcTestWithRemoteExpectations("putriddefiler3", MonsterType.REGULAR, Difficulty.NORMAL, 1, 1, true)
        runAtomicTcTestWithRemoteExpectations("putriddefiler4", MonsterType.REGULAR, Difficulty.NORMAL, 1, 1, true)
        runAtomicTcTestWithRemoteExpectations("putriddefiler5", MonsterType.REGULAR, Difficulty.NORMAL, 1, 1, true)
        runAtomicTcTestWithRemoteExpectations("putriddefiler1", MonsterType.REGULAR, Difficulty.NIGHTMARE, 5, 3, true)
        runAtomicTcTestWithRemoteExpectations("putriddefiler2", MonsterType.REGULAR, Difficulty.NIGHTMARE, 5, 3, true)
        runAtomicTcTestWithRemoteExpectations("putriddefiler3", MonsterType.REGULAR, Difficulty.NIGHTMARE, 5, 3, true)
        runAtomicTcTestWithRemoteExpectations("putriddefiler4", MonsterType.REGULAR, Difficulty.NIGHTMARE, 5, 3, true)
        runAtomicTcTestWithRemoteExpectations("putriddefiler5", MonsterType.REGULAR, Difficulty.NIGHTMARE, 5, 3, true)
        runAtomicTcTestWithRemoteExpectations("putriddefiler1", MonsterType.REGULAR, Difficulty.HELL, 8, 8, true)
        runAtomicTcTestWithRemoteExpectations("putriddefiler2", MonsterType.REGULAR, Difficulty.HELL, 8, 8, true)
        runAtomicTcTestWithRemoteExpectations("putriddefiler3", MonsterType.REGULAR, Difficulty.HELL, 8, 8, true)
        runAtomicTcTestWithRemoteExpectations("putriddefiler4", MonsterType.REGULAR, Difficulty.HELL, 8, 8, true)
        runAtomicTcTestWithRemoteExpectations("putriddefiler5", MonsterType.REGULAR, Difficulty.HELL, 8, 8, true)

//        runAtomicTcTest("duriel", MonsterType.REGULAR, Difficulty.NORMAL, 1, 1, true)
//        runAtomicTcTest("duriel", MonsterType.REGULAR, Difficulty.NIGHTMARE, 1, 1, true)
//        runAtomicTcTest("duriel", MonsterType.REGULAR, Difficulty.HELL, 1, 1, true)
//        runAtomicTcTest("duriel", MonsterType.REGULAR, Difficulty.HELL, 7, 7, true)
//
    }

    @Test
    fun runLocalTests() = getResource("integExpectations/tcTests").listFiles()!!.forEach {
        val parts = it.name.removeSuffix(".tsv").split("_")
        runAtomicTcTestWithLocalExpectations(
            parts[0],
            MonsterType.valueOf(parts[1]),
            Difficulty.valueOf(parts[2]),
            parts[3].toInt(),
            parts[4].toInt(),
            it
        )
    }

    fun runAtomicTcTestWithRemoteExpectations(
        monsterId: String,
        monsterType: MonsterType,
        difficulty: Difficulty,
        nPlayers: Int,
        partySize: Int,
        isBoss: Boolean = false
    ) = runAtomicTcTestWithLocalExpectations(
        monsterId, monsterType, difficulty, nPlayers, partySize, testDataGenerator.generateTcExpectationDataToFile(
            monsterId,
            monsterType,
            difficulty,
            nPlayers,
            partySize,
            isBoss
        )
    )

    fun runAtomicTcTestWithLocalExpectations(
        monsterId: String,
        monsterType: MonsterType,
        difficulty: Difficulty,
        nPlayers: Int,
        partySize: Int,
        file: File
    ) {
        val actual = apiResource.getAtomicTcs(monsterId, monsterType, difficulty, nPlayers, partySize)
        val expected = readTsv(
            file,
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
        "amu",
        "pk2",
        "pk3"
    )
}
