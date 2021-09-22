package com.silospen.dropcalc.monsters

import com.google.common.collect.ImmutableTable
import com.silospen.dropcalc.*
import com.silospen.dropcalc.Difficulty.*
import com.silospen.dropcalc.MonsterType.BOSS
import com.silospen.dropcalc.areas.AreasLibrary
import com.silospen.dropcalc.treasureclasses.TreasureClassCalculator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock

class MonsterLibraryTest {
    @Test
    fun test() {
        val monsterClassConfigs = monsterClassTestData.toList()
        val areasLibrary: AreasLibrary = AreasLibrary.fromAreas(areasTestData)
        val actual = MonsterLibrary.fromConfig(
            monsterClassConfigs,
            listOf(
                SuperUniqueMonsterConfig(
                    "Bonebreak",
                    "Bonebreak-name",
                    "bonebreak's area",
                    "skeleton1",
                    true,
                    mapOf(NORMAL to "Bonebreak TC", HELL to "Bonebreak TC(H)")
                )
            ),
            MonsterFactory(areasLibrary, mockTreasureClassCalculator())
        )
        val expected = MonsterLibrary(monstersTestData)
        assertEquals(expected, actual)
    }

    @Test
    fun questMonsterTest() {
        val durielArea = Area(
            "duriels-house",
            "DurielsHouse",
            mapOf(NORMAL to 10, NIGHTMARE to 20, HELL to 30),
            ImmutableTable.builder<Difficulty, MonsterType, Set<String>>()
                .put(NORMAL, BOSS, setOf("duriel"))
                .put(NIGHTMARE, BOSS, setOf("duriel"))
                .put(HELL, BOSS, setOf("duriel"))
                .build()
        )
        val actual = MonsterLibrary.fromConfig(
            listOf(durielMonsterClass),
            emptyList(),
            MonsterFactory(
                AreasLibrary.fromAreas(
                    listOf(
                        durielArea
                    )
                ),
                mockTreasureClassCalculator("u")
            )
        )
        val expected = MonsterLibrary(
            setOf(
                Monster(
                    "duriel",
                    "Duriel-name",
                    durielMonsterClass,
                    durielArea,
                    NORMAL,
                    BOSS,
                    "u" + durielMonsterClass.monsterClassTreasureClasses.getValue(NORMAL, TreasureClassType.REGULAR),
                    22
                ),
                Monster(
                    "durielq",
                    "Duriel-name (q)",
                    durielMonsterClass,
                    durielArea,
                    NORMAL,
                    BOSS,
                    "u" + durielMonsterClass.monsterClassTreasureClasses.getValue(NORMAL, TreasureClassType.QUEST),
                    22
                ),
                Monster(
                    "duriel",
                    "Duriel-name",
                    durielMonsterClass,
                    durielArea,
                    NIGHTMARE,
                    BOSS,
                    "u" + durielMonsterClass.monsterClassTreasureClasses.getValue(NIGHTMARE, TreasureClassType.REGULAR),
                    55
                ),
                Monster(
                    "durielq",
                    "Duriel-name (q)",
                    durielMonsterClass,
                    durielArea,
                    NIGHTMARE,
                    BOSS,
                    "u" + durielMonsterClass.monsterClassTreasureClasses.getValue(NIGHTMARE, TreasureClassType.QUEST),
                    55
                ),
                Monster(
                    "duriel",
                    "Duriel-name",
                    durielMonsterClass,
                    durielArea,
                    HELL,
                    BOSS,
                    "u" + durielMonsterClass.monsterClassTreasureClasses.getValue(HELL, TreasureClassType.REGULAR),
                    88
                ),
                Monster(
                    "durielq",
                    "Duriel-name (q)",
                    durielMonsterClass,
                    durielArea,
                    HELL,
                    BOSS,
                    "u" + durielMonsterClass.monsterClassTreasureClasses.getValue(HELL, TreasureClassType.QUEST),
                    88
                ),
            )
        )
        assertEquals(expected, actual)
    }
}

private fun mockTreasureClassCalculator(prefix: String = ""): TreasureClassCalculator = mock {
    on { changeTcBasedOnLevel(any(), any(), any()) } doAnswer {
        VirtualTreasureClass("$prefix${it.getArgument<TreasureClass>(0).name}")
    }
    on { getTreasureClass(any()) } doAnswer { VirtualTreasureClass(it.getArgument(0)) }
}