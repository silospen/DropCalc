package com.silospen.dropcalc.resource

import com.silospen.dropcalc.*
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
                    "1-name",
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
                    "1-name",
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
                    "2-name",
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
                    "2-name(d)",
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
                    "1-name",
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
                    "1-name(q)",
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
        itemLibrary, mock()
    )

    @Test
    fun monsters() {
        assertEquals(
            listOf(MetadataResponse("1-name", "1"), MetadataResponse("2-name", "2")),
            metadataResource.getMonsters(Difficulty.NORMAL, MonsterType.SUPERUNIQUE, false, true)
        )
        assertEquals(
            listOf(MetadataResponse("1-name", "1"), MetadataResponse("2-name(d)", "2d")),
            metadataResource.getMonsters(Difficulty.NORMAL, MonsterType.SUPERUNIQUE, true, true)
        )
        assertEquals(
            listOf(MetadataResponse("1-name", "1")),
            metadataResource.getMonsters(Difficulty.NORMAL, MonsterType.BOSS, false, false)
        )
        assertEquals(
            listOf(MetadataResponse("1-name", "1"), MetadataResponse("1-name(q)", "1q")),
            metadataResource.getMonsters(Difficulty.NORMAL, MonsterType.BOSS, false, true)
        )
    }

    @Test
    fun items() {
        assertEquals(
            emptyList<MetadataResponse>(),
            metadataResource.getItems(ApiItemQuality.UNIQUE, ItemVersion.ELITE)
        )
        assertEquals(
            listOf(MetadataResponse("item_2", "item2")),
            metadataResource.getItems(ApiItemQuality.WHITE, ItemVersion.ELITE)
        )
        assertEquals(
            listOf(MetadataResponse("item_2", "item2")),
            metadataResource.getItems(ApiItemQuality.WHITE, null)
        )
        assertEquals(
            emptyList<MetadataResponse>(),
            metadataResource.getItems(ApiItemQuality.MAGIC, null)
        )
    }
}