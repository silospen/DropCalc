package com.silospen.dropcalc

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Charsets
import com.google.common.hash.Hashing
import com.silospen.dropcalc.resource.ApiResource
import com.silospen.dropcalc.resource.ApiResponseEntry
import com.silospen.dropcalc.resource.VersionedMetadataResource
import org.apache.commons.math3.util.Precision
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.File
import java.util.concurrent.Callable

@SpringBootTest
class DropCalcLargeIntegTests {
    @Autowired
    private lateinit var jacksonObjectMapper: ObjectMapper

    private val largeIntegTestRunner by lazy {
        LargeIntegTestRunner(jacksonObjectMapper)
    }

    @Autowired
    private lateinit var apiResource: ApiResource

    @Autowired
    private lateinit var versionedMetadataResources: Map<Version, VersionedMetadataResource>

    private val bigTcTestFile = File("src/test/resources/integExpectations/bigTcTests")
    private val bigMonstersTestFile = File("src/test/resources/integExpectations/bigMonstersTests")
    private val bigItemsTestFile = File("src/test/resources/integExpectations/bigItemsTests")

    @Test
    fun runItemsTest() {
        largeIntegTestRunner.runTests(bigItemsTestFile, { expected ->
            getItemExpectation(
                expected.version,
                expected.itemId,
                expected.apiItemQuality,
                expected.monsterType,
                expected.difficulty,
                expected.numPlayers,
                expected.partySize,
                expected.magicFind
            )
        }, object : TypeReference<List<ItemsTestDataExpectation>>() {})
    }

    @Test
    fun runMonstersTest() {
        largeIntegTestRunner.runTests(bigMonstersTestFile, { expected ->
            getMonstersExpectation(
                expected.version,
                expected.monsterId,
                expected.monsterType,
                expected.difficulty,
                expected.numPlayers,
                expected.partySize,
                expected.apiItemQuality,
                expected.magicFind
            )
        }, object : TypeReference<List<MonstersTestDataExpectation>>() {})
    }

    @Test
    fun runAtomicTcsTest() {
        largeIntegTestRunner.runTests(bigTcTestFile, { expected ->
            getAtomicTcs(
                expected.version,
                expected.monsterId,
                expected.monsterType,
                expected.difficulty,
                expected.numPlayers,
                expected.partySize
            )
        }, object : TypeReference<List<AtomicTcsTestDataExpectation>>() {})
    }

    @Test
    @Disabled
    fun generateItemsTestData() {
        largeIntegTestRunner.generateTestData(bigItemsTestFile, ::generateItemsTestDataInputs)
    }

    @Test
    @Disabled
    fun generateMonstersTestData() {
        largeIntegTestRunner.generateTestData(bigMonstersTestFile, ::generateMonstersTestDataInputs)
    }

    @Test
    @Disabled
    fun generateAtomicTcsTestData() {
        largeIntegTestRunner.generateTestData(bigTcTestFile, ::generateAtomicTcsTestDataInputs)
    }

