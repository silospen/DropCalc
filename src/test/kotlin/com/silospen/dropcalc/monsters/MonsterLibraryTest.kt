package com.silospen.dropcalc.monsters

import com.google.common.collect.HashBasedTable
import com.google.common.collect.ImmutableTable
import com.silospen.dropcalc.*
import com.silospen.dropcalc.Difficulty.*
import com.silospen.dropcalc.MonsterType.BOSS
import com.silospen.dropcalc.MonsterType.REGULAR
import com.silospen.dropcalc.areas.AreasLibrary
import com.silospen.dropcalc.treasureclasses.TreasureClassLibrary
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock

class MonsterLibraryTest {

    private val treasureClassLibrary = mockTreasureClassLibrary()
    private val monsterClassConfigs = monsterClassTestData.toList()
    private val areasLibrary: AreasLibrary = AreasLibrary.fromAreas(areasTestData)

    private fun createMonsterLibrary(library: TreasureClassLibrary, monsterClassConfigs: List<MonsterClass>) =
        MonsterLibrary.fromConfig(
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
            MonsterFactory(areasLibrary, library),
            library
        )

    @Test
    fun testConstruction() {
        assertEquals(
            MonsterLibrary(monstersTestData, treasureClassLibrary),
            createMonsterLibrary(treasureClassLibrary, monsterClassConfigs)
        )
    }

    @Test
    fun testMixOfDesecratedAndNon() {
        val desecratedSkeleton = skeletonMonster.copy(id = "skeleton1d", isDesecrated = true)
        val monsters = monstersTestData.toMutableSet().apply {
            add(desecratedSkeleton)
        }
        val monsterLibrary = MonsterLibrary(monsters, treasureClassLibrary)
        val baseExpectedMonsters =
            monstersTestData.filter { it.id == fetishShamanMonsterClass.id && it.type == REGULAR }
        assertEquals(
            listOf(desecratedSkeleton) + baseExpectedMonsters,
            monsterLibrary.getMonsters(true, 0, monsterType = REGULAR)
        )
        assertEquals(
            listOf(skeletonMonster) + baseExpectedMonsters,
            monsterLibrary.getMonsters(false, 0, monsterType = REGULAR)
        )
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
            ),
            treasureClassLibrary
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
            ),
            treasureClassLibrary
        )
        assertEquals(expected, actual)
    }

    @Test
    fun upgradeMonsterToDesecrated() {
        val monsterClassTreasureClasses =
            HashBasedTable.create(skeletonMonsterClass.monsterClassTreasureClasses).apply {
                put(NORMAL, TreasureClassType.DESECRATED_REGULAR, "Desecrated Act 1 H2H A")
            }
        val desecratedSkeletonMonsterClass =
            skeletonMonsterClass.copy(
                monsterClassTreasureClasses = monsterClassTreasureClasses,
            )
        val monsterLibrary = createMonsterLibrary(
            mockTreasureClassLibrary("u"),
            listOf(
                desecratedSkeletonMonsterClass,
                durielMonsterClass,
                putridDefilerMonsterClass,
                fetishShamanMonsterClass,
                radamentMonsterClass
            )
        )
        val expectedRegularSkeletonMonster =
            skeletonMonster.copy(monsterClass = desecratedSkeletonMonsterClass, treasureClass = "uAct 1 H2H A")
        val expectedDesecratedSkeletonMonster = skeletonMonster.copy(
            id = "skeleton1d",
            name = "Skeleton-name (d)",
            level = 45,
            treasureClass = "uuDesecrated Act 1 H2H A",
            monsterClass = desecratedSkeletonMonsterClass,
            isDesecrated = true,
            treasureClassType = TreasureClassType.DESECRATED_REGULAR,
        )
        val expectedDesecrated = listOf(
            expectedDesecratedSkeletonMonster,
            fetishShamanMonster1.copy(level = 45, treasureClass = "uuAct 3 Cast A"),
            fetishShamanMonster2.copy(level = 45, treasureClass = "uuAct 3 Cast A"),
        )
        assertEquals(
            expectedDesecrated,
            monsterLibrary.getMonsters(true, 99, difficulty = NORMAL, monsterType = REGULAR)
        )
        assertEquals(
            expectedDesecrated,
            monsterLibrary.getMonsters(true, 99, monsterType = REGULAR)
        )
        assertEquals(
            listOf(expectedDesecratedSkeletonMonster),
            monsterLibrary.getMonsters(true, 99, monsterId = "skeleton1d", difficulty = NORMAL, REGULAR)
        )
        assertEquals(
            emptyList<Monster>(),
            monsterLibrary.getMonsters(true, 99, monsterId = "skeleton1", difficulty = NORMAL, monsterType = REGULAR)
        )


        val expectedNormal = listOf(
            expectedRegularSkeletonMonster,
            fetishShamanMonster1.copy(treasureClass = "uAct 3 Cast A"),
            fetishShamanMonster2.copy(treasureClass = "uAct 3 Cast A"),
        )
        assertEquals(
            expectedNormal,
            monsterLibrary.getMonsters(false, 99, difficulty = NORMAL, monsterType = REGULAR)
        )
        assertEquals(
            expectedNormal,
            monsterLibrary.getMonsters(false, 99, monsterType = REGULAR)
        )
        assertEquals(
            emptyList<Monster>(),
            monsterLibrary.getMonsters(false, 99, monsterId = "skeleton1d", difficulty = NORMAL, monsterType = REGULAR)
        )
        assertEquals(
            listOf(expectedRegularSkeletonMonster),
            monsterLibrary.getMonsters(false, 99, monsterId = "skeleton1", difficulty = NORMAL, monsterType = REGULAR)
        )
    }
}

private fun mockTreasureClassLibrary(prefix: String = ""): TreasureClassLibrary = mock {
    on { changeTcBasedOnLevel(any<String>(), any(), any(), any()) } doAnswer {
        "$prefix${it.getArgument<String>(0)}"
    }
    on { getTreasureClass(any()) } doAnswer { VirtualTreasureClass(it.getArgument(0)) }
}