package com.silospen.dropcalc

import com.google.common.collect.HashBasedTable
import com.google.common.collect.ImmutableTable
import com.silospen.dropcalc.Difficulty.*
import com.silospen.dropcalc.MonsterType.*
import com.silospen.dropcalc.monsters.Monster
import com.silospen.dropcalc.translations.Translations
import java.util.*

private fun getSkeletonClassProperties(): HashBasedTable<Difficulty, TreasureClassType, String> {
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
    return properties
}

private fun getDurielClassProperties(): HashBasedTable<Difficulty, TreasureClassType, String> {
    val properties = HashBasedTable.create<Difficulty, TreasureClassType, String>()
    properties.put(NORMAL, TreasureClassType.REGULAR, "Duriel")
    properties.put(NORMAL, TreasureClassType.CHAMPION, "Duriel")
    properties.put(NORMAL, TreasureClassType.UNIQUE, "Duriel")
    properties.put(NORMAL, TreasureClassType.QUEST, "Durielq")

    properties.put(NIGHTMARE, TreasureClassType.REGULAR, "Duriel (N)")
    properties.put(NIGHTMARE, TreasureClassType.CHAMPION, "Duriel (N)")
    properties.put(NIGHTMARE, TreasureClassType.UNIQUE, "Duriel (N)")
    properties.put(NIGHTMARE, TreasureClassType.QUEST, "Durielq (N)")

    properties.put(HELL, TreasureClassType.REGULAR, "Duriel (H)")
    properties.put(HELL, TreasureClassType.CHAMPION, "Duriel (H)")
    properties.put(HELL, TreasureClassType.UNIQUE, "Duriel (H)")
    properties.put(HELL, TreasureClassType.QUEST, "Durielq (H)")
    return properties
}

private fun getPutridDefilerClassProperties(): HashBasedTable<Difficulty, TreasureClassType, String> {
    val properties = HashBasedTable.create<Difficulty, TreasureClassType, String>()
    properties.put(NORMAL, TreasureClassType.REGULAR, "Act 5 Cast A")
    properties.put(NORMAL, TreasureClassType.CHAMPION, "Act 5 Champ A")
    properties.put(NORMAL, TreasureClassType.UNIQUE, "Act 5 Unique A")

    properties.put(NIGHTMARE, TreasureClassType.REGULAR, "Act 5 (N) Cast A")
    properties.put(NIGHTMARE, TreasureClassType.CHAMPION, "Act 5 (N) Champ A")
    properties.put(NIGHTMARE, TreasureClassType.UNIQUE, "Act 5 (N) Unique A")

    properties.put(HELL, TreasureClassType.REGULAR, "Act 5 (H) Cast A")
    properties.put(HELL, TreasureClassType.CHAMPION, "Act 5 (H) Champ A")
    properties.put(HELL, TreasureClassType.UNIQUE, "Act 5 (H) Unique A")
    return properties
}

private fun getFetishShamanClassProperties(): HashBasedTable<Difficulty, TreasureClassType, String> {
    val properties = HashBasedTable.create<Difficulty, TreasureClassType, String>()
    properties.put(NORMAL, TreasureClassType.REGULAR, "Act 3 Cast A")
    properties.put(NORMAL, TreasureClassType.CHAMPION, "Act 3 Champ A")
    properties.put(NORMAL, TreasureClassType.UNIQUE, "Act 3 Unique A")

    properties.put(NIGHTMARE, TreasureClassType.REGULAR, "Act 3 (N) Cast A")
    properties.put(NIGHTMARE, TreasureClassType.CHAMPION, "Act 3 (N) Champ A")
    properties.put(NIGHTMARE, TreasureClassType.UNIQUE, "Act 3 (N) Unique A")

    properties.put(HELL, TreasureClassType.REGULAR, "Act 3 (H) Cast A")
    properties.put(HELL, TreasureClassType.CHAMPION, "Act 3 (H) Champ A")
    properties.put(HELL, TreasureClassType.UNIQUE, "Act 3 (H) Unique A")
    return properties
}

private fun getRadamentClassProperties(): HashBasedTable<Difficulty, TreasureClassType, String> {
    val properties = HashBasedTable.create<Difficulty, TreasureClassType, String>()
    properties.put(NORMAL, TreasureClassType.REGULAR, "Radament")
    properties.put(NORMAL, TreasureClassType.CHAMPION, "Radament")
    properties.put(NORMAL, TreasureClassType.UNIQUE, "Radament")

    properties.put(NIGHTMARE, TreasureClassType.REGULAR, "Radament (N)")
    properties.put(NIGHTMARE, TreasureClassType.CHAMPION, "Radament (N)")
    properties.put(NIGHTMARE, TreasureClassType.UNIQUE, "Radament (N)")

    properties.put(HELL, TreasureClassType.REGULAR, "Radament (H)")
    properties.put(HELL, TreasureClassType.CHAMPION, "Radament (H)")
    properties.put(HELL, TreasureClassType.UNIQUE, "Radament (H)")
    return properties
}

