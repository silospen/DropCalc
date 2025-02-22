package com.silospen.dropcalc

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Charsets
import com.google.common.hash.Hashing
import com.silospen.dropcalc.LegacyTabularApiResponse.Companion.fromTabularApiResponse
import com.silospen.dropcalc.resource.ApiResource
import com.silospen.dropcalc.resource.ApiResponseContext
import com.silospen.dropcalc.resource.TabularApiResponse
import com.silospen.dropcalc.resource.VersionedMetadataResource
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.File
import java.util.concurrent.Callable

@SpringBootTest
class DropCalcLargeDesecratedIntegTests {
    @Autowired
    private lateinit var jacksonObjectMapper: ObjectMapper

    private val largeIntegTestRunner by lazy {
        LargeIntegTestRunner(jacksonObjectMapper)
    }

    @Autowired
    private lateinit var apiResource: ApiResource

    @Autowired
    private lateinit var versionedMetadataResources: Map<Version, VersionedMetadataResource>

    private val bigDesecratedTcTestFile = File("src/test/resources/integExpectations/bigDesecratedTcTests")
    private val bigDesecratedMonstersTestFile = File("src/test/resources/integExpectations/bigDesecratedMonstersTests")
    private val bigDesecratedItemsTestFile = File("src/test/resources/integExpectations/bigDesecratedItemsTests")

    @Test
    fun runDesecratedItemsTest() {
        largeIntegTestRunner.runTests(bigDesecratedItemsTestFile, { expected ->
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
        }, object : TypeReference<List<DesecratedItemsTestDataExpectation>>() {})
    }

    @Test
    fun runDesecratedMonstersTest() {
        largeIntegTestRunner.runTests(bigDesecratedMonstersTestFile, { expected ->
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
        }, object : TypeReference<List<DesecratedMonstersTestDataExpectation>>() {})
    }

    @Test
    fun runDesecratedAtomicTcsTest() {
        largeIntegTestRunner.runTests(bigDesecratedTcTestFile, { expected ->
            getAtomicTcs(
                expected.version,
                expected.monsterId,
                expected.monsterType,
                expected.difficulty,
                expected.numPlayers,
                expected.partySize,
                expected.desecratedLevel,
            )
        }, object : TypeReference<List<DesecratedAtomicTcsTestDataExpectation>>() {})
    }

    @Test
    @Disabled
    fun generateDesecratedItemsTestData() {
        largeIntegTestRunner.generateTestData(bigDesecratedItemsTestFile, ::generateDesecratedItemsTestDataInputs)
    }

    @Test
    @Disabled
    fun generateDesecratedMonstersTestData() {
        largeIntegTestRunner.generateTestData(bigDesecratedMonstersTestFile, ::generateDesecratedMonstersTestDataInputs)
    }

    @Test
    @Disabled
    fun generateDesecratedAtomicTcsTestData() {
        largeIntegTestRunner.generateTestData(bigDesecratedTcTestFile, ::generateDesecratedAtomicTcsTestDataInputs)
    }

