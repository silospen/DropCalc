package com.silospen.dropcalc.files

import com.google.common.collect.HashBasedTable
import com.google.common.collect.ImmutableTable
import com.silospen.dropcalc.*
import com.silospen.dropcalc.Difficulty.*
import com.silospen.dropcalc.ItemClassification.*
import com.silospen.dropcalc.MonsterType.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import java.util.*

class UniqueItemsLineParserTest {
    @Test
    fun uniqueItemsParser() {
        val axeItemType = ItemType("axe", "Axe", WEAPON, false, 3)
        val haxBaseItem = BaseItem("hax", "hax-name", axeItemType, 1)
        val axeBaseItem = BaseItem("axe", "axe-name", axeItemType, 2)
        val jewelBaseItem = BaseItem("jew", "jew-name", ItemType("jew", "Jewel", MISC, false, 3), 3)
        val actual = readTsv(
            getResource("parsersTestData/uniqueItems.txt"),
            UniqueItemLineParser(
                stubTranslations, listOf(
                    haxBaseItem,
                    axeBaseItem,
                    jewelBaseItem
                )
            )
        )
        val expected = listOf(
            Item("The Gnasher", "The Gnasher-name", ItemQuality.UNIQUE, haxBaseItem, 7),
            Item("Deathspade", "Deathspade-name", ItemQuality.UNIQUE, axeBaseItem, 12),
            Item("Rainbow Facet", "Rainbow Facet-name", ItemQuality.UNIQUE, jewelBaseItem, 85),
        )
        assertEquals(expected, actual)
    }
}

class BaseItemLineParserTest {
    @Test
    fun weaponParser() {
        val axeItemType = ItemType("axe", "Axe", WEAPON, false, 3)
        val potionItemType = ItemType("tpot", "Potion", MISC, false, 3)
        val actual = readTsv(
            getResource("parsersTestData/weapons.txt"),
            BaseItemLineParser.forWeaponsTxt(
                stubTranslations, listOf(
                    axeItemType,
                    potionItemType,
                    ItemType("knif", "Knife", WEAPON, false, 3),
                )
            )
        )
        assertEquals(
            listOf(BaseItem("hax", "hax-name", axeItemType, 3), BaseItem("opl", "bopl-name", potionItemType, 4)),
            actual
        )
    }

    @Test
    fun armorParser() {
        val helmItemType = ItemType("helm", "Helm", ARMOR, false, 3)
        val actual = readTsv(
            getResource("parsersTestData/armor.txt"),
            BaseItemLineParser.forArmorTxt(
                stubTranslations, listOf(helmItemType)
            )
        )
        assertEquals(
            listOf(BaseItem("cap", "cap-name", helmItemType, 1)),
            actual
        )
    }

    @Test
    fun miscParser() {
        val elixirItemType = ItemType("elix", "Elixir", MISC, false, 3)
        val ringItemType = ItemType("ring", "Ring", MISC, false, 3)
        val actual = readTsv(
            getResource("parsersTestData/misc.txt"),
            BaseItemLineParser.forMiscTxt(
                stubTranslations, listOf(elixirItemType, ringItemType)
            )
        )
        assertEquals(
            listOf(BaseItem("elx", "elx-name", elixirItemType, 21), BaseItem("rin", "rin-name", ringItemType, 1)),
            actual
        )
    }
}

class ItemTypeParserTest {

    @Test
    fun itemTypeParser() {
        val actual = readTsv(
            getResource("parsersTestData/itemTypes.txt"),
            ItemTypeParser()
        )
        assertEquals(
            listOf(
                ItemType("shie", "Shield", ARMOR, false, 3),
                ItemType("tors", "Armor", ARMOR, false, 3),
                ItemType("gold", "Gold", MISC, false, 3),
                ItemType("aspe", "Amazon Spear", WEAPON, true, 1)
            ), actual
        )
    }
}

class MonstatsLineParserTest {
    @Test
    fun monstatsParser() {
        val actual = readTsv(
//            File("C:\\Users\\silos\\Downloads\\D2Files\\cleanTextFiles\\1.12a\\monstats.txt"),
            getResource("parsersTestData/monstats.txt"),
            MonstatsLineParser(stubTranslations)
        ).toSet()
        assertEquals(monsterClassTestData, actual)
    }
}

class SuperUniqueLineParserTest {
    @Test
    fun superUniquesParser() {
        val actual = readTsv(
//            File("C:\\Users\\silos\\Downloads\\D2Files\\cleanTextFiles\\1.12a\\SuperUniques.txt"),
            getResource("parsersTestData/superuniques.txt"),
            SuperUniqueLineParser(
                mapOf(
                    "Corpsefire" to "Act 1 - Cave 1",
                    "The Feature Creep" to "Act 4 - Lava 1",
                ), stubTranslations
            )
        ).toSet()
        val expected = setOf(
            SuperUniqueMonsterConfig(
                "The Feature Creep",
                "The Feature Creep-name",
                "Act 4 - Lava 1",
                "hephasto",
                false,
                mapOf(
                    NORMAL to "Haphesto",
                    NIGHTMARE to "Haphesto (N)",
                    HELL to "Haphesto (H)",
                )
            ),
            SuperUniqueMonsterConfig(
                "Corpsefire",
                "Corpsefire-name",
                "Act 1 - Cave 1",
                "zombie1",
                true,
                mapOf(
                    NORMAL to "Act 1 Super A",
                    NIGHTMARE to "Act 1 (N) Super A",
                    HELL to "Act 1 (H) Super A",
                )
            ),
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
            LevelsLineParser(
                stubTranslations,
                ImmutableTable.of("Act 5 - Throne Room", UNIQUE, setOf("hardcoded-mon-1", "hardcoded-mon-2"))
            )
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
        monsterClassIds.put(NORMAL, UNIQUE, umon + setOf("hardcoded-mon-1", "hardcoded-mon-2"))
        monsterClassIds.put(NIGHTMARE, UNIQUE, nmon + setOf("hardcoded-mon-1", "hardcoded-mon-2"))
        monsterClassIds.put(HELL, UNIQUE, nmon + setOf("hardcoded-mon-1", "hardcoded-mon-2"))
        val expected = setOf(
            Area(
                "Act 1 - Town",
                "Rogue Encampment-name",
                emptyMap(),
                ImmutableTable.of()
            ),
            Area(
                "Act 5 - Throne Room",
                "Throne of Destruction-name",
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
