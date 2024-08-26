package com.silospen.dropcalc

import com.silospen.dropcalc.DropCalcIntegTest.Mode.RUN_ASSERTIONS
import com.silospen.dropcalc.DropCalcIntegTest.Mode.WRITE_EXPECTATIONS
import com.silospen.dropcalc.files.Line
import com.silospen.dropcalc.files.LineParser
import com.silospen.dropcalc.files.readTsv
import com.silospen.dropcalc.resource.ApiResource
import com.silospen.dropcalc.resource.ApiResponse
import org.apache.commons.math3.util.Precision
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

@SpringBootTest
class DropCalcIntegTest {
    @Autowired
    private lateinit var apiResource: ApiResource

    private val tcTestsFolder = File("src/test/resources/integExpectations/tcTests")
    private val monsterTestsFolder = File("src/test/resources/integExpectations/monsterTests")
    private val itemTestsFolder = File("src/test/resources/integExpectations/itemTests")

    @Test
    fun runLocalTcTests() = runLocalTcTests(RUN_ASSERTIONS)

    enum class Mode {
        RUN_ASSERTIONS,
        WRITE_EXPECTATIONS
    }

    fun runLocalTcTests(mode: Mode) = runLocalTests(tcTestsFolder) { parts, file ->
        runAtomicTcTestWithLocalExpectations(
            parts[0],
            MonsterType.valueOf(parts[1]),
            Difficulty.valueOf(parts[2]),
            parts[3].toInt(),
            parts[4].toInt(),
            file,
            mode,
            if (parts.size == 5) Version.V1_12 else Version.valueOf(parts[5].replace(" ", "_"))
        )
    }

    @Test
    fun runLocalMonsterTests() = runLocalMonsterTests(RUN_ASSERTIONS)

    fun runLocalMonsterTests(mode: Mode) =
        runLocalTests(monsterTestsFolder) { parts, file ->
            runMonsterTestWithLocalExpectations(
                parts[0],
                MonsterType.valueOf(parts[1]),
                Difficulty.valueOf(parts[2]),
                parts[3].toInt(),
                parts[4].toInt(),
                ItemQuality.valueOf(parts[5]),
                parts[6].toInt(),
                file,
                mode
            )
        }

    @Test
    fun runLocalItemTests() = runLocalItemTests(RUN_ASSERTIONS)

    fun runLocalItemTests(mode: Mode) =
        runLocalTests(itemTestsFolder) { parts, file ->
            runItemsTestWithLocalExpectations(
                parts[0],
                MonsterType.valueOf(parts[1]),
                if (parts[2] == "null") null else Difficulty.valueOf(parts[2]),
                parts[3].toInt(),
                parts[4].toInt(),
                ItemQuality.valueOf(parts[5]),
                parts[6].toInt(),
                file,
                mode
            )
        }

    @Test
    @Disabled
    fun generateExpectationData() {
        runLocalTcTests(WRITE_EXPECTATIONS)
        runLocalMonsterTests(WRITE_EXPECTATIONS)
        runLocalItemTests(WRITE_EXPECTATIONS)
    }

    fun runLocalTests(folder: File, testToRun: (List<String>, File) -> Unit) {
        folder.listFiles()!!.filter { it.name.endsWith(".tsv") }.forEach {
            val parts = it.name.removeSuffix(".tsv").replace("$", ":").split("_")
            testToRun(parts, it)
        }
    }

    fun runItemsTestWithLocalExpectations(
        itemId: String,
        monsterType: MonsterType,
        difficulty: Difficulty?,
        nPlayers: Int,
        partySize: Int,
        itemQuality: ItemQuality,
        magicFind: Int,
        file: File,
        mode: Mode,
    ) = runTestWithLocalExpectations(
        file,
        tcExpectationDataLineParser,
        {
            apiResource.getItemProbabilities(
                Version.V1_12,
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
        mode,
    )

    fun runMonsterTestWithLocalExpectations(
        monsterId: String,
        monsterType: MonsterType,
        difficulty: Difficulty,
        nPlayers: Int,
        partySize: Int,
        itemQuality: ItemQuality,
        magicFind: Int,
        file: File,
        mode: Mode,
    ) = runTestWithLocalExpectations(
        file,
        tcExpectationDataLineParser,
        {
            apiResource.getMonster(
                Version.V1_12,
                monsterId,
                monsterType,
                difficulty,
                nPlayers,
                partySize,
                itemQuality,
                magicFind
            )
        },
        this::runAtomicTcAsserts,
        "$monsterId, $monsterType, $difficulty, $nPlayers, $partySize, $itemQuality, $magicFind",
        mode,
    )

    fun runAtomicTcTestWithLocalExpectations(
        monsterId: String,
        monsterType: MonsterType,
        difficulty: Difficulty,
        nPlayers: Int,
        partySize: Int,
        file: File,
        mode: Mode,
        version: Version = Version.V1_12
    ) = runTestWithLocalExpectations(
        file,
        tcExpectationDataLineParser,
        { apiResource.getAtomicTcs(version, monsterId, monsterType, difficulty, nPlayers, partySize) },
        this::runAtomicTcAsserts,
        "$monsterId, $monsterType, $difficulty, $nPlayers, $partySize",
        mode
    )

    fun runTestWithLocalExpectations(
        expectationsFile: File,
        expectationsLineParser: LineParser<ApiResponse?>,
        actualSource: () -> List<ApiResponse>,
        assertionsRunner: (List<ApiResponse>, List<ApiResponse>, String) -> Unit,
        inputsForLogging: String,
        mode: Mode,
    ) {
        val actual = actualSource()
        val expected = readTsv(
            FileInputStream(expectationsFile),
            expectationsLineParser
        )
        assertTrue(expected.isNotEmpty(), "Expected is empty")
        assertTrue(actual.isNotEmpty(), "Actual is empty")

        when (mode) {
            RUN_ASSERTIONS -> assertionsRunner(actual, expected, inputsForLogging)
            WRITE_EXPECTATIONS -> writeExpectationsTsv(FileOutputStream(expectationsFile), actual)
        }


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
                val key = it.name.lowercase() to it.area
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
        override fun parseLine(line: Line): ApiResponse =
            ApiResponse(line[0], line[1], line[2].toDouble())
    }

    private fun writeExpectationsTsv(outputStream: FileOutputStream, expectations: List<ApiResponse>) {
        outputStream.bufferedWriter().use { writer ->
            expectations.forEach { writer.appendLine("${it.name}\t${it.area}\t${it.prob}") }
        }
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
        "Oblivion Knight (Lord De Seis) - doomknight3 (H)" to "The Chaos Sanctuary",
        "Overlord (Sharptooth Slayer) - overseer3 (H)" to "Frigid Highlands",
        "Soul Killer Shaman (Witch Doctor Endugu) - fetishshaman4 (H)" to "Flayer Dungeon Level 3",
        "Unraveler (Ancient Kaa the Soulless) - unraveler3 (H)" to "Tal Rasha's Tomb",
        "Warped Shaman (Colenzo the Annihilator) - fallenshaman5 (H)" to "Throne of Destruction",
    )
}

