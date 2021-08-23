package com.silospen.dropcalc.parser

import com.silospen.dropcalc.BossMonster
import com.silospen.dropcalc.ChampionMonster
import com.silospen.dropcalc.RegularMonster
import com.silospen.dropcalc.UniqueMonster
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
            RegularMonster("skeleton1"),
            BossMonster("duriel", true),
            BossMonster("putriddefiler2")
        )
        assertEquals(expected, actual)
    }

    private fun getResource(name: String) = File(object {}.javaClass.getResource("/$name")!!.toURI())
}