private val skeletonClassProperties = getSkeletonClassProperties()
private val durielClassProperties = getDurielClassProperties()
private val putridDefilerClassProperties = getPutridDefilerClassProperties()
private val fetishShamanClassProperties = getFetishShamanClassProperties()
private val radamentClassProperties = getRadamentClassProperties()

val skeletonMonsterClass = MonsterClass(
    "skeleton1",
    "Skeleton-name",
    minionIds = setOf("skeleton1"),
    monsterClassTreasureClasses = skeletonClassProperties,
    monsterLevels = levelsPerDifficulty(2, 37, 68)
)
val durielMonsterClass =
    MonsterClass(
        "duriel",
        "Duriel-name",
        minionIds = setOf("duriel"),
        isBoss = true,
        monsterClassTreasureClasses = durielClassProperties,
        monsterLevels = levelsPerDifficulty(22, 55, 88)
    )
val putridDefilerMonsterClass = MonsterClass(
    "putriddefiler2",
    "Putrid Defiler2-name",
    minionIds = setOf("putriddefiler2"),
    isBoss = true,
    monsterClassTreasureClasses = putridDefilerClassProperties,
    monsterLevels = levelsPerDifficulty(37, 62, 81)
)
val fetishShamanMonsterClass = MonsterClass(
    "fetishshaman2",
    "FetishShaman-name",
    minionIds = setOf("fetish2", "fetishblow2"),
    monsterClassTreasureClasses = fetishShamanClassProperties,
    monsterLevels = levelsPerDifficulty(22, 49, 80)
)
val radamentMonsterClass = MonsterClass(
    "radament",
    "Radament-name",
    minionIds = setOf("skeleton4"),
    isBoss = true,
    monsterClassTreasureClasses = radamentClassProperties,
    monsterLevels = levelsPerDifficulty(16, 49, 83)
)
val monsterClassTestData = setOf(
    skeletonMonsterClass,
    durielMonsterClass,
    putridDefilerMonsterClass,
    fetishShamanMonsterClass,
    radamentMonsterClass
)

val area1Data = Area(
    "area-1",
    "my-area-1",
    levelsPerDifficulty(normal = 5),
    ImmutableTable.builder<Difficulty, MonsterType, Set<String>>()
        .put(NORMAL, REGULAR, setOf("skeleton1", "fetishshaman2"))
        .put(NORMAL, UNIQUE, setOf("skeleton1"))
        .build()
)
val area2Data = Area(
    "area-2",
    "my-area-2",
    levelsPerDifficulty(normal = 12, hell = 83),
    ImmutableTable.builder<Difficulty, MonsterType, Set<String>>()
        .put(NORMAL, REGULAR, setOf("fetishshaman2"))
        .put(HELL, CHAMPION, setOf("fetishshaman2"))
        .build()
)

val bonebreakAreaData = Area(
    "bonebreak's area",
    "bonebreak's area name",
    levelsPerDifficulty(normal = 22, hell = 55),
    ImmutableTable.builder<Difficulty, MonsterType, Set<String>>()
        .put(NORMAL, SUPERUNIQUE, setOf("Bonebreak"))
        .put(HELL, SUPERUNIQUE, setOf("Bonebreak"))
        .build()
)

fun levelsPerDifficulty(
    normal: Int? = null,
    nightmare: Int? = null,
    hell: Int? = null
): EnumMap<Difficulty, Int> {
    val result = EnumMap<Difficulty, Int>(Difficulty::class.java)
    normal?.let { result.put(NORMAL, normal) }
    nightmare?.let { result.put(NIGHTMARE, nightmare) }
    hell?.let { result.put(HELL, hell) }
    return result
}

val areasTestData = listOf(
    area1Data,
    area2Data,
    bonebreakAreaData
)

