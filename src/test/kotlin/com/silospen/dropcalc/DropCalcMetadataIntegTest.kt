package com.silospen.dropcalc

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.silospen.dropcalc.resource.MetadataResource
import com.silospen.dropcalc.resource.MetadataResponse
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.File
import java.util.concurrent.Callable

@SpringBootTest
class DropCalcMetadataIntegTest {
    @Autowired
    private lateinit var metadataResource: MetadataResource

    @Autowired
    private lateinit var jacksonObjectMapper: ObjectMapper

    private val largeIntegTestRunner by lazy {
        LargeIntegTestRunner(jacksonObjectMapper)
    }

    private val monstersMetadataTestFile = File("src/test/resources/integExpectations/monstersMetadataTest")
    private val monstersDescMetadataTestFile = File("src/test/resources/integExpectations/monstersDescMetadataTest")
    private val itemsMetadataTestFile = File("src/test/resources/integExpectations/itemsMetadataTest")

    @Test
    fun monstersMetadataTest() {
        largeIntegTestRunner.runTests(monstersMetadataTestFile, { expected ->
            getMonstersMetadataExpectation(
                expected.version,
                expected.monsterType,
                expected.difficulty,
                false,
            )
        }, object : TypeReference<List<MonstersMetadataTestDataExpectation>>() {})
    }

    @Test
    fun monstersMetadataTest_desc() {
        //TODO, combine
        largeIntegTestRunner.runTests(monstersDescMetadataTestFile, { expected ->
            getMonstersMetadataExpectation(
                expected.version,
                expected.monsterType,
                expected.difficulty,
                true,
            )
        }, object : TypeReference<List<MonstersMetadataTestDataExpectation>>() {})
    }

    @Test
    fun itemsMetadataTest() {
        largeIntegTestRunner.runTests(itemsMetadataTestFile, { expected ->
            getItemMetadataExpectation(
                expected.version,
                expected.apiItemQuality,
                expected.itemVersion,
            )
        }, object : TypeReference<List<ItemsMetadataTestDataExpectation>>() {})
    }

    @Test
    @Disabled
    fun generateMonstersTestData() {
        largeIntegTestRunner.generateTestData(monstersMetadataTestFile, ::generateMonstersMetadataTestDataInputs)
    }

    @Test
    @Disabled
    fun generateDescMonstersTestData() {
        largeIntegTestRunner.generateTestData(
            monstersDescMetadataTestFile,
            ::generateDescMonstersMetadataTestDataInputs
        )
    }

    @Test
    @Disabled
    fun generateItemsTestData() {
        largeIntegTestRunner.generateTestData(itemsMetadataTestFile, ::generateItemsMetadataTestDataInputs)
    }

    private fun generateItemsMetadataTestDataInputs(counter: Counter): List<Callable<ItemsMetadataTestDataExpectation>> {
        val result = mutableListOf<Callable<ItemsMetadataTestDataExpectation>>()
        for (version in Version.values()) {
            for (itemQuality in ApiItemQuality.values()) {
                for (itemVersion in ItemVersion.values()) {
                    result.add(
                        Callable {
                            counter.incrementAndPossiblyPrint()
                            getItemMetadataExpectation(
                                version,
                                itemQuality,
                                itemVersion,
                            )
                        }
                    )
                }
            }
        }
        return result
    }

    private fun getItemMetadataExpectation(
        version: Version,
        apiItemQuality: ApiItemQuality,
        itemVersion: ItemVersion
    ) = ItemsMetadataTestDataExpectation(
        version,
        apiItemQuality,
        itemVersion,
        metadataResource.getItems(
            version,
            apiItemQuality,
            itemVersion,
        )
    )

    fun generateMonstersMetadataTestDataInputs(counter: Counter): List<Callable<MonstersMetadataTestDataExpectation>> {
        val result = mutableListOf<Callable<MonstersMetadataTestDataExpectation>>()
        for (version in Version.values()) {
            for (monsterType in MonsterType.values()) {
                for (difficulty in Difficulty.values()) {
//                    for(desecrated in listOf(true, false)) {}
                    result.add(
                        Callable {
                            counter.incrementAndPossiblyPrint()
                            getMonstersMetadataExpectation(
                                version,
                                monsterType,
                                difficulty,
                                false,
                            )
                        }
                    )
                }
            }
        }
        return result
    }

    fun generateDescMonstersMetadataTestDataInputs(counter: Counter): List<Callable<MonstersMetadataTestDataExpectation>> {
        val result = mutableListOf<Callable<MonstersMetadataTestDataExpectation>>()
        for (version in Version.values()) {
            for (monsterType in MonsterType.values()) {
                for (difficulty in Difficulty.values()) {
//                    for(desecrated in listOf(true, false)) {}
                    result.add(
                        Callable {
                            counter.incrementAndPossiblyPrint()
                            getMonstersMetadataExpectation(
                                version,
                                monsterType,
                                difficulty,
                                true,
                            )
                        }
                    )
                }
            }
        }
        return result
    }

    private fun getMonstersMetadataExpectation(
        version: Version,
        monsterType: MonsterType,
        difficulty: Difficulty,
        desecrated: Boolean
    ) = MonstersMetadataTestDataExpectation(
        version,
        monsterType,
        difficulty,
        metadataResource.getMonsters(
            version,
            difficulty,
            monsterType,
            desecrated,
        )
    )

    data class MonstersMetadataTestDataExpectation(
        val version: Version,
        val monsterType: MonsterType,
        val difficulty: Difficulty,
        val metadataResponse: List<MetadataResponse>
    )

    data class ItemsMetadataTestDataExpectation(
        val version: Version,
        val apiItemQuality: ApiItemQuality,
        val itemVersion: ItemVersion,
        val metadataResponse: List<MetadataResponse>
    )
}