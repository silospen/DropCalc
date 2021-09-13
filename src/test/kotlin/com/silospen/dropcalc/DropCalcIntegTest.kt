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
        runAtomicTcTestWithRemoteExpectations("mephisto", MonsterType.BOSS, Difficulty.NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("mephisto", MonsterType.BOSS, Difficulty.NIGHTMARE, 1, 1)
        runAtomicTcTestWithRemoteExpectations("mephisto", MonsterType.BOSS, Difficulty.HELL, 6, 6)
        runAtomicTcTestWithRemoteExpectations("mephistoq", MonsterType.BOSS, Difficulty.HELL, 6, 6)
        runAtomicTcTestWithRemoteExpectations("diablo", MonsterType.BOSS, Difficulty.NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("diablo", MonsterType.BOSS, Difficulty.NIGHTMARE, 1, 1)
        runAtomicTcTestWithRemoteExpectations("diablo", MonsterType.BOSS, Difficulty.HELL, 6, 6)
        runAtomicTcTestWithRemoteExpectations("diabloq", MonsterType.BOSS, Difficulty.HELL, 6, 6)
        runAtomicTcTestWithRemoteExpectations("baalcrab", MonsterType.BOSS, Difficulty.NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("baalcrab", MonsterType.BOSS, Difficulty.NIGHTMARE, 1, 1)
        runAtomicTcTestWithRemoteExpectations("baalcrab", MonsterType.BOSS, Difficulty.HELL, 6, 6)
        runAtomicTcTestWithRemoteExpectations("baalcrabq", MonsterType.BOSS, Difficulty.HELL, 6, 6)
    }

    @Test
    fun bossTest() {
        runAtomicTcTestWithRemoteExpectations("radament", MonsterType.BOSS, Difficulty.NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("radament", MonsterType.BOSS, Difficulty.NIGHTMARE, 1, 1)
        runAtomicTcTestWithRemoteExpectations("radament", MonsterType.BOSS, Difficulty.NIGHTMARE, 6, 6)
        runAtomicTcTestWithRemoteExpectations("radament", MonsterType.BOSS, Difficulty.HELL, 1, 1)

        runAtomicTcTestWithRemoteExpectations("summoner", MonsterType.BOSS, Difficulty.NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("summoner", MonsterType.BOSS, Difficulty.NIGHTMARE, 1, 1)
        runAtomicTcTestWithRemoteExpectations("summoner", MonsterType.BOSS, Difficulty.HELL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("summoner", MonsterType.BOSS, Difficulty.HELL, 7, 7)

        runAtomicTcTestWithRemoteExpectations("izual", MonsterType.BOSS, Difficulty.NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("izual", MonsterType.BOSS, Difficulty.NIGHTMARE, 1, 1)
        runAtomicTcTestWithRemoteExpectations("izual", MonsterType.BOSS, Difficulty.HELL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("izual", MonsterType.BOSS, Difficulty.HELL, 7, 7)

        runAtomicTcTestWithRemoteExpectations("bloodraven", MonsterType.BOSS, Difficulty.NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("bloodraven", MonsterType.BOSS, Difficulty.NIGHTMARE, 1, 1)
        runAtomicTcTestWithRemoteExpectations("bloodraven", MonsterType.BOSS, Difficulty.HELL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("bloodraven", MonsterType.BOSS, Difficulty.HELL, 7, 7)

        runAtomicTcTestWithRemoteExpectations("nihlathakboss", MonsterType.BOSS, Difficulty.NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("nihlathakboss", MonsterType.BOSS, Difficulty.NIGHTMARE, 1, 1)
        runAtomicTcTestWithRemoteExpectations("nihlathakboss", MonsterType.BOSS, Difficulty.HELL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("nihlathakboss", MonsterType.BOSS, Difficulty.HELL, 7, 7)

        runAtomicTcTestWithRemoteExpectations("andariel", MonsterType.BOSS, Difficulty.NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("andariel", MonsterType.BOSS, Difficulty.NIGHTMARE, 1, 1)
        runAtomicTcTestWithRemoteExpectations("andariel", MonsterType.BOSS, Difficulty.HELL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("andariel", MonsterType.BOSS, Difficulty.HELL, 7, 7)

        runAtomicTcTestWithRemoteExpectations("putriddefiler1", MonsterType.BOSS, Difficulty.NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("putriddefiler2", MonsterType.BOSS, Difficulty.NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("putriddefiler3", MonsterType.BOSS, Difficulty.NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("putriddefiler4", MonsterType.BOSS, Difficulty.NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("putriddefiler5", MonsterType.BOSS, Difficulty.NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("putriddefiler1", MonsterType.BOSS, Difficulty.NIGHTMARE, 5, 3)
        runAtomicTcTestWithRemoteExpectations("putriddefiler2", MonsterType.BOSS, Difficulty.NIGHTMARE, 5, 3)
        runAtomicTcTestWithRemoteExpectations("putriddefiler3", MonsterType.BOSS, Difficulty.NIGHTMARE, 5, 3)
        runAtomicTcTestWithRemoteExpectations("putriddefiler4", MonsterType.BOSS, Difficulty.NIGHTMARE, 5, 3)
        runAtomicTcTestWithRemoteExpectations("putriddefiler5", MonsterType.BOSS, Difficulty.NIGHTMARE, 5, 3)
        runAtomicTcTestWithRemoteExpectations("putriddefiler1", MonsterType.BOSS, Difficulty.HELL, 8, 8)
        runAtomicTcTestWithRemoteExpectations("putriddefiler2", MonsterType.BOSS, Difficulty.HELL, 8, 8)
        runAtomicTcTestWithRemoteExpectations("putriddefiler3", MonsterType.BOSS, Difficulty.HELL, 8, 8)
        runAtomicTcTestWithRemoteExpectations("putriddefiler4", MonsterType.BOSS, Difficulty.HELL, 8, 8)
        runAtomicTcTestWithRemoteExpectations("putriddefiler5", MonsterType.BOSS, Difficulty.HELL, 8, 8)
    }

    @Test
    fun minionTest() {
        runAtomicTcTestWithRemoteExpectations("skeleton1:skeleton1", MonsterType.MINION, Difficulty.NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("skeleton2:skeleton2", MonsterType.MINION, Difficulty.NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("skeleton2:unraveler1", MonsterType.MINION, Difficulty.NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("skeleton2:unraveler1", MonsterType.MINION, Difficulty.HELL, 5, 5)
        runAtomicTcTestWithRemoteExpectations("snowyeti4:Frozenstein", MonsterType.MINION, Difficulty.HELL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("snowyeti4:Frozenstein", MonsterType.MINION, Difficulty.NIGHTMARE, 3, 3)
    }

    @Test
    fun superUniqueTest() {
        runAtomicTcTestWithRemoteExpectations("Bloodwitch the Wild", MonsterType.SUPERUNIQUE, Difficulty.NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("Axe Dweller", MonsterType.SUPERUNIQUE, Difficulty.NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("Toorc Icefist", MonsterType.SUPERUNIQUE, Difficulty.NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("Pindleskin", MonsterType.SUPERUNIQUE, Difficulty.NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("Pindleskin", MonsterType.SUPERUNIQUE, Difficulty.NIGHTMARE, 1, 1)
        runAtomicTcTestWithRemoteExpectations("Pindleskin", MonsterType.SUPERUNIQUE, Difficulty.HELL, 8, 8)
        runAtomicTcTestWithRemoteExpectations("Baal Subject 3", MonsterType.SUPERUNIQUE, Difficulty.NORMAL, 1, 1)
        runAtomicTcTestWithLocalExpectations(
            "Nihlathak Boss",
            MonsterType.SUPERUNIQUE,
            Difficulty.NORMAL,
            1,
            1,
            testDataGenerator.generateTcExpectationDataToFile(
                "nihlathakboss",
                MonsterType.BOSS,
                Difficulty.NORMAL,
                1,
                1
            )
        )
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
        partySize: Int
    ) = runAtomicTcTestWithLocalExpectations(
        monsterId, monsterType, difficulty, nPlayers, partySize, testDataGenerator.generateTcExpectationDataToFile(
            monsterId,
            monsterType,
            difficulty,
            nPlayers,
            partySize
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
