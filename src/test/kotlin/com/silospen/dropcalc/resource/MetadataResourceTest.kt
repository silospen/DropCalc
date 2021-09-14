package com.silospen.dropcalc.resource

import com.silospen.dropcalc.*
import com.silospen.dropcalc.monsters.Monster
import com.silospen.dropcalc.monsters.MonsterLibrary
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MetadataResourceTest {
    @Test
    fun monsters() {
        val metadataResource = MetadataResource(
            MonsterLibrary(
                setOf(
                    Monster(
                        "1",
                        "1-name",
                        skeletonMonsterClass,
                        area1Data,
                        Difficulty.NORMAL,
                        MonsterType.SUPERUNIQUE,
                        tc("foo")
                    ),
                    Monster(
                        "1",
                        "1-name",
                        skeletonMonsterClass,
                        area2Data,
                        Difficulty.NORMAL,
                        MonsterType.SUPERUNIQUE,
                        tc("foo")
                    ),
                    Monster(
                        "2",
                        "2-name",
                        skeletonMonsterClass,
                        area2Data,
                        Difficulty.NORMAL,
                        MonsterType.SUPERUNIQUE,
                        tc("foo")
                    ),
                    Monster(
                        "1",
                        "1-name",
                        skeletonMonsterClass,
                        area2Data,
                        Difficulty.NORMAL,
                        MonsterType.BOSS,
                        tc("foo")
                    )
                )
            )
        )
        assertEquals(
            listOf(MonstersResponse("1-name", "1"), MonstersResponse("2-name", "2")),
            metadataResource.getMonsters(Difficulty.NORMAL, MonsterType.SUPERUNIQUE)
        )
        assertEquals(
            listOf(MonstersResponse("1-name", "1")),
            metadataResource.getMonsters(Difficulty.NORMAL, MonsterType.BOSS)
        )
    }
}