package com.silospen.dropcalc.parser

import com.google.common.collect.HashBasedTable
import com.silospen.dropcalc.*
import com.silospen.dropcalc.Difficulty.*
import com.silospen.dropcalc.MonsterClassType.BOSS
import com.silospen.dropcalc.MonsterType.*
import com.silospen.dropcalc.reader.readTsv
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.File

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
        val skeletonClassProperties = getSkeletonClassProperties()
        val durielClassProperties = getDurielClassProperties()
        val putridDefilerClassProperties = getPutridDefilerClassProperties()
        val fetishShamanClassProperties = getFetishShamanClassProperties()
        val radamentClassProperties = getRadamentClassProperties()

        val expected = setOf(
            MonsterClass("skeleton1", monsterClassProperties = skeletonClassProperties),
            MonsterClass("duriel", monsterClassType = BOSS, monsterClassProperties = durielClassProperties),
            MonsterClass(
                "putriddefiler2",
                monsterClassType = BOSS,
                monsterClassProperties = putridDefilerClassProperties
            ),
            MonsterClass(
                "fetishshaman2",
                minionIds = setOf("fetish2", "fetishblow2"),
                monsterClassProperties = fetishShamanClassProperties
            ),
            MonsterClass(
                "radament",
                minionIds = setOf("skeleton4"),
                monsterClassType = BOSS,
                monsterClassProperties = radamentClassProperties
            )
        )
        assertEquals(expected, actual)
    }

    private fun getSkeletonClassProperties(): HashBasedTable<Difficulty, MonsterType, MonsterClassProperty> {
        val properties = HashBasedTable.create<Difficulty, MonsterType, MonsterClassProperty>()
        properties.put(NORMAL, REGULAR, MonsterClassProperty(2, tc("Act 1 H2H A")))
        properties.put(NORMAL, CHAMPION, MonsterClassProperty(2, tc("Act 1 Champ A")))
        properties.put(NORMAL, UNIQUE, MonsterClassProperty(2, tc("Act 1 Unique A")))

        properties.put(NIGHTMARE, REGULAR, MonsterClassProperty(37, tc("Act 1 (N) H2H A")))
        properties.put(NIGHTMARE, CHAMPION, MonsterClassProperty(37, tc("Act 1 (N) Champ A")))
        properties.put(NIGHTMARE, UNIQUE, MonsterClassProperty(37, tc("Act 1 (N) Unique A")))

        properties.put(HELL, REGULAR, MonsterClassProperty(68, tc("Act 1 (H) H2H A")))
        properties.put(HELL, CHAMPION, MonsterClassProperty(68, tc("Act 1 (H) Champ A")))
        properties.put(HELL, UNIQUE, MonsterClassProperty(68, tc("Act 1 (H) Unique A")))
        return properties
    }

    private fun getDurielClassProperties(): HashBasedTable<Difficulty, MonsterType, MonsterClassProperty> {
        val properties = HashBasedTable.create<Difficulty, MonsterType, MonsterClassProperty>()
        properties.put(NORMAL, REGULAR, MonsterClassProperty(22, tc("Duriel")))
        properties.put(NORMAL, CHAMPION, MonsterClassProperty(22, tc("Duriel")))
        properties.put(NORMAL, UNIQUE, MonsterClassProperty(22, tc("Duriel")))
        properties.put(NORMAL, QUEST, MonsterClassProperty(22, tc("Durielq")))

        properties.put(NIGHTMARE, REGULAR, MonsterClassProperty(55, tc("Duriel (N)")))
        properties.put(NIGHTMARE, CHAMPION, MonsterClassProperty(55, tc("Duriel (N)")))
        properties.put(NIGHTMARE, UNIQUE, MonsterClassProperty(55, tc("Duriel (N)")))
        properties.put(NIGHTMARE, QUEST, MonsterClassProperty(55, tc("Durielq (N)")))

        properties.put(HELL, REGULAR, MonsterClassProperty(88, tc("Duriel (H)")))
        properties.put(HELL, CHAMPION, MonsterClassProperty(88, tc("Duriel (H)")))
        properties.put(HELL, UNIQUE, MonsterClassProperty(88, tc("Duriel (H)")))
        properties.put(HELL, QUEST, MonsterClassProperty(88, tc("Durielq (H)")))
        return properties
    }

    private fun getPutridDefilerClassProperties(): HashBasedTable<Difficulty, MonsterType, MonsterClassProperty> {
        val properties = HashBasedTable.create<Difficulty, MonsterType, MonsterClassProperty>()
        properties.put(NORMAL, REGULAR, MonsterClassProperty(37, tc("Act 5 Cast A")))
        properties.put(NORMAL, CHAMPION, MonsterClassProperty(37, tc("Act 5 Champ A")))
        properties.put(NORMAL, UNIQUE, MonsterClassProperty(37, tc("Act 5 Unique A")))

        properties.put(NIGHTMARE, REGULAR, MonsterClassProperty(62, tc("Act 5 (N) Cast A")))
        properties.put(NIGHTMARE, CHAMPION, MonsterClassProperty(62, tc("Act 5 (N) Champ A")))
        properties.put(NIGHTMARE, UNIQUE, MonsterClassProperty(62, tc("Act 5 (N) Unique A")))

        properties.put(HELL, REGULAR, MonsterClassProperty(81, tc("Act 5 (H) Cast A")))
        properties.put(HELL, CHAMPION, MonsterClassProperty(81, tc("Act 5 (H) Champ A")))
        properties.put(HELL, UNIQUE, MonsterClassProperty(81, tc("Act 5 (H) Unique A")))
        return properties
    }

    private fun getFetishShamanClassProperties(): HashBasedTable<Difficulty, MonsterType, MonsterClassProperty> {
        val properties = HashBasedTable.create<Difficulty, MonsterType, MonsterClassProperty>()
        properties.put(NORMAL, REGULAR, MonsterClassProperty(22, tc("Act 3 Cast A")))
        properties.put(NORMAL, CHAMPION, MonsterClassProperty(22, tc("Act 3 Champ A")))
        properties.put(NORMAL, UNIQUE, MonsterClassProperty(22, tc("Act 3 Unique A")))

        properties.put(NIGHTMARE, REGULAR, MonsterClassProperty(49, tc("Act 3 (N) Cast A")))
        properties.put(NIGHTMARE, CHAMPION, MonsterClassProperty(49, tc("Act 3 (N) Champ A")))
        properties.put(NIGHTMARE, UNIQUE, MonsterClassProperty(49, tc("Act 3 (N) Unique A")))

        properties.put(HELL, REGULAR, MonsterClassProperty(80, tc("Act 3 (H) Cast A")))
        properties.put(HELL, CHAMPION, MonsterClassProperty(80, tc("Act 3 (H) Champ A")))
        properties.put(HELL, UNIQUE, MonsterClassProperty(80, tc("Act 3 (H) Unique A")))
        return properties
    }

    private fun getRadamentClassProperties(): HashBasedTable<Difficulty, MonsterType, MonsterClassProperty> {
        val properties = HashBasedTable.create<Difficulty, MonsterType, MonsterClassProperty>()
        properties.put(NORMAL, REGULAR, MonsterClassProperty(16, tc("Radament")))
        properties.put(NORMAL, CHAMPION, MonsterClassProperty(16, tc("Radament")))
        properties.put(NORMAL, UNIQUE, MonsterClassProperty(16, tc("Radament")))

        properties.put(NIGHTMARE, REGULAR, MonsterClassProperty(49, tc("Radament (N)")))
        properties.put(NIGHTMARE, CHAMPION, MonsterClassProperty(49, tc("Radament (N)")))
        properties.put(NIGHTMARE, UNIQUE, MonsterClassProperty(49, tc("Radament (N)")))

        properties.put(HELL, REGULAR, MonsterClassProperty(83, tc("Radament (H)")))
        properties.put(HELL, CHAMPION, MonsterClassProperty(83, tc("Radament (H)")))
        properties.put(HELL, UNIQUE, MonsterClassProperty(83, tc("Radament (H)")))
        return properties
    }

    private fun tc(name: String) = TreasureClass(name, 0, TreasureClassProperties(picks = 1), emptySet())
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

fun getResource(name: String) = File(object {}.javaClass.getResource("/$name")!!.toURI())
