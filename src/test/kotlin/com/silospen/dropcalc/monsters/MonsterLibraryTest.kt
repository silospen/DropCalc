package com.silospen.dropcalc.monsters

import com.google.common.collect.ImmutableTable
import com.silospen.dropcalc.*
import com.silospen.dropcalc.Difficulty.*
import com.silospen.dropcalc.MonsterType.BOSS
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
            listOf(
                SuperUniqueMonsterConfig(
                    "Bonebreak",
                    "bonebreak's area",
                    "skeleton1",
                    true,
                    mapOf(NORMAL to tc("Bonebreak TC"), HELL to tc("Bonebreak TC(H)"))
                )
            ),
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
            AreasLibrary.fromAreas(
                listOf(
                    durielArea
                )
            )
        )
        val expected = MonsterLibrary(
            setOf(
                Monster(
                    "duriel",
                    durielMonsterClass,
                    durielArea,
                    NORMAL,
                    BOSS,
                    durielMonsterClass.monsterClassProperties.getValue(NORMAL, TreasureClassType.REGULAR)
                ),
                Monster(
                    "durielq",
                    durielMonsterClass,
                    durielArea,
                    NORMAL,
                    BOSS,
                    durielMonsterClass.monsterClassProperties.getValue(NORMAL, TreasureClassType.QUEST)
                ),
                Monster(
                    "duriel",
                    durielMonsterClass,
                    durielArea,
                    NIGHTMARE,
                    BOSS,
                    durielMonsterClass.monsterClassProperties.getValue(NIGHTMARE, TreasureClassType.REGULAR)
                ),
                Monster(
                    "durielq",
                    durielMonsterClass,
                    durielArea,
                    NIGHTMARE,
                    BOSS,
                    durielMonsterClass.monsterClassProperties.getValue(NIGHTMARE, TreasureClassType.QUEST)
                ),
                Monster(
                    "duriel",
                    durielMonsterClass,
                    durielArea,
                    HELL,
                    BOSS,
                    durielMonsterClass.monsterClassProperties.getValue(HELL, TreasureClassType.REGULAR)
                ),
                Monster(
                    "durielq",
                    durielMonsterClass,
                    durielArea,
                    HELL,
                    BOSS,
                    durielMonsterClass.monsterClassProperties.getValue(HELL, TreasureClassType.QUEST)
                ),
            )
        )
        assertEquals(expected, actual)
    }
}
