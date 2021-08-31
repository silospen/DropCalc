package com.silospen.dropcalc.monsters

import com.silospen.dropcalc.*
import com.silospen.dropcalc.areas.AreasLibrary
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class MonsterLibraryTest {
    @Test
    fun test() {
        val monsterClassConfigs = monsterClassTestdata.toList()
        val areasLibrary: AreasLibrary = AreasLibrary.fromAreas(areasTestData)
        val actual = MonsterLibrary.fromConfig(
            monsterClassConfigs,
            areasLibrary
        )

        val expected = MonsterLibrary(monstersTestData)

        Assertions.assertEquals(expected, actual)
    }
}