    fun generateDesecratedAtomicTcsTestDataInputs(counter: Counter): List<Callable<DesecratedAtomicTcsTestDataExpectation>> {
        val result = mutableListOf<Callable<DesecratedAtomicTcsTestDataExpectation>>()
        for (version in Version.values()) {
            val monsterLibrary = versionedMetadataResources.getValue(version).monsterLibrary
            for (monsterType in MonsterType.values()) {
                for (monster in monsterLibrary.getMonsters(true, 0, monsterType = monsterType)) {
                    for ((nPlayers, nGroup) in listOf(3 to 5, 3 to 8, 7 to 8)) {
                        for (desecratedLevel in listOf(0, 45, 99)) {
                            result.add(
                                Callable {
                                    counter.incrementAndPossiblyPrint()
                                    getAtomicTcs(
                                        version,
                                        monster.id,
                                        monsterType,
                                        monster.difficulty,
                                        nPlayers,
                                        nGroup,
                                        desecratedLevel
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
        return result
    }

    fun generateDesecratedMonstersTestDataInputs(counter: Counter): List<Callable<DesecratedMonstersTestDataExpectation>> {
        val result = mutableListOf<Callable<DesecratedMonstersTestDataExpectation>>()
        for (version in Version.values()) {
            val monsterLibrary = versionedMetadataResources.getValue(version).monsterLibrary
            for (monsterType in MonsterType.values()) {
                for (monster in monsterLibrary.getMonsters(true, 0, monsterType = monsterType)) {
                    for ((nPlayers, nGroup) in listOf(7 to 5)) {
                        for (apiItemQuality in ApiItemQuality.values()) {
                            for (magicFind in listOf(0, 975)) {
                                result.add(
                                    Callable {
                                        counter.incrementAndPossiblyPrint()
                                        getMonstersExpectation(
                                            version,
                                            monster.id,
                                            monsterType,
                                            monster.difficulty,
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
        return result
    }

    fun generateDesecratedItemsTestDataInputs(counter: Counter): List<Callable<DesecratedItemsTestDataExpectation>> {
        val result = mutableListOf<Callable<DesecratedItemsTestDataExpectation>>()
        for (version in Version.values()) {
            val itemLibrary = versionedMetadataResources.getValue(version).itemLibrary
            for (monsterType in MonsterType.values()) {
                for (apiItemQuality in ApiItemQuality.values()) {
                    for (itemId in itemLibrary.items.asSequence().filter { it.quality == apiItemQuality.itemQuality }
                        .filter { apiItemQuality.additionalFilter(it) }.map { it.id }
                        .distinct()) {
                        for (difficulty in listOf<Difficulty?>(null) + Difficulty.values()) {
                            for ((nPlayers, nGroup) in listOf(7 to 5)) {
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
    ) = DesecratedItemsTestDataExpectation(
        version,
        itemId,
        apiItemQuality,
        monsterType,
        difficulty,
        numPlayers,
        partySize,
        magicFind,
        generateHash(
            apiResource.getTabularItemProbabilities(
                version,
                itemId,
                monsterType,
                apiItemQuality,
                difficulty,
                numPlayers,
                partySize,
                magicFind,
                false,
                true,
                0,
                true
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
    ) = DesecratedMonstersTestDataExpectation(
        version,
        monsterId,
        monsterType,
        difficulty,
        numPlayers,
        partySize,
        apiItemQuality,
        magicFind,
        generateHash(
            apiResource.getTabularMonster(
                version,
                monsterId,
                monsterType,
                difficulty,
                numPlayers,
                partySize,
                apiItemQuality,
                magicFind,
                false,
                true,
                0
            )
        )
    )

    private fun getAtomicTcs(
        version: Version,
        monsterId: String,
        monsterType: MonsterType,
        difficulty: Difficulty,
        numPlayers: Int,
        partySize: Int,
        desecratedLevel: Int
    ) = DesecratedAtomicTcsTestDataExpectation(
        version,
        monsterId,
        monsterType,
        difficulty,
        numPlayers,
        partySize,
        desecratedLevel,
        generateHash(
            apiResource.getTabularAtomicTcs(
                version,
                monsterId,
                monsterType,
                difficulty,
                numPlayers,
                partySize,
                false,
                true,
                desecratedLevel
            )
        )
    )

    private fun generateHash(apiResponse: TabularApiResponse): String {
        return Hashing.sha256()
            .hashString(fromTabularApiResponse(apiResponse).toString().removePrefix("Legacy"), Charsets.UTF_8)
            .toString()
    }
}

data class LegacyTabularApiResponse(
    val columns: List<String>,
    val rows: List<List<String>>,
    val context: ApiResponseContext
) {
    companion object {
        fun fromTabularApiResponse(tabularApiResponse: TabularApiResponse): LegacyTabularApiResponse {
            return LegacyTabularApiResponse(
                tabularApiResponse.columns,
                tabularApiResponse.rows.map { listOf(it[0], it[1], it[2]) },
                tabularApiResponse.context
            )
        }
    }
}

data class DesecratedAtomicTcsTestDataExpectation(
    val version: Version,
    val monsterId: String,
    val monsterType: MonsterType,
    val difficulty: Difficulty,
    val numPlayers: Int,
    val partySize: Int,
    val desecratedLevel: Int,
    val hash: String
)

data class DesecratedMonstersTestDataExpectation(
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

data class DesecratedItemsTestDataExpectation(
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