    fun generateAtomicTcsTestDataInputs(counter: Counter): List<Callable<AtomicTcsTestDataExpectation>> {
        val result = mutableListOf<Callable<AtomicTcsTestDataExpectation>>()
        for (version in Version.values()) {
            val monsterLibrary = versionedMetadataResources.getValue(version).monsterLibrary
            for (monsterType in MonsterType.values()) {
                for (monsterId in monsterLibrary.getMonsters(monsterType, false, 0).asSequence().map { it.id }
                    .distinct()) {
                    for (difficulty in Difficulty.values()) {
                        for (nPlayers in listOf(3, 7)) {
                            for (nGroup in listOf(5, 8)) {
                                result.add(
                                    Callable {
                                        counter.incrementAndPossiblyPrint()
                                        getAtomicTcs(
                                            version,
                                            monsterId,
                                            monsterType,
                                            difficulty,
                                            nPlayers,
                                            nGroup
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
        return result
    }

    fun generateMonstersTestDataInputs(counter: Counter): List<Callable<MonstersTestDataExpectation>> {
        val result = mutableListOf<Callable<MonstersTestDataExpectation>>()
        for (version in Version.values()) {
            val monsterLibrary = versionedMetadataResources.getValue(version).monsterLibrary
            for (monsterType in MonsterType.values()) {
                for (monsterId in monsterLibrary.getMonsters(monsterType, false, 0).asSequence().map { it.id }
                    .distinct()) {
                    for (difficulty in Difficulty.values()) {
                        for (nPlayers in listOf(7)) {
                            for (nGroup in listOf(5)) {
                                for (apiItemQuality in ApiItemQuality.values()) {
                                    for (magicFind in listOf(0, 975)) {
                                        result.add(
                                            Callable {
                                                counter.incrementAndPossiblyPrint()
                                                getMonstersExpectation(
                                                    version,
                                                    monsterId,
                                                    monsterType,
                                                    difficulty,
                                                    nPlayers,
                                                    nGroup,
                                                    apiItemQuality,
                                                    magicFind
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return result
    }

    fun generateItemsTestDataInputs(counter: Counter): List<Callable<ItemsTestDataExpectation>> {
        val result = mutableListOf<Callable<ItemsTestDataExpectation>>()
        for (version in Version.values()) {
            val itemLibrary = versionedMetadataResources.getValue(version).itemLibrary
            for (monsterType in MonsterType.values()) {
                for (apiItemQuality in ApiItemQuality.values()) {
                    for (itemId in itemLibrary.items.asSequence().filter { it.quality == apiItemQuality.itemQuality }
                        .filter { apiItemQuality.additionalFilter(it) }.map { it.id }
                        .distinct()) {
                        for (difficulty in listOf<Difficulty?>(null) + Difficulty.values()) {
                            for (nPlayers in listOf(7)) {
                                for (nGroup in listOf(5)) {
                                    for (magicFind in listOf(0, 975)) {
                                        result.add(
                                            Callable {
                                                counter.incrementAndPossiblyPrint()
                                                getItemExpectation(
                                                    version,
                                                    itemId,
                                                    apiItemQuality,
                                                    monsterType,
                                                    difficulty,
                                                    nPlayers,
                                                    nGroup,
                                                    magicFind
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return result
    }

    private fun getItemExpectation(
        version: Version,
        itemId: String,
        apiItemQuality: ApiItemQuality,
        monsterType: MonsterType,
        difficulty: Difficulty?,
        numPlayers: Int,
        partySize: Int,
        magicFind: Int
    ) = ItemsTestDataExpectation(
        version,
        itemId,
        apiItemQuality,
        monsterType,
        difficulty,
        numPlayers,
        partySize,
        magicFind,
        generateHash(
            apiResource.getItemProbabilities(
                version,
                itemId,
                monsterType,
                apiItemQuality,
                difficulty,
                numPlayers,
                partySize,
                magicFind
            )
        )
    )

    private fun getMonstersExpectation(
        version: Version,
        monsterId: String,
        monsterType: MonsterType,
        difficulty: Difficulty,
        numPlayers: Int,
        partySize: Int,
        apiItemQuality: ApiItemQuality,
        magicFind: Int
    ) = MonstersTestDataExpectation(
        version,
        monsterId,
        monsterType,
        difficulty,
        numPlayers,
        partySize,
        apiItemQuality,
        magicFind,
        generateHash(
            apiResource.getMonster(
                version,
                monsterId,
                monsterType,
                difficulty,
                numPlayers,
                partySize,
                apiItemQuality,
                magicFind
            )
        )
    )

    private fun getAtomicTcs(
        version: Version,
        monsterId: String,
        monsterType: MonsterType,
        difficulty: Difficulty,
        numPlayers: Int,
        partySize: Int
    ) = AtomicTcsTestDataExpectation(
        version,
        monsterId,
        monsterType,
        difficulty,
        numPlayers,
        partySize,
        generateHash(
            apiResource.getAtomicTcs(
                version,
                monsterId,
                monsterType,
                difficulty,
                numPlayers,
                partySize
            )
        )
    )

    private fun generateHash(apiResponseEntries: List<ApiResponseEntry>) = Hashing.sha256().hashString(
        apiResponseEntries
            .asSequence()
            .map { it.copy(prob = Precision.round(it.prob, 11)) }
            .sortedWith(compareBy({ it.name }, { it.area }))
            .toList()
            .toString(),
        Charsets.UTF_8
    ).toString()

    data class AtomicTcsTestDataExpectation(
        val version: Version,
        val monsterId: String,
        val monsterType: MonsterType,
        val difficulty: Difficulty,
        val numPlayers: Int,
        val partySize: Int,
        val hash: String
    )

    data class MonstersTestDataExpectation(
        val version: Version,
        val monsterId: String,
        val monsterType: MonsterType,
        val difficulty: Difficulty,
        val numPlayers: Int,
        val partySize: Int,
        val apiItemQuality: ApiItemQuality,
        val magicFind: Int,
        val hash: String
    )

    data class ItemsTestDataExpectation(
        val version: Version,
        val itemId: String,
        val apiItemQuality: ApiItemQuality,
        val monsterType: MonsterType,
        val difficulty: Difficulty?,
        val numPlayers: Int,
        val partySize: Int,
        val magicFind: Int,
        val hash: String
    )
}