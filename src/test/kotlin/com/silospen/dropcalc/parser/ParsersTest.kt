package com.silospen.dropcalc.parser

import com.silospen.dropcalc.*
import com.silospen.dropcalc.MonsterConfigType.BOSS
import com.silospen.dropcalc.reader.readTsv
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class ParsersTest {

    @Test
    fun monstatsParser() {
        val actual = readTsv(
//            File("C:\\Users\\silos\\Downloads\\D2Files\\cleanTextFiles\\1.12a\\monstats.txt"),
            getResource("parsersTestData/monstats.txt"),
            ::monstatsLineParser
        ).toSet()
        val expected = setOf(
            MonsterClass("skeleton1"),
            MonsterClass("duriel", hasQuestTreasureClass = true, monsterConfigType = BOSS),
            MonsterClass("putriddefiler2", monsterConfigType = BOSS),
            MonsterClass("fetishshaman2", setOf("fetish2", "fetishblow2")),
            MonsterClass("radament", setOf("skeleton4"), monsterConfigType = BOSS)
        )
        assertEquals(expected, actual)
    }

    @Test
    fun superUniquesParser() {
        val actual = readTsv(
//            File("C:\\Users\\silos\\Downloads\\D2Files\\cleanTextFiles\\1.12a\\SuperUniques.txt"),
            getResource("parsersTestData/superuniques.txt"),
            ::superUniqueLineParser
        ).toSet()
        val expected = setOf(
            SuperUniqueMonsterConfig("The Feature Creep", "hephasto", false),
            SuperUniqueMonsterConfig("Corpsefire", "zombie1", true)
        )
        assertEquals(expected, actual)
    }

    @Test
    fun treasureClassesParser() {
        val actual = readTsv(
//            File("C:\\Users\\silos\\Downloads\\D2Files\\cleanTextFiles\\1.12a\\TreasureClassEx.txt"),
            getResource("parsersTestData/treasureclass.txt"),
            ::treasureClassesParser
        ).toSet()
        val expected = setOf(
            TreasureClassConfig(
                "Act 3 Junk",
                TreasureClassProperties(picks = 1),
                setOf("Act 2 Junk" to 2, "Potion 3" to 8, "Misc 1" to 4, "Ammo" to 4),
            ),
            TreasureClassConfig(
                "Act 5 (N) Melee B",
                TreasureClassProperties(group = 2, level = 60, picks = 1),
                setOf(
                    "weap51" to 2,
                    "armo51" to 1,
                    "weap54" to 6,
                    "armo54" to 3,
                    "weap57" to 14,
                    "armo57" to 7,
                    "weap60" to 6,
                    "armo60" to 3,
                    "Act 5 (N) Melee A" to 1743
                ),
            ),
            TreasureClassConfig(
                "Summoner",
                TreasureClassProperties(picks = 5, unique = 900, set = 900, rare = 972, magic = 1024, noDrop = 19),
                setOf(
                    "\"gld,mul=1280\"" to 9,
                    "Act 2 Equip C" to 15,
                    "Act 3 Junk" to 5,
                    "Act 2 Good" to 3,
                    "Act 3 Magic A" to 4
                ),
            ),
        )
        assertEquals(
            expected, actual
        )
    }
}

fun getResource(name: String) = File(object {}.javaClass.getResource("/$name")!!.toURI())
