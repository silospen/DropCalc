package com.silospen.dropcalc.resource

import com.silospen.dropcalc.*
import com.silospen.dropcalc.Language.ENGLISH
import com.silospen.dropcalc.Language.FRENCH
import com.silospen.dropcalc.items.ItemLibrary
import com.silospen.dropcalc.monsters.Monster
import com.silospen.dropcalc.monsters.MonsterLibrary
import com.silospen.dropcalc.treasureclasses.TreasureClassLibrary
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock

class MetadataResourceTest {
    private val itemLibrary = ItemLibrary(
        emptyList(),
        emptyList(),
        listOf(
            Item("item1", "item_1", ItemQuality.UNIQUE, armor1, 1, 1, false, null),
            Item("item2", "item_2", ItemQuality.WHITE, weapon2, 1, 1, false, null)
        )
    )
    private val metadataResource = VersionedMetadataResource(
        MonsterLibrary(
            setOf(
                Monster(
                    "1",
                    "1",
                    "1",
                    skeletonMonsterClass,
                    area1Data,
                    Difficulty.NORMAL,
                    MonsterType.SUPERUNIQUE,
                    "foo",
                    false,
                    1,
                    false,
                    TreasureClassType.REGULAR
                ),
                Monster(
                    "1",
                    "1",
                    "1",
                    skeletonMonsterClass,
                    area2Data,
                    Difficulty.NORMAL,
                    MonsterType.SUPERUNIQUE,
                    "foo",
                    false,
                    2,
                    false,
                    TreasureClassType.REGULAR
                ),
                Monster(
                    "2",
                    "2",
                    "2",
                    skeletonMonsterClass,
                    area2Data,
                    Difficulty.NORMAL,
                    MonsterType.SUPERUNIQUE,
                    "foo",
                    false,
                    3,
                    false,
                    TreasureClassType.REGULAR
                ),
                Monster(
                    "2d",
                    "2",
                    "2",
                    skeletonMonsterClass,
                    area2Data,
                    Difficulty.NORMAL,
                    MonsterType.SUPERUNIQUE,
                    "foo",
                    true,
                    3,
                    false,
                    TreasureClassType.DESECRATED_REGULAR
                ),
                Monster(
                    "1",
                    "1",
                    "1",
                    skeletonMonsterClass,
                    area2Data,
                    Difficulty.NORMAL,
                    MonsterType.BOSS,
                    "foo",
                    false,
                    4,
                    false,
                    TreasureClassType.REGULAR
                ),
                Monster(
                    "1q",
                    "1",
                    "1",
                    skeletonMonsterClass,
                    area2Data,
                    Difficulty.NORMAL,
                    MonsterType.BOSS,
                    "foo",
                    false,
                    4,
                    false,
                    TreasureClassType.QUEST
                )
            ), TreasureClassLibrary(emptyList(), itemLibrary)
        ),
        itemLibrary, stubTranslations, true, mock()
    )

    @Test
    fun monsters() {
        assertEquals(
            listOf(MetadataResponse("1-ENGLISH-name", "1"), MetadataResponse("2-ENGLISH-name", "2")),
            metadataResource.getMonsters(Difficulty.NORMAL, MonsterType.SUPERUNIQUE, false, true, ENGLISH)
        )
        assertEquals(
            listOf(MetadataResponse("1-FRENCH-name", "1"), MetadataResponse("2-FRENCH-name", "2")),
            metadataResource.getMonsters(Difficulty.NORMAL, MonsterType.SUPERUNIQUE, false, true, FRENCH)
        )
        assertEquals(
            listOf(MetadataResponse("1-ENGLISH-name", "1"), MetadataResponse("2-ENGLISH-name (d)", "2d")),
            metadataResource.getMonsters(Difficulty.NORMAL, MonsterType.SUPERUNIQUE, true, true, ENGLISH)
        )
        assertEquals(
            listOf(MetadataResponse("1-ENGLISH-name", "1")),
            metadataResource.getMonsters(Difficulty.NORMAL, MonsterType.BOSS, false, false, ENGLISH)
        )
        assertEquals(
            listOf(MetadataResponse("1-ENGLISH-name", "1"), MetadataResponse("1-ENGLISH-name (q)", "1q")),
            metadataResource.getMonsters(Difficulty.NORMAL, MonsterType.BOSS, false, true, ENGLISH)
        )
    }

    @Test
    fun items() {
        assertEquals(
            emptyList<MetadataResponse>(),
            metadataResource.getItems(ApiItemQuality.UNIQUE, ItemVersion.ELITE, ENGLISH)
        )
        assertEquals(
            listOf(MetadataResponse("item_2-ENGLISH-name", "item2")),
            metadataResource.getItems(ApiItemQuality.WHITE, ItemVersion.ELITE, ENGLISH)
        )
        assertEquals(
            listOf(MetadataResponse("item_2-FRENCH-name", "item2")),
            metadataResource.getItems(ApiItemQuality.WHITE, ItemVersion.ELITE, FRENCH)
        )
        assertEquals(
            listOf(MetadataResponse("item_2-ENGLISH-name", "item2")),
            metadataResource.getItems(ApiItemQuality.WHITE, null, ENGLISH)
        )
        assertEquals(
            emptyList<MetadataResponse>(),
            metadataResource.getItems(ApiItemQuality.MAGIC, null, ENGLISH)
        )
    }
}