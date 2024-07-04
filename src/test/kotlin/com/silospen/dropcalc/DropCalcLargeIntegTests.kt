package com.silospen.dropcalc

import com.fasterxml.jackson.core.JsonGenerator
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

private val mapper = ObjectMapper()

@SpringBootTest
class DropCalcLargeIntegTests {
    @Autowired
    private lateinit var jacksonObjectMapper: ObjectMapper

    @Autowired
    private lateinit var apiResource: ApiResource

    @Autowired
    private lateinit var versionedMetadataResources: Map<Version, VersionedMetadataResource>

    private val threadPool = Executors.newFixedThreadPool(6)

    private val bigTcTestFile = File("src/test/resources/integExpectations/bigTcTests")
    private val bigMonstersTestFile = File("src/test/resources/integExpectations/bigMonstersTests")

    @Test
    fun runMonstersTest() {

    }

    @Test
    fun runAtomicTcsTest() {
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

//    @Test
//    @Disabled
//    fun generateMonstersTestData() {
//        TestDataExpectationWriter.init(bigTcTestFile).use { writer ->
//            threadPool.invokeAll(generateMonsterTestDataInputs())
//                .forEach { writer.write(it.get()) }
//        }
//    }

    @Test
    @Disabled
    fun generateAtomicTcsTestData() {
        TestDataExpectationWriter.init(bigTcTestFile).use { writer ->
            threadPool.invokeAll(generateAtomicTcsTestDataInputs())
                .forEach { writer.write(it.get()) }
        }
    }

    fun generateAtomicTcsTestDataInputs(): List<Callable<TestDataExpectation>> {
        val result = mutableListOf<Callable<TestDataExpectation>>()
        for (version in Version.values()) {
            val monsterLibrary = versionedMetadataResources.getValue(version).monsterLibrary
            for (monsterType in MonsterType.values()) {
                for (monsterId in monsterLibrary.getMonsters(monsterType).asSequence().map { it.id }.distinct()) {
                    for (difficulty in Difficulty.values()) {
                        for (nPlayers in listOf(3, 7)) {
                            for (nGroup in listOf(5, 8)) {
                                result.add(
                                    Callable {
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


class TestDataExpectationWriter(private val jsonGenerator: JsonGenerator) :
    AutoCloseable {

    companion object {
        fun init(file: File): TestDataExpectationWriter {
            val jsonGenerator = mapper.createGenerator(file.bufferedWriter())
            jsonGenerator.writeStartArray()
            return TestDataExpectationWriter(jsonGenerator)
        }
    }

    fun write(testDataExpectation: TestDataExpectation) {
        jsonGenerator.writeObject(testDataExpectation)
    }

    override fun close() {
        jsonGenerator.writeEndArray()
        jsonGenerator.close()
    }
}

data class Counter(private val counter: AtomicLong = AtomicLong(0L)) {
    fun incrementAndPossiblyPrint() {
        val value = counter.incrementAndGet()
        if (value % 1000 == 0L) println(value)
    }
}