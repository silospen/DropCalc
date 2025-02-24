package com.silospen.dropcalc.files

import com.google.common.collect.HashBasedTable
import com.google.common.collect.ImmutableTable
import com.google.common.collect.Table
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

    private val axeItemType = ItemType("axe", "Axe", false, 3, setOf("weap"))
    private val haxBaseItem = BaseItem("hax", "hax-name", axeItemType, ItemVersion.NORMAL, 1, setOf("weap3"))
    private val axeBaseItem = BaseItem("axe", "axe-name", axeItemType, ItemVersion.NORMAL, 2, setOf("weap3"))
    private val jewelBaseItem =
        BaseItem(
            "jew",
            "jew-name",
            ItemType("jew", "Jewel", false, 3, setOf("misc", "jewl")),
            ItemVersion.NORMAL,
            3,
            setOf("misc3", "jewl3")
        )

    @Test
    fun uniqueItemsParser() {
        val actual = parseItems(Version.V1_12)
        val expected = listOf(
            Item("The Gnasher", "The Gnasher", ItemQuality.UNIQUE, haxBaseItem, 7, 1, false, null),
            Item("Deathspade", "Deathspade", ItemQuality.UNIQUE, axeBaseItem, 12, 1, false, null),
            Item("Rainbow Facet", "Rainbow Facet", ItemQuality.UNIQUE, jewelBaseItem, 85, 1, false, null),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun uniqueItemsParser_d2rEnabledFlag() {
        val actual = parseItems(Version.D2R_V1_0)
        val expected = listOf(
            Item("The Gnasher", "The Gnasher", ItemQuality.UNIQUE, haxBaseItem, 7, 1, false, null),
            Item("The Gnasherv2", "The Gnasherv2", ItemQuality.UNIQUE, haxBaseItem, 7, 1, true, null),
            Item("Deathspade", "Deathspade", ItemQuality.UNIQUE, axeBaseItem, 12, 1, false, null),
            Item("Rainbow Facet", "Rainbow Facet", ItemQuality.UNIQUE, jewelBaseItem, 85, 1, false, null),
        )
        assertEquals(expected, actual)
    }

    private fun parseItems(version: Version): List<Item> {
        val actual = readTsv(
            getResource("parsersTestData/uniqueItems.txt"),
            UniqueItemLineParser(listOf(haxBaseItem, axeBaseItem, jewelBaseItem), version)
        )
        return actual
    }
}

class SetItemsLineParserTest {
    @Test
    fun setItemsParser() {
        val shieldItemType = ItemType("lrg", "Large Shield", false, 3, setOf("armo"))
        val shieldBaseItem = BaseItem("lrg", "shield", shieldItemType, ItemVersion.NORMAL, 1, setOf("armo3"))
        val actual = readTsv(
            getResource("parsersTestData/setitems.txt"),
            SetItemLineParser(listOf(shieldBaseItem))
        )
        val expected = listOf(
            Item("Civerb's Ward", "Civerb's Ward", ItemQuality.SET, shieldBaseItem, 13, 7, false, null),
            Item(
                "Cow King's Hide",
                "Cow King's Hide",
                ItemQuality.SET,
                shieldBaseItem,
                13,
                7,
                false,
                "hellbovine"
            ),
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
                listOf(
                    axeItemType,
                    potionItemType,
                    ItemType("knif", "Knife", false, 3, setOf("weap")),
                ),
            )
        )
        assertEquals(
            listOf(
                BaseItem("hax", "hax", axeItemType, ItemVersion.NORMAL, 3, setOf("weap3")),
                BaseItem("opl", "bopl", potionItemType, ItemVersion.NORMAL, 4, setOf("misc6"))
            ),
            actual
        )
    }

    @Test
    fun armorParser() {
        val helmItemType = ItemType("helm", "Helm", false, 3, setOf("armo"))
        val actual = readTsv(
            getResource("parsersTestData/armor.txt"),
            BaseItemLineParser(listOf(helmItemType))
        )
        assertEquals(
            listOf(BaseItem("cap", "cap", helmItemType, ItemVersion.NORMAL, 1, setOf("armo3"))),
            actual
        )
    }

    @Test
    fun miscParser() {
        val elixirItemType = ItemType("elix", "Elixir", false, 3, setOf("misc"))
        val ringItemType = ItemType("ring", "Ring", false, 3, setOf("misc"))
        val actual = readTsv(
            getResource("parsersTestData/misc.txt"),
            BaseItemLineParser(listOf(elixirItemType, ringItemType), ItemVersion.NORMAL)
        )
        assertEquals(
            listOf(
                BaseItem("elx", "elx", elixirItemType, ItemVersion.NORMAL, 21, setOf("misc21")),
                BaseItem("rin", "rin", ringItemType, ItemVersion.NORMAL, 1, setOf("misc3"))
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
            MonstatsLineParser()
        ).toSet()
        assertEquals(monsterClassTestData, actual)
    }

    @Test
    fun monstatsParser_desecrated() {

        fun skeletonClassProperties(): HashBasedTable<Difficulty, TreasureClassType, String> {
            val properties = HashBasedTable.create<Difficulty, TreasureClassType, String>()
            properties.put(NORMAL, TreasureClassType.REGULAR, "Act 1 H2H A")
            properties.put(NORMAL, TreasureClassType.CHAMPION, "Act 1 Champ A")
            properties.put(NORMAL, TreasureClassType.UNIQUE, "Act 1 Unique A")

            properties.put(NIGHTMARE, TreasureClassType.REGULAR, "Act 1 (N) H2H A")
            properties.put(NIGHTMARE, TreasureClassType.CHAMPION, "Act 1 (N) Champ A")
            properties.put(NIGHTMARE, TreasureClassType.UNIQUE, "Act 1 (N) Unique A")

            properties.put(HELL, TreasureClassType.REGULAR, "Act 1 (H) H2H A")
            properties.put(HELL, TreasureClassType.CHAMPION, "Act 1 (H) Champ A")
            properties.put(HELL, TreasureClassType.UNIQUE, "Act 1 (H) Unique A")
            properties.put(HELL, TreasureClassType.DESECRATED_CHAMPION, "Act 1 (H) Champ A Desecrated")
            properties.put(HELL, TreasureClassType.DESECRATED_UNIQUE, "Act 1 (H) Unique A Desecrated")
            return properties
        }

        fun durielClassProperties(): HashBasedTable<Difficulty, TreasureClassType, String> {
            val properties = HashBasedTable.create<Difficulty, TreasureClassType, String>()
            properties.put(NORMAL, TreasureClassType.REGULAR, "Duriel")
            properties.put(NORMAL, TreasureClassType.CHAMPION, "Duriel")
            properties.put(NORMAL, TreasureClassType.UNIQUE, "Duriel")
            properties.put(NORMAL, TreasureClassType.QUEST, "Durielq")
            properties.put(NORMAL, TreasureClassType.DESECRATED_REGULAR, "Duriel Desecrated A")
            properties.put(NORMAL, TreasureClassType.DESECRATED_CHAMPION, "Duriel Desecrated A")
            properties.put(NORMAL, TreasureClassType.DESECRATED_UNIQUE, "Duriel Desecrated A")

            properties.put(NIGHTMARE, TreasureClassType.REGULAR, "Duriel (N)")
            properties.put(NIGHTMARE, TreasureClassType.CHAMPION, "Duriel (N)")
            properties.put(NIGHTMARE, TreasureClassType.UNIQUE, "Duriel (N)")
            properties.put(NIGHTMARE, TreasureClassType.QUEST, "Durielq (N)")
            properties.put(NIGHTMARE, TreasureClassType.DESECRATED_REGULAR, "Duriel (N) Desecrated A")
            properties.put(NIGHTMARE, TreasureClassType.DESECRATED_CHAMPION, "Duriel (N) Desecrated A")
            properties.put(NIGHTMARE, TreasureClassType.DESECRATED_UNIQUE, "Duriel (N) Desecrated A")

            properties.put(HELL, TreasureClassType.REGULAR, "Duriel (H)")
            properties.put(HELL, TreasureClassType.CHAMPION, "Duriel (H)")
            properties.put(HELL, TreasureClassType.UNIQUE, "Duriel (H)")
            properties.put(HELL, TreasureClassType.QUEST, "Durielq (H)")
            properties.put(HELL, TreasureClassType.DESECRATED_REGULAR, "Duriel (H) Desecrated A")
            properties.put(HELL, TreasureClassType.DESECRATED_CHAMPION, "Duriel (H) Desecrated A")
            properties.put(HELL, TreasureClassType.DESECRATED_UNIQUE, "Duriel (H) Desecrated A")
            return properties
        }

        val actual = readTsv(
            getResource("parsersTestData/monstats_desecrated.txt"),
            MonstatsLineParser()
        ).toSet()
        assertEquals(
            setOf(
                MonsterClass(
                    "skeleton1",
                    "Skeleton",
                    minionIds = setOf("skeleton1"),
                    monsterClassTreasureClasses = skeletonClassProperties(),
                    monsterLevels = levelsPerDifficulty(2, 37, 68)
                ),
                MonsterClass(
                    "duriel",
                    "Duriel",
                    minionIds = setOf("duriel"),
                    isBoss = true,
                    monsterClassTreasureClasses = durielClassProperties(),
                    monsterLevels = levelsPerDifficulty(22, 55, 88)
                ),
            ), actual
        )
    }
}

class SuperUniqueLineParserTest {
    @Test
    fun superUniquesParser() {
        val actual = parseSuperuniqueConfigs("superuniques")
        val expected = generatedExpected(HashBasedTable.create(), HashBasedTable.create())
        assertEquals(expected, actual)
    }

    @Test
    fun superUniquesParser_desecrated() {
        val actual = parseSuperuniqueConfigs("superuniques_desecrated")
        val expected = generatedExpected(
            HashBasedTable.create<Difficulty, TreasureClassType, String>().apply {
                put(NORMAL, TreasureClassType.DESECRATED_REGULAR, "Haphesto Desecrated A")
                put(NIGHTMARE, TreasureClassType.DESECRATED_REGULAR, "Haphesto (N) Desecrated A")
                put(HELL, TreasureClassType.DESECRATED_REGULAR, "Haphesto (H) Desecrated A")
            },
            HashBasedTable.create<Difficulty, TreasureClassType, String>().apply {
                put(HELL, TreasureClassType.DESECRATED_REGULAR, "Act 1 (H) Super A Desecrated")
            }
        )
        assertEquals(expected, actual)
    }

    private fun generatedExpected(
        additionalHaphestoTcs: Table<Difficulty, TreasureClassType, String>,
        additionalCorpsefireTcs: Table<Difficulty, TreasureClassType, String>
    ): Set<SuperUniqueMonsterConfig> {
        val haphestoTcs = HashBasedTable.create<Difficulty, TreasureClassType, String>()
        haphestoTcs.put(NORMAL, TreasureClassType.REGULAR, "Haphesto")
        haphestoTcs.put(NIGHTMARE, TreasureClassType.REGULAR, "Haphesto (N)")
        haphestoTcs.put(HELL, TreasureClassType.REGULAR, "Haphesto (H)")
        haphestoTcs.putAll(additionalHaphestoTcs)
        val corpsefireTcs = HashBasedTable.create<Difficulty, TreasureClassType, String>()
        corpsefireTcs.put(NORMAL, TreasureClassType.REGULAR, "Act 1 Super A")
        corpsefireTcs.put(NIGHTMARE, TreasureClassType.REGULAR, "Act 1 (N) Super A")
        corpsefireTcs.put(HELL, TreasureClassType.REGULAR, "Act 1 (H) Super A")
        corpsefireTcs.putAll(additionalCorpsefireTcs)
        val expected = setOf(
            SuperUniqueMonsterConfig(
                "The Feature Creep",
                "The Feature Creep",
                "Act 4 - Lava 1",
                "hephasto",
                false,
                haphestoTcs
            ),
            SuperUniqueMonsterConfig(
                "Corpsefire",
                "Corpsefire",
                "Act 1 - Cave 1",
                "zombie1",
                true,
                corpsefireTcs
            ),
        )
        return expected
    }

    private fun parseSuperuniqueConfigs(filename: String): Set<SuperUniqueMonsterConfig> {
        val actual = readTsv(
            getResource("parsersTestData/$filename.txt"),
            SuperUniqueLineParser(
                mapOf(
                    "Corpsefire" to "Act 1 - Cave 1",
                    "The Feature Creep" to "Act 4 - Lava 1",
                )
            )
        ).toSet()
        return actual
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
                "Rogue Encampment",
                emptyMap(),
                ImmutableTable.of()
            ),
            Area(
                "Act 5 - Throne Room",
                "Throne of Destruction",
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
