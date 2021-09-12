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
                TreasureClassType.REGULAR
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
                TreasureClassType.CHAMPION
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
                TreasureClassType.REGULAR
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
                TreasureClassType.CHAMPION
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
                ), HELL, BOSS, TreasureClassType.REGULAR
            ).level
        )
    }
}