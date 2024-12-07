package com.silospen.dropcalc.files

import com.google.common.collect.HashBasedTable
import com.google.common.collect.ImmutableTable
import com.silospen.dropcalc.*
import com.silospen.dropcalc.Difficulty.*
import com.silospen.dropcalc.ItemQualityRatios.Companion.EMPTY
import com.silospen.dropcalc.MonsterType.*
import com.silospen.dropcalc.items.ItemTypeCodeLibrary
import com.silospen.dropcalc.items.ItemTypeCodeWithParents
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class ItemRatioLineParserTest {
    @Test
    fun itemRatioParser() {
        val actual = readTsv(
            getResource("parsersTestData/itemratio.txt"),
            ItemRatioLineParser()
        )
        println()
        val expected = listOf(
            ItemRatio(
                isUber = true,
                isClassSpecific = false,
                mapOf(
                    ItemQuality.UNIQUE to ItemQualityModifiers(400, 1, 6400),
                    ItemQuality.RARE to ItemQualityModifiers(100, 2, 3200),
                    ItemQuality.SET to ItemQualityModifiers(160, 2, 5600),
                    ItemQuality.MAGIC to ItemQualityModifiers(34, 3, 192)
                )
            ),
            ItemRatio(
                isUber = false,
                isClassSpecific = true,
                mapOf(
                    ItemQuality.UNIQUE to ItemQualityModifiers(240, 3, 6400),
                    ItemQuality.RARE to ItemQualityModifiers(80, 3, 3200),
                    ItemQuality.SET to ItemQualityModifiers(120, 3, 5600),
                    ItemQuality.MAGIC to ItemQualityModifiers(17, 6, 192)
                )
            )
        )
        assertEquals(expected, actual)
    }
}

class UniqueItemsLineParserTest {
    @Test
    fun uniqueItemsParser() {
        val axeItemType = ItemType("axe", "Axe", false, 3, setOf("weap"))
        val haxBaseItem = BaseItem("hax", "hax-name", axeItemType, ItemVersion.NORMAL, 1, setOf("weap3"))
        val axeBaseItem = BaseItem("axe", "axe-name", axeItemType, ItemVersion.NORMAL, 2, setOf("weap3"))
        val jewelBaseItem =
            BaseItem(
                "jew",
                "jew-name",
                ItemType("jew", "Jewel", false, 3, setOf("misc", "jewl")),
                ItemVersion.NORMAL,
                3,
                setOf("misc3", "jewl3")
            )
        val actual = readTsv(
            getResource("parsersTestData/uniqueItems.txt"),
            UniqueItemLineParser(
                stubTranslations, listOf(
                    haxBaseItem,
                    axeBaseItem,
                    jewelBaseItem
                ), Version.V1_12
            )
        )
        val expected = listOf(
            Item("The Gnasher", "The Gnasher-name", ItemQuality.UNIQUE, haxBaseItem, 7, 1, false),
            Item("Deathspade", "Deathspade-name", ItemQuality.UNIQUE, axeBaseItem, 12, 1, false),
            Item("Rainbow Facet", "Rainbow Facet-name", ItemQuality.UNIQUE, jewelBaseItem, 85, 1, false),
        )
        assertEquals(expected, actual)
    }
}

class SetItemsLineParserTest {
    @Test
    fun setItemsParser() {
        val shieldItemType = ItemType("lrg", "Large Shield", false, 3, setOf("armo"))
        val shieldBaseItem = BaseItem("lrg", "shield-name", shieldItemType, ItemVersion.NORMAL, 1, setOf("armo3"))
        val actual = readTsv(
            getResource("parsersTestData/setitems.txt"),
            SetItemLineParser(
                stubTranslations, listOf(
                    shieldBaseItem
                )
            )
        )
        val expected = listOf(
            Item("Civerb's Ward", "Civerb's Ward-name", ItemQuality.SET, shieldBaseItem, 13, 7, false),
        )
        assertEquals(expected, actual)
    }
}

class BaseItemLineParserTest {
    @Test
    fun weaponParser() {
        val axeItemType = ItemType("axe", "Axe", false, 3, setOf("weap"))
        val potionItemType = ItemType("tpot", "Potion", false, 3, setOf("misc"))
        val actual = readTsv(
            getResource("parsersTestData/weapons.txt"),
            BaseItemLineParser(
                stubTranslations,
                listOf(
                    axeItemType,
                    potionItemType,
                    ItemType("knif", "Knife", false, 3, setOf("weap")),
                ),
            )
        )
        assertEquals(
            listOf(
                BaseItem("hax", "hax-name", axeItemType, ItemVersion.NORMAL, 3, setOf("weap3")),
                BaseItem("opl", "bopl-name", potionItemType, ItemVersion.NORMAL, 4, setOf("misc6"))
            ),
            actual
        )
    }