val skeletonMonster = Monster(
    skeletonMonsterClass.id,
    skeletonMonsterClass.id,
    "Skeleton-name",
    skeletonMonsterClass,
    area1Data,
    NORMAL,
    REGULAR,
    skeletonMonsterClass.monsterClassTreasureClasses.getValue(NORMAL, TreasureClassType.REGULAR),
    false,
    2,
    false,
    TreasureClassType.REGULAR
)
val fetishShamanMonster1 = Monster(
    fetishShamanMonsterClass.id,
    fetishShamanMonsterClass.id,
    "FetishShaman-name",
    fetishShamanMonsterClass,
    area1Data,
    NORMAL,
    REGULAR,
    fetishShamanMonsterClass.monsterClassTreasureClasses.getValue(NORMAL, TreasureClassType.REGULAR),
    false,
    22,
    false,
    TreasureClassType.REGULAR
)
val fetishShamanMonster2 = Monster(
    fetishShamanMonsterClass.id,
    fetishShamanMonsterClass.id,
    "FetishShaman-name",
    fetishShamanMonsterClass,
    area2Data,
    NORMAL,
    REGULAR,
    fetishShamanMonsterClass.monsterClassTreasureClasses.getValue(NORMAL, TreasureClassType.REGULAR),
    false,
    22,
    false,
    TreasureClassType.REGULAR
)
val monstersTestData = setOf(
    Monster(
        "skeleton1:Bonebreak",
        "skeleton1:Bonebreak",
        "Skeleton-name (Bonebreak-name)",
        skeletonMonsterClass,
        bonebreakAreaData,
        NORMAL,
        MINION,
        skeletonMonsterClass.monsterClassTreasureClasses.getValue(NORMAL, TreasureClassType.REGULAR),
        false,
        5,
        false,
        TreasureClassType.REGULAR
    ),
    Monster(
        "skeleton1:Bonebreak",
        "skeleton1:Bonebreak",
        "Skeleton-name (Bonebreak-name)",
        skeletonMonsterClass,
        bonebreakAreaData,
        HELL,
        MINION,
        skeletonMonsterClass.monsterClassTreasureClasses.getValue(HELL, TreasureClassType.REGULAR),
        false,
        58,
        false,
        TreasureClassType.REGULAR
    ),
    Monster(
        "skeleton1:skeleton1",
        "skeleton1:skeleton1",
        "Skeleton-name (Skeleton-name)",
        skeletonMonsterClass,
        area1Data,
        NORMAL,
        MINION,
        skeletonMonsterClass.monsterClassTreasureClasses.getValue(NORMAL, TreasureClassType.REGULAR),
        false,
        5,
        false,
        TreasureClassType.REGULAR
    ),
    Monster(
        "Bonebreak",
        "Bonebreak",
        "Bonebreak-name",
        skeletonMonsterClass,
        bonebreakAreaData,
        NORMAL,
        SUPERUNIQUE,
        "Bonebreak TC",
        false,
        5,
        true,
        TreasureClassType.REGULAR
    ),
    Monster(
        "Bonebreak",
        "Bonebreak",
        "Bonebreak-name",
        skeletonMonsterClass,
        bonebreakAreaData,
        HELL,
        SUPERUNIQUE,
        "Bonebreak TC(H)",
        false,
        58,
        true,
        TreasureClassType.REGULAR
    ),
    skeletonMonster,
    Monster(
        skeletonMonsterClass.id,
        skeletonMonsterClass.id,
        "Skeleton-name",
        skeletonMonsterClass,
        area1Data,
        NORMAL,
        UNIQUE,
        skeletonMonsterClass.monsterClassTreasureClasses.getValue(NORMAL, TreasureClassType.UNIQUE),
        false,
        5,
        true,
        TreasureClassType.UNIQUE
    ),
    fetishShamanMonster1,
    fetishShamanMonster2,
    Monster(
        fetishShamanMonsterClass.id,
        fetishShamanMonsterClass.id,
        "FetishShaman-name",
        fetishShamanMonsterClass,
        area2Data,
        HELL,
        CHAMPION,
        fetishShamanMonsterClass.monsterClassTreasureClasses.getValue(HELL, TreasureClassType.CHAMPION),
        false,
        85,
        false,
        TreasureClassType.CHAMPION
    )
)

val armor1 = BaseItem(
    "item1",
    "Fancy Armor",
    ItemType("arm", "Armor", false, 2, setOf("armo")),
    ItemVersion.NORMAL,
    2,
    setOf("armo3")
)
val weapon1 = BaseItem(
    "item2",
    "Fancy Weapon",
    ItemType("wep", "Weapon", false, 4, setOf("weap", "mele")),
    ItemVersion.NORMAL,
    3,
    setOf("weap3", "mele3")
)
val weapon2 = BaseItem(
    "item3",
    "Another Fancy Weapon",
    ItemType("wep", "Weapon", false, 2, setOf("weap")),
    ItemVersion.ELITE,
    3,
    setOf("weap3")
)
val ring = BaseItem(
    "rin",
    "Fancy Ring",
    ItemType("rin", "Ring", false, 5, setOf("misc")),
    ItemVersion.NORMAL,
    2,
    setOf("misc3")
)
val uniqueRing = Item(
    "MyUniqueRing",
    "MyUniqueRing",
    ItemQuality.UNIQUE,
    ring,
    10,
    3,
    true,
    null
)

val stubTranslations = object : Translations {
    override fun getTranslationOrNull(key: String): String {
        return "$key-name"
    }
}