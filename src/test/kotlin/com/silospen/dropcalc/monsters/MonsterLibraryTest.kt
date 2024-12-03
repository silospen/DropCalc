package com.silospen.dropcalc.monsters

import com.google.common.collect.ImmutableTable
import com.silospen.dropcalc.*
import com.silospen.dropcalc.Difficulty.*
import com.silospen.dropcalc.MonsterType.BOSS
import com.silospen.dropcalc.areas.AreasLibrary
import com.silospen.dropcalc.treasureclasses.TreasureClassLibrary
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
                    ImmutableTable.builder<Difficulty, TreasureClassType, String>()
                        .put(NORMAL, TreasureClassType.REGULAR, "Bonebreak TC")
                        .put(HELL, TreasureClassType.REGULAR, "Bonebreak TC(H)")
                        .build()
                )
            ),
            MonsterFactory(areasLibrary, mockTreasureClassLibrary())
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
                mockTreasureClassLibrary("u")
            )
        )
        val expected = MonsterLibrary(
            setOf(
                Monster(
                    "duriel",
                    "duriel",
                    "Duriel-name",
                    durielMonsterClass,
                    durielArea,
                    NORMAL,
                    BOSS,
                    "u" + durielMonsterClass.monsterClassTreasureClasses.getValue(NORMAL, TreasureClassType.REGULAR),
                    false,
                    22,
                    false,
                    TreasureClassType.REGULAR
                ),
                Monster(
                    "durielq",
                    "duriel",
                    "Duriel-name (q)",
                    durielMonsterClass,
                    durielArea,
                    NORMAL,
                    BOSS,
                    "u" + durielMonsterClass.monsterClassTreasureClasses.getValue(NORMAL, TreasureClassType.QUEST),
                    false,
                    22,
                    false,
                    TreasureClassType.QUEST
                ),
                Monster(
                    "duriel",
                    "duriel",
                    "Duriel-name",
                    durielMonsterClass,
                    durielArea,
                    NIGHTMARE,
                    BOSS,
                    "u" + durielMonsterClass.monsterClassTreasureClasses.getValue(NIGHTMARE, TreasureClassType.REGULAR),
                    false,
                    55,
                    false,
                    TreasureClassType.REGULAR
                ),
                Monster(
                    "durielq",
                    "duriel",
                    "Duriel-name (q)",
                    durielMonsterClass,
                    durielArea,
                    NIGHTMARE,
                    BOSS,
                    "u" + durielMonsterClass.monsterClassTreasureClasses.getValue(NIGHTMARE, TreasureClassType.QUEST),
                    false,
                    55,
                    false,
                    TreasureClassType.QUEST
                ),
                Monster(
                    "duriel",
                    "duriel",
                    "Duriel-name",
                    durielMonsterClass,
                    durielArea,
                    HELL,
                    BOSS,
                    "u" + durielMonsterClass.monsterClassTreasureClasses.getValue(HELL, TreasureClassType.REGULAR),
                    false,
                    88,
                    false,
                    TreasureClassType.REGULAR
                ),
                Monster(
                    "durielq",
                    "duriel",
                    "Duriel-name (q)",
                    durielMonsterClass,
                    durielArea,
                    HELL,
                    BOSS,
                    "u" + durielMonsterClass.monsterClassTreasureClasses.getValue(HELL, TreasureClassType.QUEST),
                    false,
                    88,
                    false,
                    TreasureClassType.QUEST
                ),
            )
        )
        assertEquals(expected, actual)
    }
}

private fun mockTreasureClassLibrary(prefix: String = ""): TreasureClassLibrary = mock {
    on { changeTcBasedOnLevel(any<String>(), any(), any()) } doAnswer {
        "$prefix${it.getArgument<String>(0)}"
    }
    on { getTreasureClass(any()) } doAnswer { VirtualTreasureClass(it.getArgument(0)) }
}