    @Test
    fun armorParser() {
        val helmItemType = ItemType("helm", "Helm", false, 3, setOf("armo"))
        val actual = readTsv(
            getResource("parsersTestData/armor.txt"),
            BaseItemLineParser(stubTranslations, listOf(helmItemType))
        )
        assertEquals(
            listOf(BaseItem("cap", "cap-name", helmItemType, ItemVersion.NORMAL, 1, setOf("armo3"))),
            actual
        )
    }

    @Test
    fun miscParser() {
        val elixirItemType = ItemType("elix", "Elixir", false, 3, setOf("misc"))
        val ringItemType = ItemType("ring", "Ring", false, 3, setOf("misc"))
        val actual = readTsv(
            getResource("parsersTestData/misc.txt"),
            BaseItemLineParser(stubTranslations, listOf(elixirItemType, ringItemType), ItemVersion.NORMAL)
        )
        assertEquals(
            listOf(
                BaseItem("elx", "elx-name", elixirItemType, ItemVersion.NORMAL, 21, setOf("misc21")),
                BaseItem("rin", "rin-name", ringItemType, ItemVersion.NORMAL, 1, setOf("misc3"))
            ),
            actual
        )
    }
}

class ItemTypeParserTest {

    @Test
    fun itemTypeParser() {
        val actual = readTsv(
            getResource("parsersTestData/itemTypes.txt"),
            ItemTypeParser(
                ItemTypeCodeLibrary(
                    listOf(
                        ItemTypeCodeWithParents("shie", setOf("armo")),
                        ItemTypeCodeWithParents("tors", setOf("armo")),
                        ItemTypeCodeWithParents("gold", setOf("misc")),
                        ItemTypeCodeWithParents("aspe", setOf("weap"))
                    )
                )
            )
        )
        assertEquals(
            listOf(
                ItemType("shie", "Shield", false, 3, setOf("armo", "shie")),
                ItemType("tors", "Armor", false, 3, setOf("armo", "tors")),
                ItemType("gold", "Gold", false, 3, setOf("misc", "gold"), canBeRare = false, canBeMagic = false),
                ItemType("aspe", "Amazon Spear", true, 1, setOf("weap", "aspe"))
            ), actual
        )
    }
}

class MonstatsLineParserTest {
    @Test
    fun monstatsParser() {
        val actual = readTsv(
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
            getResource("parsersTestData/superuniques.txt"),
            SuperUniqueLineParser(
                mapOf(
                    "Corpsefire" to "Act 1 - Cave 1",
                    "The Feature Creep" to "Act 4 - Lava 1",
                ), stubTranslations
            )
        ).toSet()
        val haphestoTcs = HashBasedTable.create<Difficulty, TreasureClassType, String>()
        haphestoTcs.put(NORMAL, TreasureClassType.REGULAR, "Haphesto")
        haphestoTcs.put(NIGHTMARE, TreasureClassType.REGULAR, "Haphesto (N)")
        haphestoTcs.put(HELL, TreasureClassType.REGULAR, "Haphesto (H)")
        val corpsefireTcs = HashBasedTable.create<Difficulty, TreasureClassType, String>()
        corpsefireTcs.put(NORMAL, TreasureClassType.REGULAR, "Act 1 Super A")
        corpsefireTcs.put(NIGHTMARE, TreasureClassType.REGULAR, "Act 1 (N) Super A")
        corpsefireTcs.put(HELL, TreasureClassType.REGULAR, "Act 1 (H) Super A")
        val expected = setOf(
            SuperUniqueMonsterConfig(
                "The Feature Creep",
                "The Feature Creep-name",
                "Act 4 - Lava 1",
                "hephasto",
                false,
                haphestoTcs
            ),
            SuperUniqueMonsterConfig(
                "Corpsefire",
                "Corpsefire-name",
                "Act 1 - Cave 1",
                "zombie1",
                true,
                corpsefireTcs
            ),
        )
        assertEquals(expected, actual)
    }
}

class TreasureClassesLineParserTest {
    @Test
    fun treasureClassesParser() {
        val actual = readTsv(
            getResource("parsersTestData/treasureclass.txt"),
            TreasureClassesLineParser()
        ).toSet()
        val expected = setOf(
            TreasureClassConfig(
                "Act 3 Junk",
                TreasureClassProperties(picks = 1, EMPTY),
                setOf("Act 2 Junk" to 2, "Potion 3" to 8, "Misc 1" to 4, "Ammo" to 4),
            ),
            TreasureClassConfig(
                "Act 5 (N) Melee B",
                TreasureClassProperties(group = 2, level = 60, picks = 1, itemQualityRatios = EMPTY),
                setOf(
                    "weap51" to 2,
                    "armo51" to 1,
                    "weap54" to 6,
                    "armo54" to 3,
                    "weap57" to 14,
                    "armo57" to 7,
                    "weap60" to 6,
                    "armo60" to 3,
                    "Act 5 (N) Melee A" to 1743,
                    "anItem" to 50
                ),
            ),
            TreasureClassConfig(
                "Summoner",
                TreasureClassProperties(
                    picks = 5,
                    itemQualityRatios = ItemQualityRatios(900, 900, 972, 1024),
                    noDrop = 19
                ),
                setOf(
                    "gld" to 9,
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
