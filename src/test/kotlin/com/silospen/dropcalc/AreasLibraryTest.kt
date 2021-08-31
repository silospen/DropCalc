package com.silospen.dropcalc

import com.google.common.collect.ImmutableTable
import com.silospen.dropcalc.Difficulty.HELL
import com.silospen.dropcalc.Difficulty.NORMAL
import com.silospen.dropcalc.MonsterType.CHAMPION
import com.silospen.dropcalc.MonsterType.REGULAR
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class AreasLibraryTest {
    @Test
    fun areasLibrary() {
        val areas = listOf(
            Area(
                "area-1",
                EnumMap(Difficulty::class.java),
                ImmutableTable.of(NORMAL, REGULAR, setOf("rmon1", "rmon2"))
            ),
            Area(
                "area-2",
                EnumMap(Difficulty::class.java),
                ImmutableTable.builder<Difficulty, MonsterType, Set<String>>()
                    .put(NORMAL, REGULAR, setOf("rmon2"))
                    .put(HELL, CHAMPION, setOf("rmon2"))
                    .build()
            )
        )
        val actual = AreasLibrary.fromAreas(areas)
        val areasByMonsterProperties = mapOf(
            Triple("rmon1", NORMAL, REGULAR) to setOf(areas[0]),
            Triple("rmon2", NORMAL, REGULAR) to areas.toSet(),
            Triple("rmon2", HELL, CHAMPION) to setOf(areas[1])
        )
        val expected = AreasLibrary(areasByMonsterProperties)
        assertEquals(expected, actual)
        assertEquals(
            areas.toSet(),
            actual.getAreasForMonsterClassId("rmon2", NORMAL, REGULAR)
        )
    }
}