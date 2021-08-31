package com.silospen.dropcalc.files

import com.google.common.collect.HashBasedTable
import com.silospen.dropcalc.*
import com.silospen.dropcalc.Difficulty.*
import com.silospen.dropcalc.MonsterType.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.File
import java.util.*

class MonstatsLineParserTest {
    @Test
    fun monstatsParser() {
        val mockTreasureClassCalculator: TreasureClassCalculator = mock()
        whenever(mockTreasureClassCalculator.getTreasureClass(any())).thenAnswer { tc(it.getArgument(0)) }
        val actual = readTsv(
//            File("C:\\Users\\silos\\Downloads\\D2Files\\cleanTextFiles\\1.12a\\monstats.txt"),
            getResource("parsersTestData/monstats.txt"),
            MonstatsLineParser(mockTreasureClassCalculator)
        ).toSet()


        assertEquals(monsterClassTestdata, actual)
    }
}

class SuperUniqueLineParserTest {
    @Test
    fun superUniquesParser() {
        val actual = readTsv(
//            File("C:\\Users\\silos\\Downloads\\D2Files\\cleanTextFiles\\1.12a\\SuperUniques.txt"),
            getResource("parsersTestData/superuniques.txt"),
            SuperUniqueLineParser()
        ).toSet()
        val expected = setOf(
            SuperUniqueMonsterConfig("The Feature Creep", "hephasto", false),
            SuperUniqueMonsterConfig("Corpsefire", "zombie1", true)
        )
        assertEquals(expected, actual)
    }
}

class TreasureClassesLineParserTest {
    @Test
    fun treasureClassesParser() {
        val actual = readTsv(
//            File("C:\\Users\\silos\\Downloads\\D2Files\\cleanTextFiles\\1.12a\\TreasureClassEx.txt"),
            getResource("parsersTestData/treasureclass.txt"),
            TreasureClassesLineParser()
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

class LevelsLineParserTest {
    @Test
    fun levelsLineParser() {
        val actual = readTsv(
//            File("C:\\Users\\silos\\Downloads\\D2Files\\cleanTextFiles\\1.12a\\Levels.txt"),
            getResource("parsersTestData/levels.txt"),
            LevelsLineParser()
        ).toSet()

        val monsterClassIds = HashBasedTable.create<Difficulty, MonsterType, Set<String>>()
        val mon = setOf("bloodlord5", "succubuswitch3")
        val nmon = setOf(
            "bloodlord5",
            "succubuswitch5",
            "bonefetish7",
            "sandraider10",
            "willowisp7",
            "vampire8",
            "megademon5",
            "unraveler9",
            "dkmag2",
            "clawviper10"
        )
        val umon = setOf("bloodlord5", "succubuswitch4")
        monsterClassIds.put(NORMAL, REGULAR, mon)
        monsterClassIds.put(NIGHTMARE, REGULAR, nmon)
        monsterClassIds.put(HELL, REGULAR, nmon)
        monsterClassIds.put(NORMAL, CHAMPION, umon)
        monsterClassIds.put(NIGHTMARE, CHAMPION, nmon)
        monsterClassIds.put(HELL, CHAMPION, nmon)
        monsterClassIds.put(NORMAL, UNIQUE, umon)
        monsterClassIds.put(NIGHTMARE, UNIQUE, nmon)
        monsterClassIds.put(HELL, UNIQUE, nmon)
        val expected = setOf(
            Area(
                "Act 5 - Throne Room",
                EnumMap<Difficulty, Int>(Difficulty::class.java).apply {
                    put(NORMAL, 43)
                    put(NIGHTMARE, 66)
                    put(HELL, 85)
                },
                monsterClassIds
            )
        )
        assertEquals(expected, actual)
    }
}

fun getResource(name: String) = File(object {}.javaClass.getResource("/$name")!!.toURI())
