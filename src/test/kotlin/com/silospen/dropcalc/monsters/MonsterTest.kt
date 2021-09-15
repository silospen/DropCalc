package com.silospen.dropcalc.monsters

import com.google.common.collect.ImmutableTable
import com.silospen.dropcalc.*
import com.silospen.dropcalc.Difficulty.HELL
import com.silospen.dropcalc.Difficulty.NORMAL
import com.silospen.dropcalc.MonsterType.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MonsterTest {
    @Test
    fun level() {
        assertEquals(
            25,
            Monster(
                fetishShamanMonsterClass.id,
                fetishShamanMonsterClass.name,
                fetishShamanMonsterClass,
                area1Data,
                NORMAL,
                MINION,
                fetishShamanMonsterClass.monsterClassTreasureClasses.getValue(NORMAL, TreasureClassType.REGULAR)
            ).level
        )
        assertEquals(
            22,
            Monster(
                fetishShamanMonsterClass.id,
                fetishShamanMonsterClass.name,
                fetishShamanMonsterClass,
                area1Data,
                NORMAL,
                REGULAR,
                fetishShamanMonsterClass.monsterClassTreasureClasses.getValue(NORMAL, TreasureClassType.REGULAR)
            ).level
        )
        assertEquals(
            24,
            Monster(
                fetishShamanMonsterClass.id,
                fetishShamanMonsterClass.name,
                fetishShamanMonsterClass,
                area1Data,
                NORMAL,
                CHAMPION,
                fetishShamanMonsterClass.monsterClassTreasureClasses.getValue(NORMAL, TreasureClassType.CHAMPION)
            ).level
        )
        assertEquals(
            83,
            Monster(
                fetishShamanMonsterClass.id,
                fetishShamanMonsterClass.name,
                fetishShamanMonsterClass,
                area2Data,
                HELL,
                REGULAR,
                fetishShamanMonsterClass.monsterClassTreasureClasses.getValue(HELL, TreasureClassType.REGULAR)
            ).level
        )
        assertEquals(
            85,
            Monster(
                fetishShamanMonsterClass.id,
                fetishShamanMonsterClass.name,
                fetishShamanMonsterClass,
                area2Data,
                HELL,
                CHAMPION,
                fetishShamanMonsterClass.monsterClassTreasureClasses.getValue(HELL, TreasureClassType.CHAMPION)
            ).level
        )
        assertEquals(
            83, Monster(
                radamentMonsterClass.id,
                radamentMonsterClass.name,
                radamentMonsterClass,
                Area(
                    "area-radament",
                    "area-radament-name",
                    levelsPerDifficulty(hell = 80),
                    ImmutableTable.builder<Difficulty, MonsterType, Set<String>>()
                        .put(HELL, REGULAR, setOf("radament"))
                        .build()
                ),
                HELL,
                BOSS,
                fetishShamanMonsterClass.monsterClassTreasureClasses.getValue(HELL, TreasureClassType.REGULAR)
            ).level
        )
    }
}