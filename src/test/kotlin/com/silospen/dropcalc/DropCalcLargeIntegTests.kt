package com.silospen.dropcalc

import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Charsets
import com.google.common.hash.Hashing
import com.silospen.dropcalc.resource.ApiResource
import com.silospen.dropcalc.resource.VersionedMetadataResource
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.File
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicLong

@SpringBootTest
class DropCalcLargeIntegTests {
    @Autowired
    private lateinit var jacksonObjectMapper: ObjectMapper

    @Autowired
    private lateinit var apiResource: ApiResource

    @Autowired
    private lateinit var versionedMetadataResources: Map<Version, VersionedMetadataResource>

    private val bigTcTestFile = File("src/test/resources/integExpectations/bigTcTests")

    @Test
    fun runAtomicTcsTest() {
        val threadPool = Executors.newFixedThreadPool(6)
        val tests: MutableList<Callable<Unit>> = mutableListOf()
        val iterations = AtomicLong(0)
        bigTcTestFile.bufferedReader().use {
            val jsonParser = jacksonObjectMapper.createParser(it)
            if (jsonParser.nextToken() != JsonToken.START_ARRAY) throw RuntimeException("Expected an array")
            while (jsonParser.nextToken() == JsonToken.START_OBJECT) {
                val expected = jacksonObjectMapper.readValue(jsonParser, TestDataExpectation::class.java)
                tests.add {
                    assertEquals(
                        expected,
                        getAtomicTcs(
                            expected.version,
                            expected.monsterId,
                            expected.monsterType,
                            expected.difficulty,
                            expected.numPlayers,
                            expected.partySize
                        )
                    )
                    val value = iterations.incrementAndGet()
                    if (value % 1000 == 0L) println("Running test: $value")

                }
            }
        }
        threadPool.invokeAll(tests)
            .forEach { it.get() }
    }

    @Test
    @Disabled
    fun generateAtomicTcsTestData() {
        bigTcTestFile.createNewFile()
        val jsonGenerator = jacksonObjectMapper.createGenerator(bigTcTestFile.bufferedWriter())
        jsonGenerator.use {
            var iterations = 0L
            jsonGenerator.writeStartArray()
            for (version in Version.values()) {
                val monsterLibrary = versionedMetadataResources.getValue(version).monsterLibrary
                for (monsterType in MonsterType.values()) {
                    for (monster in monsterLibrary.getMonsters(monsterType)) {
                        for (difficulty in Difficulty.values()) {
                            for (nPlayers in listOf(3, 7)) {
                                jsonGenerator.writeObject(
                                    getAtomicTcs(
                                        version,
                                        monster.id,
                                        monster.type,
                                        difficulty,
                                        nPlayers,
                                        3
                                    )
                                )
                                if (++iterations % 1000 == 0L) println(iterations)
                            }
                        }
                    }
                }
            }
            jsonGenerator.writeEndArray()
        }
    }

    private fun getAtomicTcs(
        version: Version,
        monsterId: String,
        monsterType: MonsterType,
        difficulty: Difficulty,
        numPlayers: Int,
        partySize: Int
    ): TestDataExpectation {
        val hash = Hashing.sha256().hashString(
            apiResource.getAtomicTcs(version, monsterId, monsterType, difficulty, numPlayers, partySize)
                .sortedWith(compareBy({ it.name }, { it.area }))
                .toString(),
            Charsets.UTF_8
        ).toString()
        return TestDataExpectation(
            version,
            monsterId,
            monsterType,
            difficulty,
            numPlayers,
            partySize,
            hash
        )
    }
}


data class TestDataExpectation(
    val version: Version,
    val monsterId: String,
    val monsterType: MonsterType,
    val difficulty: Difficulty,
    val numPlayers: Int,
    val partySize: Int,
    val hash: String
)