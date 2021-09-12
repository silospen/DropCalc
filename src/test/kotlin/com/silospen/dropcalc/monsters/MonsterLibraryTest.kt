package com.silospen.dropcalc.monsters

import com.google.common.collect.ImmutableTable
import com.silospen.dropcalc.*
import com.silospen.dropcalc.areas.AreasLibrary
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MonsterLibraryTest {
    @Test
    fun test() {
        val monsterClassConfigs = monsterClassTestData.toList()
        val areasLibrary: AreasLibrary = AreasLibrary.fromAreas(areasTestData)
        val actual = MonsterLibrary.fromConfig(
            monsterClassConfigs,
            areasLibrary
        )
        val expected = MonsterLibrary(monstersTestData)
        assertEquals(expected, actual)
    }

    @Test
    fun questMonsterTest() {
        val durielArea = Area(
            "duriels-house",
            "DurielsHouse",
            mapOf(Difficulty.NORMAL to 10, Difficulty.NIGHTMARE to 20, Difficulty.HELL to 30),
            ImmutableTable.builder<Difficulty, MonsterType, Set<String>>()
                .put(Difficulty.NORMAL, MonsterType.REGULAR, setOf("duriel"))
                .put(Difficulty.NIGHTMARE, MonsterType.REGULAR, setOf("duriel"))
                .put(Difficulty.HELL, MonsterType.REGULAR, setOf("duriel"))
                .build()
        )
        val actual = MonsterLibrary.fromConfig(
            listOf(durielMonsterClass),
            AreasLibrary.fromAreas(
                listOf(
                    durielArea
                )
            )
        )
        val expected  = MonsterLibrary(setOf(
            Monster("duriel", durielMonsterClass, durielArea, Difficulty.NORMAL, MonsterType.REGULAR, TreasureClassType.REGULAR),
            Monster("durielq", durielMonsterClass, durielArea, Difficulty.NORMAL, MonsterType.REGULAR, TreasureClassType.QUEST),
            Monster("duriel", durielMonsterClass, durielArea, Difficulty.NIGHTMARE, MonsterType.REGULAR, TreasureClassType.REGULAR),
            Monster("durielq", durielMonsterClass, durielArea, Difficulty.NIGHTMARE, MonsterType.REGULAR, TreasureClassType.QUEST),
            Monster("duriel", durielMonsterClass, durielArea, Difficulty.HELL, MonsterType.REGULAR, TreasureClassType.REGULAR),
            Monster("durielq", durielMonsterClass, durielArea, Difficulty.HELL, MonsterType.REGULAR, TreasureClassType.QUEST),
        ))
        assertEquals(expected, actual)
    }
}
