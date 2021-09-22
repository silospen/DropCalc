package com.silospen.dropcalc

import com.silospen.dropcalc.files.LineParser
import com.silospen.dropcalc.files.getResource
import com.silospen.dropcalc.files.readTsv
import com.silospen.dropcalc.resource.ApiResource
import com.silospen.dropcalc.resource.ApiResponse
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

    @Autowired
    private lateinit var testDataGenerator: TestDataGenerator

    @Test
    fun remoteTests() {
    }

    @Test
    fun runLocalTcTests() = runLocalTests("integExpectations/tcTests") { parts, file ->
        runAtomicTcTestWithLocalExpectations(
            parts[0],
            MonsterType.valueOf(parts[1]),
            Difficulty.valueOf(parts[2]),
            parts[3].toInt(),
            parts[4].toInt(),
            file
        )
    }

    @Test
    fun runLocalMonsterTests() =
        runLocalTests("integExpectations/monsterTests") { parts, file ->
            runMonsterTestWithLocalExpectations(
                parts[0],
                MonsterType.valueOf(parts[1]),
                Difficulty.valueOf(parts[2]),
                parts[3].toInt(),
                parts[4].toInt(),
                ItemQuality.valueOf(parts[5]),
                parts[6].toInt(),
                file
            )
        }

    @Test
    fun runLocalItemTests() =
        runLocalTests("integExpectations/itemTests") { parts, file ->
            runItemsTestWithLocalExpectations(
                parts[0],
                MonsterType.valueOf(parts[1]),
                Difficulty.valueOf(parts[2]),
                parts[3].toInt(),
                parts[4].toInt(),
                ItemQuality.valueOf(parts[5]),
                parts[6].toInt(),
                file
            )
        }

    fun runLocalTests(resourceName: String, testToRun: (List<String>, File) -> Unit) {
        getResource(resourceName).listFiles()!!.filter { it.name.endsWith(".tsv") }.forEach {
            val parts = it.name.removeSuffix(".tsv").replace("$", ":").split("_")
            testToRun(parts, it)
        }
    }

    fun runItemsTestWithRemoteExpectations(
        itemId: String,
        monsterType: MonsterType,
        difficulty: Difficulty,
        nPlayers: Int,
        partySize: Int,
        itemQuality: ItemQuality,
        magicFind: Int,
    ) = runItemsTestWithLocalExpectations(
        itemId, monsterType, difficulty, nPlayers, partySize, itemQuality, magicFind,
        testDataGenerator.generateItemExpectationDataToFile(
            itemId,
            monsterType,
            difficulty,
            nPlayers,
            partySize,
            itemQuality,
            magicFind
        )
//            .copyTo(File("C:\\Users\\silos\\Projects\\DropCalc\\src\\test\\resources\\integExpectations\\itemTests\\${itemId}_${monsterType}_${difficulty}_${nPlayers}_${partySize}_${itemQuality}_${magicFind}.tsv"))
    )

    fun runItemsTestWithLocalExpectations(
        itemId: String,
        monsterType: MonsterType,
        difficulty: Difficulty,
        nPlayers: Int,
        partySize: Int,
        itemQuality: ItemQuality,
        magicFind: Int,
        file: File
    ) = runTestWithLocalExpectations(
        file,
        tcExpectationDataLineParser,
        {
            apiResource.getItemProbabilities(
                itemId,
                monsterType,
                itemQuality,
                difficulty,
                nPlayers,
                partySize,
                magicFind
            )
        },
        this::runAtomicTcAsserts,
        "$itemId, $monsterType, $difficulty, $nPlayers, $partySize, $itemQuality, $magicFind",
    )

    fun runMonsterTestWithRemoteExpectations(
        monsterId: String,
        monsterType: MonsterType,
        difficulty: Difficulty,
        nPlayers: Int,
        partySize: Int,
        itemQuality: ItemQuality,
        magicFind: Int,
    ) = runMonsterTestWithLocalExpectations(
        monsterId, monsterType, difficulty, nPlayers, partySize, itemQuality, magicFind,
        testDataGenerator.generateMonsterExpectationDataToFile(
            monsterId,
            monsterType,
            difficulty,
            nPlayers,
            partySize,
            itemQuality,
            magicFind
        )
//            .copyTo(
//                File(
//                    "C:\\Users\\silos\\Projects\\DropCalc\\src\\test\\resources\\integExpectations\\monsterTests\\${
//                        monsterId.replace(
//                            ":",
//                            "$"
//                        )
//                    }_${monsterType}_${difficulty}_${nPlayers}_${partySize}_${itemQuality}_${magicFind}.tsv"
//                )
//            )
    )

    fun runMonsterTestWithLocalExpectations(
        monsterId: String,
        monsterType: MonsterType,
        difficulty: Difficulty,
        nPlayers: Int,
        partySize: Int,
        itemQuality: ItemQuality,
        magicFind: Int,
        file: File
    ) = runTestWithLocalExpectations(
        file,
        tcExpectationDataLineParser,
        { apiResource.getMonster(monsterId, monsterType, difficulty, nPlayers, partySize, itemQuality, magicFind) },
        this::runAtomicTcAsserts,
        "$monsterId, $monsterType, $difficulty, $nPlayers, $partySize, $itemQuality, $magicFind",
    )

    fun runAtomicTcTestWithRemoteExpectations(
        monsterId: String,
        monsterType: MonsterType,
        difficulty: Difficulty,
        nPlayers: Int,
        partySize: Int
    ) = runAtomicTcTestWithLocalExpectations(
        monsterId, monsterType, difficulty, nPlayers, partySize,
        testDataGenerator.generateTcExpectationDataToFile(
            monsterId,
            monsterType,
            difficulty,
            nPlayers,
            partySize
        )
//            .copyTo(
//                File(
//                    "C:\\Users\\silos\\Projects\\DropCalc\\src\\test\\resources\\integExpectations\\tcTests\\${
//                        monsterId.replace(
//                            ":",
//                            "$"
//                        )
//                    }_${monsterType}_${difficulty}_${nPlayers}_${partySize}.tsv"
//                )
//            )
    )

    fun runAtomicTcTestWithLocalExpectations(
        monsterId: String,
        monsterType: MonsterType,
        difficulty: Difficulty,
        nPlayers: Int,
        partySize: Int,
        file: File
    ) = runTestWithLocalExpectations(
        file,
        tcExpectationDataLineParser,
        { apiResource.getAtomicTcs(monsterId, monsterType, difficulty, nPlayers, partySize) },
        this::runAtomicTcAsserts,
        "$monsterId, $monsterType, $difficulty, $nPlayers, $partySize",
    )

    fun <T> runTestWithLocalExpectations(
        expectationsFile: File,
        expectationsLineParser: LineParser<T?>,
        actualSource: () -> List<T>,
        assertionsRunner: (List<T>, List<T>, String) -> Unit,
        inputsForLogging: String
    ) {
        val actual = actualSource()
        val expected = readTsv(
            expectationsFile,
            expectationsLineParser
        )
        assertTrue(expected.isNotEmpty(), "Expected is empty")
        assertTrue(actual.isNotEmpty(), "Actual is empty")
        assertionsRunner(actual, expected, inputsForLogging)
    }

    private fun runAtomicTcAsserts(
        actual: List<ApiResponse>,
        expected: List<ApiResponse>,
        testInput: String
    ) {
        val actualsByTcAndArea: Map<Pair<String, String>, List<ApiResponse>> =
            actual.groupBy { it.name.lowercase() to it.area }
        val outcomes = expected
            .filterNot { brokenNames.contains(it.name) }
            .filterNot { brokenNamesAndAreas.contains(it.name to it.area) }
            .map {
                val key = it.name.lowercase().replace(" - ", " ") to it.area
                val value = actualsByTcAndArea.getValue(key)
                if (value.size != 1) throw RuntimeException("Multiple probs found for $it and $value")
                val actualProb = value[0].prob
                val expectedProb = it.prob
                key to Precision.equals(actualProb.dec(), expectedProb.dec(), 0.00000000001)
            }
        val failedAsserts = outcomes.filter { !it.second }
        if (failedAsserts.isNotEmpty()) {
            println("Failed: $testInput on ${failedAsserts.joinToString("\n")}")
            fail()
        } else {
            println("Succeeded: $testInput")
        }
    }

    private val tcExpectationDataLineParser = object : LineParser<ApiResponse?> {
        override fun parseLine(line: List<String>): ApiResponse =
            ApiResponse(line[0], line[1], line[2].toDouble())
    }

    private val brokenNames = setOf(
        "cm1",
        "cm2",
        "cm3",
        "jew",
        "rin",
        "amu",
        "pk2",
        "pk3",
        "Choking Gas Potion",
        "Exploding Potion",
        "Oil Potion",
        "Rancid Gas Potion",
        "Strangling Gas Potion",
        "Amulet",
        "Grand Charm",
        "Jewel",
        "Large Charm",
        "Ring",
        "Small Charm",
        "Fulminating Potion",
        "Rainbow Facet"
    )

    private val brokenNamesAndAreas = setOf(
        "Oblivion Knight (Lord De Seis) doomknight3 (H)" to "The Chaos Sanctuary",
        "Overlord (Sharptooth Slayer) overseer3 (H)" to "Frigid Highlands",
        "Soul Killer Shaman (Witch Doctor Endugu) fetishshaman4 (H)" to "Flayer Dungeon Level 3",
        "Unraveler (Ancient Kaa the Soulless) unraveler3 (H)" to "Tal Rasha's Tomb",
        "Warped Shaman (Colenzo the Annihilator) fallenshaman5 (H)" to "Throne of Destruction",
    )
}
