package com.silospen.dropcalc.monsters

import com.silospen.dropcalc.areas.AreasLibrary
import com.silospen.dropcalc.areasTestData
import com.silospen.dropcalc.monsterClassTestData
import com.silospen.dropcalc.monstersTestData
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MonsterLibraryTest {
    @Test
    fun test() {
        val monsterClassConfigs = monsterClassTestData.toList()
        val areasLibrary: AreasLibrary = AreasLibrary.fromAreas(areasTestData)
        val actual = MonsterLibrary.fromConfig(
            monsterClassConfigs,
            areasLibrary
        )
        val expected = MonsterLibrary(monstersTestData)
        assertEquals(expected, actual)
    }
}
