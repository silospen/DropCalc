package com.silospen.dropcalc.resource

import com.silospen.dropcalc.*
import com.silospen.dropcalc.items.ItemLibrary
import com.silospen.dropcalc.monsters.Monster
import com.silospen.dropcalc.monsters.MonsterLibrary
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MetadataResourceTest {
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
                )
            )
        ),
        ItemLibrary(
            emptyList(),
            emptyList(),
            listOf(
                Item("item1", "item_1", ItemQuality.UNIQUE, armor1, 1, 1),
                Item("item2", "item_2", ItemQuality.WHITE, weapon2, 1, 1)
            )
        )
    )

    @Test
    fun monsters() {
        assertEquals(
            listOf(MetadataResponse("1-name", "1"), MetadataResponse("2-name", "2")),
            metadataResource.getMonsters(Difficulty.NORMAL, MonsterType.SUPERUNIQUE, false)
        )
        assertEquals(
            listOf(MetadataResponse("1-name", "1"), MetadataResponse("2-name(d)", "2d")),
            metadataResource.getMonsters(Difficulty.NORMAL, MonsterType.SUPERUNIQUE, true)
        )
        assertEquals(
            listOf(MetadataResponse("1-name", "1")),
            metadataResource.getMonsters(Difficulty.NORMAL, MonsterType.BOSS, false)
        )
    }

    @Test
    fun items() {
        assertEquals(
            emptyList<MetadataResponse>(),
            metadataResource.getItems(ItemQuality.UNIQUE, ItemVersion.ELITE)
        )
        assertEquals(
            listOf(MetadataResponse("item_2", "item2")),
            metadataResource.getItems(ItemQuality.WHITE, ItemVersion.ELITE)
        )
        assertEquals(
            listOf(MetadataResponse("item_2", "item2")),
            metadataResource.getItems(ItemQuality.WHITE, null)
        )
        assertEquals(
            emptyList<MetadataResponse>(),
            metadataResource.getItems(ItemQuality.MAGIC, null)
        )
    }
}