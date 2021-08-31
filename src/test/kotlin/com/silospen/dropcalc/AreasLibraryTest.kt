package com.silospen.dropcalc

import com.silospen.dropcalc.Difficulty.HELL
import com.silospen.dropcalc.Difficulty.NORMAL
import com.silospen.dropcalc.MonsterType.CHAMPION
import com.silospen.dropcalc.MonsterType.REGULAR
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AreasLibraryTest {
    @Test
    fun areasLibrary() {
        val actual = AreasLibrary.fromAreas(areasTestData)
        val areasByMonsterProperties = mapOf(
            Triple("skeleton1", NORMAL, REGULAR) to setOf(areasTestData[0]),
            Triple("fetishshaman2", NORMAL, REGULAR) to areasTestData.toSet(),
            Triple("fetishshaman2", HELL, CHAMPION) to setOf(areasTestData[1])
        )
        val expected = AreasLibrary(areasByMonsterProperties)
        assertEquals(expected, actual)
        assertEquals(
            areasTestData.toSet(),
            actual.getAreasForMonsterClassId("fetishshaman2", NORMAL, REGULAR)
        )
    }
}