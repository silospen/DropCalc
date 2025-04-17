package com.silospen.dropcalc.areas

import com.silospen.dropcalc.*
import com.silospen.dropcalc.Difficulty.HELL
import com.silospen.dropcalc.Difficulty.NORMAL
import com.silospen.dropcalc.MonsterType.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AreasLibraryTest {
    @Test
    fun areasLibrary() {
        val actual = AreasLibrary.fromAreas(areasTestData)
        val areasByMonsterProperties = mapOf(
            Triple("skeleton1", NORMAL, REGULAR) to setOf(areasTestData[0]),
            Triple("skeleton1", NORMAL, UNIQUE) to setOf(areasTestData[0]),
            Triple("fetishshaman2", NORMAL, REGULAR) to setOf(area1Data) + setOf(area2Data),
            Triple("fetishshaman2", HELL, CHAMPION) to setOf(areasTestData[1]),
            Triple("Bonebreak", NORMAL, SUPERUNIQUE) to setOf(areasTestData[2]),
            Triple("Bonebreak", HELL, SUPERUNIQUE) to setOf(areasTestData[2])
        )
        val expected = AreasLibrary(
            mapOf("area-1" to area1Data, "area-2" to area2Data, "bonebreak's area" to bonebreakAreaData),
            areasByMonsterProperties
        )
        assertEquals(expected, actual)
        assertEquals(
            setOf(area1Data) + setOf(area2Data),
            actual.getAreasForMonsterClassId("fetishshaman2", NORMAL, REGULAR)
        )
    }

    @Test
    fun getDisplayName() {
        assertEquals("DurielsHouse-ENGLISH-name", durielArea.getDisplayName(stubTranslations, Language.ENGLISH))
    }
}