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
            22,
            Monster(
                fetishShamanMonsterClass.id,
                fetishShamanMonsterClass,
                area1Data,
                NORMAL,
                REGULAR,
                fetishShamanMonsterClass.monsterClassProperties.getValue(NORMAL, TreasureClassType.REGULAR)
            ).level
        )
        assertEquals(
            24,
            Monster(
                fetishShamanMonsterClass.id,
                fetishShamanMonsterClass,
                area1Data,
                NORMAL,
                CHAMPION,
                fetishShamanMonsterClass.monsterClassProperties.getValue(NORMAL, TreasureClassType.CHAMPION)
            ).level
        )
        assertEquals(
            83,
            Monster(
                fetishShamanMonsterClass.id,
                fetishShamanMonsterClass,
                area2Data,
                HELL,
                REGULAR,
                fetishShamanMonsterClass.monsterClassProperties.getValue(HELL, TreasureClassType.REGULAR)
            ).level
        )
        assertEquals(
            85,
            Monster(
                fetishShamanMonsterClass.id,
                fetishShamanMonsterClass,
                area2Data,
                HELL,
                CHAMPION,
                fetishShamanMonsterClass.monsterClassProperties.getValue(HELL, TreasureClassType.CHAMPION)
            ).level
        )
        assertEquals(
            83, Monster(
                radamentMonsterClass.id,
                radamentMonsterClass, Area(
                    "area-radament",
                    "area-radament-name",
                    levelsPerDifficulty(hell = 80),
                    ImmutableTable.builder<Difficulty, MonsterType, Set<String>>()
                        .put(HELL, REGULAR, setOf("radament"))
                        .build()
                ), HELL, BOSS, fetishShamanMonsterClass.monsterClassProperties.getValue(HELL, TreasureClassType.REGULAR)
            ).level
        )
    }
}