package com.silospen.dropcalc.parser

import com.silospen.dropcalc.MonsterConfig
import com.silospen.dropcalc.SuperUniqueMonsterConfig
import com.silospen.dropcalc.reader.readTsv
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class ParsersTest {

    @Test
    fun monstatsParser() {
        val actual = readTsv(
//            File("C:\\Users\\silos\\Downloads\\D2Files\\cleanTextFiles\\1.12a\\monstats.txt"),
            getResource("monstats.txt"),
            ::monstatsLineParser
        ).toSet()
        val expected = setOf(
            MonsterConfig("skeleton1"),
            MonsterConfig("duriel", hasQuestTreasureClass = true, isBoss = true),
            MonsterConfig("putriddefiler2", isBoss = true),
            MonsterConfig("fetishshaman2", setOf("fetish2", "fetishblow2")),
            MonsterConfig("radament", setOf("skeleton4"), isBoss = true)
        )
        assertEquals(expected, actual)
    }

    @Test
    fun superUniquesParser() {
        val actual = readTsv(
//            File("C:\\Users\\silos\\Downloads\\D2Files\\cleanTextFiles\\1.12a\\SuperUniques.txt"),
            getResource("superuniques.txt"),
            ::superUniqueLineParser
        ).toSet()
        val expected = setOf(
            SuperUniqueMonsterConfig("The Feature Creep", "hephasto", false),
            SuperUniqueMonsterConfig("Corpsefire", "zombie1", true)
        )
        assertEquals(expected, actual)
    }

    private fun getResource(name: String) = File(object {}.javaClass.getResource("/$name")!!.toURI())
}