package com.silospen.dropcalc

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Charsets
import com.google.common.hash.Hashing
import com.silospen.dropcalc.resource.ApiResource
import com.silospen.dropcalc.resource.ApiResponse
import com.silospen.dropcalc.resource.VersionedMetadataResource
import org.apache.commons.math3.util.Precision
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
    private val bigItemsTestFile = File("src/test/resources/integExpectations/bigItemsTests")

    @Test
    fun runItemsTest() {
        runTests(bigItemsTestFile, { expected ->
            getItemExpectation(
                expected.version,
                expected.itemId,
                expected.itemQuality,
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
        runTests(bigMonstersTestFile, { expected ->
            getMonstersExpectation(
                expected.version,
                expected.monsterId,
                expected.monsterType,
                expected.difficulty,
                expected.numPlayers,
                expected.partySize,
                expected.itemQuality,
                expected.magicFind
            )
        }, object : TypeReference<List<MonstersTestDataExpectation>>() {})
    }

    @Test
    fun runAtomicTcsTest() {
        runTests(bigTcTestFile, { expected ->
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

    private fun <T> runTests(file: File, actualGenerator: (T) -> T, typeReference: TypeReference<List<T>>) {
        val counter = Counter()
        val tests = jacksonObjectMapper.readValue(file, typeReference)
            .map { expected ->
                Callable {
                    assertEquals(
                        expected,
                        actualGenerator(expected)
                    )
                    counter.incrementAndPossiblyPrint()
                }
            }
        threadPool.invokeAll(tests).forEach { it.get() }
    }

    @Test
    @Disabled
    fun generateItemsTestData() {
        generateTestData(bigItemsTestFile, ::generateItemsTestDataInputs)
    }

    @Test
    @Disabled
    fun generateMonstersTestData() {
        generateTestData(bigMonstersTestFile, ::generateMonstersTestDataInputs)
    }

    @Test
    @Disabled
    fun generateAtomicTcsTestData() {
        generateTestData(bigTcTestFile, ::generateAtomicTcsTestDataInputs)
    }

    private fun <T : Any> generateTestData(file: File, dataGenerator: (Counter) -> List<Callable<T>>) {
        val counter = Counter()
        TestDataExpectationWriter.init(file).use { writer ->
            threadPool.invokeAll(dataGenerator(counter))
                .forEach { writer.write(it.get()) }
        }
    }

    fun generateAtomicTcsTestDataInputs(counter: Counter): List<Callable<AtomicTcsTestDataExpectation>> {
        val result = mutableListOf<Callable<AtomicTcsTestDataExpectation>>()
        for (version in Version.values()) {
            val monsterLibrary = versionedMetadataResources.getValue(version).monsterLibrary
            for (monsterType in MonsterType.values()) {
                for (monsterId in monsterLibrary.getMonsters(monsterType).asSequence().map { it.id }.distinct()) {
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
                for (monsterId in monsterLibrary.getMonsters(monsterType).asSequence().map { it.id }.distinct()) {
                    for (difficulty in Difficulty.values()) {
                        for (nPlayers in listOf(7)) {
                            for (nGroup in listOf(5)) {
                                for (itemQuality in ItemQuality.values()) {
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
                                                    itemQuality,
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
                for (itemQuality in ItemQuality.values()) {
                    for (itemId in itemLibrary.items.asSequence().filter { it.quality == itemQuality }.map { it.id }
                        .distinct()) {
                        for (difficulty in Difficulty.values()) {
                            for (nPlayers in listOf(7)) {
                                for (nGroup in listOf(5)) {
                                    for (magicFind in listOf(0, 975)) {
                                        result.add(
                                            Callable {
                                                counter.incrementAndPossiblyPrint()
                                                getItemExpectation(
                                                    version,
                                                    itemId,
                                                    itemQuality,
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
        itemQuality: ItemQuality,
        monsterType: MonsterType,
        difficulty: Difficulty,
        numPlayers: Int,
        partySize: Int,
        magicFind: Int
    ) = ItemsTestDataExpectation(
        version,
        itemId,
        itemQuality,
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
                itemQuality,
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
        itemQuality: ItemQuality,
        magicFind: Int
    ) = MonstersTestDataExpectation(
        version,
        monsterId,
        monsterType,
        difficulty,
        numPlayers,
        partySize,
        itemQuality,
        magicFind,
        generateHash(
            apiResource.getMonster(
                version,
                monsterId,
                monsterType,
                difficulty,
                numPlayers,
                partySize,
                itemQuality,
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

    private fun generateHash(apiResponses: List<ApiResponse>) = Hashing.sha256().hashString(
        apiResponses
            .asSequence()
            .map { it.copy(prob = Precision.round(it.prob, 11)) }
            .sortedWith(compareBy({ it.name }, { it.area }))
            .toList()
            .toString(),
        Charsets.UTF_8
    ).toString()
}


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
    val itemQuality: ItemQuality,
    val magicFind: Int,
    val hash: String
)

data class ItemsTestDataExpectation(
    val version: Version,
    val itemId: String,
    val itemQuality: ItemQuality,
    val monsterType: MonsterType,
    val difficulty: Difficulty,
    val numPlayers: Int,
    val partySize: Int,
    val magicFind: Int,
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

    fun write(o: Any) {
        jsonGenerator.writeObject(o)
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