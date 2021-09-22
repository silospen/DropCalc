package com.silospen.dropcalc

import com.silospen.dropcalc.Difficulty.*
import com.silospen.dropcalc.ItemQuality.*
import com.silospen.dropcalc.ItemQuality.UNIQUE
import com.silospen.dropcalc.MonsterType.*
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
    fun test() {
        runAtomicTcTestWithRemoteExpectations("snowyeti2", REGULAR, NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("snowyeti2", REGULAR, NORMAL, 3, 3)
        runAtomicTcTestWithRemoteExpectations("snowyeti2", REGULAR, NIGHTMARE, 3, 3)
        runAtomicTcTestWithRemoteExpectations("snowyeti2", REGULAR, HELL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("snowyeti2", REGULAR, HELL, 3, 3)
        runAtomicTcTestWithRemoteExpectations("snowyeti2", CHAMPION, NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("snowyeti2", CHAMPION, HELL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("snowyeti2", CHAMPION, HELL, 8, 8)
        runAtomicTcTestWithRemoteExpectations("snowyeti2", MonsterType.UNIQUE, HELL, 8, 8)
    }

    @Test
    fun primeEvilTest() {
        runAtomicTcTestWithRemoteExpectations("mephisto", BOSS, NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("mephisto", BOSS, NIGHTMARE, 1, 1)
        runAtomicTcTestWithRemoteExpectations("mephisto", BOSS, HELL, 6, 6)
        runAtomicTcTestWithRemoteExpectations("mephistoq", BOSS, HELL, 6, 6)
        runAtomicTcTestWithRemoteExpectations("diablo", BOSS, NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("diablo", BOSS, NIGHTMARE, 1, 1)
        runAtomicTcTestWithRemoteExpectations("diablo", BOSS, HELL, 6, 6)
        runAtomicTcTestWithRemoteExpectations("diabloq", BOSS, HELL, 6, 6)
        runAtomicTcTestWithRemoteExpectations("baalcrab", BOSS, NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("baalcrab", BOSS, NIGHTMARE, 1, 1)
        runAtomicTcTestWithRemoteExpectations("baalcrab", BOSS, HELL, 6, 6)
        runAtomicTcTestWithRemoteExpectations("baalcrabq", BOSS, HELL, 6, 6)
    }

    @Test
    fun bossTest() {
        runAtomicTcTestWithRemoteExpectations("radament", BOSS, NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("radament", BOSS, NIGHTMARE, 1, 1)
        runAtomicTcTestWithRemoteExpectations("radament", BOSS, NIGHTMARE, 6, 6)
        runAtomicTcTestWithRemoteExpectations("radament", BOSS, HELL, 1, 1)

        runAtomicTcTestWithRemoteExpectations("summoner", BOSS, NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("summoner", BOSS, NIGHTMARE, 1, 1)
        runAtomicTcTestWithRemoteExpectations("summoner", BOSS, HELL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("summoner", BOSS, HELL, 7, 7)

        runAtomicTcTestWithRemoteExpectations("izual", BOSS, NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("izual", BOSS, NIGHTMARE, 1, 1)
        runAtomicTcTestWithRemoteExpectations("izual", BOSS, HELL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("izual", BOSS, HELL, 7, 7)

        runAtomicTcTestWithRemoteExpectations("bloodraven", BOSS, NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("bloodraven", BOSS, NIGHTMARE, 1, 1)
        runAtomicTcTestWithRemoteExpectations("bloodraven", BOSS, HELL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("bloodraven", BOSS, HELL, 7, 7)

        runAtomicTcTestWithRemoteExpectations("nihlathakboss", BOSS, NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("nihlathakboss", BOSS, NIGHTMARE, 1, 1)
        runAtomicTcTestWithRemoteExpectations("nihlathakboss", BOSS, HELL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("nihlathakboss", BOSS, HELL, 7, 7)

        runAtomicTcTestWithRemoteExpectations("andariel", BOSS, NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("andariel", BOSS, NIGHTMARE, 1, 1)
        runAtomicTcTestWithRemoteExpectations("andariel", BOSS, HELL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("andariel", BOSS, HELL, 7, 7)

        runAtomicTcTestWithRemoteExpectations("putriddefiler1", BOSS, NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("putriddefiler2", BOSS, NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("putriddefiler3", BOSS, NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("putriddefiler4", BOSS, NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("putriddefiler5", BOSS, NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("putriddefiler1", BOSS, NIGHTMARE, 5, 3)
        runAtomicTcTestWithRemoteExpectations("putriddefiler2", BOSS, NIGHTMARE, 5, 3)
        runAtomicTcTestWithRemoteExpectations("putriddefiler3", BOSS, NIGHTMARE, 5, 3)
        runAtomicTcTestWithRemoteExpectations("putriddefiler4", BOSS, NIGHTMARE, 5, 3)
        runAtomicTcTestWithRemoteExpectations("putriddefiler5", BOSS, NIGHTMARE, 5, 3)
        runAtomicTcTestWithRemoteExpectations("putriddefiler1", BOSS, HELL, 8, 8)
        runAtomicTcTestWithRemoteExpectations("putriddefiler2", BOSS, HELL, 8, 8)
        runAtomicTcTestWithRemoteExpectations("putriddefiler3", BOSS, HELL, 8, 8)
        runAtomicTcTestWithRemoteExpectations("putriddefiler4", BOSS, HELL, 8, 8)
        runAtomicTcTestWithRemoteExpectations("putriddefiler5", BOSS, HELL, 8, 8)
    }

    @Test
    fun minionTest() {
        runAtomicTcTestWithRemoteExpectations("skeleton4:Radament", MINION, HELL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("skeleton1:skeleton1", MINION, NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("skeleton2:skeleton2", MINION, NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("skeleton2:unraveler1", MINION, NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("skeleton2:unraveler1", MINION, HELL, 5, 5)
        runAtomicTcTestWithRemoteExpectations("snowyeti4:Frozenstein", MINION, HELL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("snowyeti4:Frozenstein", MINION, NIGHTMARE, 3, 3)
    }

    @Test
    fun superUniqueTest() {
        runAtomicTcTestWithRemoteExpectations("Bloodwitch the Wild", SUPERUNIQUE, NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("Axe Dweller", SUPERUNIQUE, NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("Toorc Icefist", SUPERUNIQUE, NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("Pindleskin", SUPERUNIQUE, NORMAL, 1, 1)
        runAtomicTcTestWithRemoteExpectations("Pindleskin", SUPERUNIQUE, NIGHTMARE, 1, 1)
        runAtomicTcTestWithRemoteExpectations("Pindleskin", SUPERUNIQUE, HELL, 8, 8)
        runAtomicTcTestWithRemoteExpectations("Baal Subject 3", SUPERUNIQUE, NORMAL, 1, 1)
        runAtomicTcTestWithLocalExpectations(
            "Nihlathak Boss",
            SUPERUNIQUE,
            NORMAL,
            1,
            1,
            testDataGenerator.generateTcExpectationDataToFile(
                "nihlathakboss",
                BOSS,
                NORMAL,
                1,
                1
            )
        )
    }

    @Test
    fun monstersTest() {
        runMonsterTestWithRemoteExpectations("andariel", BOSS, NORMAL, 1, 1, UNIQUE, 0)
        runMonsterTestWithRemoteExpectations("andariel", BOSS, NORMAL, 1, 1, UNIQUE, 500)
        runMonsterTestWithRemoteExpectations("snowyeti2", REGULAR, NORMAL, 1, 1, WHITE, 0)
        runMonsterTestWithRemoteExpectations("baalcrab", BOSS, NORMAL, 1, 1, WHITE, 0)
        runMonsterTestWithRemoteExpectations("baalcrab", BOSS, NORMAL, 1, 1, WHITE, 100)
        runMonsterTestWithRemoteExpectations("andariel", BOSS, NORMAL, 1, 1, WHITE, 0)
        runMonsterTestWithRemoteExpectations("snowyeti2", CHAMPION, NORMAL, 1, 1, WHITE, 0)
        runMonsterTestWithRemoteExpectations("snowyeti2", REGULAR, HELL, 6, 6, WHITE, 0)
        runMonsterTestWithRemoteExpectations("snowyeti2", REGULAR, NORMAL, 1, 1, SET, 0)
        runMonsterTestWithRemoteExpectations("snowyeti2", REGULAR, NORMAL, 1, 1, SET, 600)
    }

//    testDataGenerator.generateMonsterExpectationDataToFile("zombie1", REGULAR, NORMAL, 1, 1, RARE)
//    .copyTo(File("C:\\Users\\silos\\Projects\\DropCalc\\src\\test\\resources\\integExpectations\\monsterTests\\zombie1_REGULAR_NORMAL_1_1_RARE_0.tsv"))

    @Test
    fun itemsTest() {
//        runItemsTestWithRemoteExpectations("The Stone of Jordan", REGULAR, HELL, 1, 1, UNIQUE, 0)
//        runItemsTestWithRemoteExpectations("The Stone of Jordan", BOSS, HELL, 1, 1, UNIQUE, 0)
        runItemsTestWithRemoteExpectations("scm", REGULAR, NORMAL, 1, 1, WHITE, 0)
        runItemsTestWithRemoteExpectations("scm", REGULAR, NORMAL, 1, 1, RARE, 0)
        runItemsTestWithRemoteExpectations("scm", REGULAR, NORMAL, 1, 1, RARE, 500)
        runItemsTestWithRemoteExpectations("scm", REGULAR, NORMAL, 1, 1, MAGIC, 500)
        runItemsTestWithRemoteExpectations("utg", MINION, HELL, 1, 1, WHITE, 0)
        runItemsTestWithRemoteExpectations("utg", MINION, HELL, 1, 1, RARE, 0)
        runItemsTestWithRemoteExpectations("utg", MINION, HELL, 1, 1, RARE, 500)
        runItemsTestWithRemoteExpectations("utg", MINION, HELL, 1, 1, MAGIC, 500)
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

    @Test
    fun runLocalMonsterTests() =
        getResource("integExpectations/monsterTests").listFiles()!!.filter { it.name.endsWith(".tsv") }.forEach {
            val parts = it.name.removeSuffix(".tsv").split("_")
            runMonsterTestWithLocalExpectations(
                parts[0],
                MonsterType.valueOf(parts[1]),
                Difficulty.valueOf(parts[2]),
                parts[3].toInt(),
                parts[4].toInt(),
                ItemQuality.valueOf(parts[5]),
                parts[6].toInt(),
                it
            )
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
        //TODO: CM, JEW, RIN don't appear to have changes in drop rate due to player changes? Is this correct?
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
