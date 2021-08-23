package com.silospen.dropcalc.parser

import com.silospen.dropcalc.*
import com.silospen.dropcalc.reader.readTsv
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class ParsersTest {

    @Test
    fun monstatsParser() {
        val actual = readTsv(
            getResource("monstats.txt"),
            baseMonstatsLineParser
        ).toSet()
        val expected = setOf(
            RegularMonster("skeleton1"),
            BossMonster("duriel", true),
            BossMonster("putriddefiler2")
        )
        assertEquals(expected, actual)
    }

    @Test
    fun minionMonstatsParser() {
        val monstatsFile = File("C:\\Users\\silos\\Downloads\\D2Files\\cleanTextFiles\\1.12a\\monstats.txt")
        val actual = readTsv(
            monstatsFile,
            MinionMonstatsLineParser(
                MonsterLibrary(
                    readTsv(
                        monstatsFile,
                        baseMonstatsLineParser
                    ).toSet()
                )
            )
        ).flatten().toSet()

        println(actual)
    }

    private fun getResource(name: String) = File(object {}.javaClass.getResource("/$name")!!.toURI())
}