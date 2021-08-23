package com.silospen.dropcalc.parser

import com.silospen.dropcalc.BossMonster
import com.silospen.dropcalc.RegularMonster
import com.silospen.dropcalc.SuperUniqueMonster
import com.silospen.dropcalc.reader.readTsv
import net.bytebuddy.implementation.bind.annotation.Super
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
            RegularMonster("skeleton1"),
            BossMonster("duriel", hasQuestTreasureClass = true),
            BossMonster("putriddefiler2"),
            RegularMonster("fetishshaman2", setOf("fetish2", "fetishblow2")),
            BossMonster("radament", setOf("skeleton4"))
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
            SuperUniqueMonster("The Feature Creep", "hephasto")
        )
        assertEquals(expected, actual)
    }

    private fun getResource(name: String) = File(object {}.javaClass.getResource("/$name")!!.toURI())
}