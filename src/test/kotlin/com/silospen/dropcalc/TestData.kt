package com.silospen.dropcalc

import com.google.common.collect.HashBasedTable
import com.google.common.collect.ImmutableTable
import com.silospen.dropcalc.Difficulty.*
import com.silospen.dropcalc.MonsterType.*
import com.silospen.dropcalc.monsters.Monster
import com.silospen.dropcalc.translations.Translations
import java.util.*

private fun getSkeletonClassProperties(): HashBasedTable<Difficulty, TreasureClassType, TreasureClass> {
    val properties = HashBasedTable.create<Difficulty, TreasureClassType, TreasureClass>()
    properties.put(NORMAL, TreasureClassType.REGULAR, tc("Act 1 H2H A"))
    properties.put(NORMAL, TreasureClassType.CHAMPION, tc("Act 1 Champ A"))
    properties.put(NORMAL, TreasureClassType.UNIQUE, tc("Act 1 Unique A"))

    properties.put(NIGHTMARE, TreasureClassType.REGULAR, tc("Act 1 (N) H2H A"))
    properties.put(NIGHTMARE, TreasureClassType.CHAMPION, tc("Act 1 (N) Champ A"))
    properties.put(NIGHTMARE, TreasureClassType.UNIQUE, tc("Act 1 (N) Unique A"))

    properties.put(HELL, TreasureClassType.REGULAR, tc("Act 1 (H) H2H A"))
    properties.put(HELL, TreasureClassType.CHAMPION, tc("Act 1 (H) Champ A"))
    properties.put(HELL, TreasureClassType.UNIQUE, tc("Act 1 (H) Unique A"))
    return properties
}

private fun getDurielClassProperties(): HashBasedTable<Difficulty, TreasureClassType, TreasureClass> {
    val properties = HashBasedTable.create<Difficulty, TreasureClassType, TreasureClass>()
    properties.put(NORMAL, TreasureClassType.REGULAR, tc("Duriel"))
    properties.put(NORMAL, TreasureClassType.CHAMPION, tc("Duriel"))
    properties.put(NORMAL, TreasureClassType.UNIQUE, tc("Duriel"))
    properties.put(NORMAL, TreasureClassType.QUEST, tc("Durielq"))

    properties.put(NIGHTMARE, TreasureClassType.REGULAR, tc("Duriel (N)"))
    properties.put(NIGHTMARE, TreasureClassType.CHAMPION, tc("Duriel (N)"))
    properties.put(NIGHTMARE, TreasureClassType.UNIQUE, tc("Duriel (N)"))
    properties.put(NIGHTMARE, TreasureClassType.QUEST, tc("Durielq (N)"))

    properties.put(HELL, TreasureClassType.REGULAR, tc("Duriel (H)"))
    properties.put(HELL, TreasureClassType.CHAMPION, tc("Duriel (H)"))
    properties.put(HELL, TreasureClassType.UNIQUE, tc("Duriel (H)"))
    properties.put(HELL, TreasureClassType.QUEST, tc("Durielq (H)"))
    return properties
}

private fun getPutridDefilerClassProperties(): HashBasedTable<Difficulty, TreasureClassType, TreasureClass> {
    val properties = HashBasedTable.create<Difficulty, TreasureClassType, TreasureClass>()
    properties.put(NORMAL, TreasureClassType.REGULAR, tc("Act 5 Cast A"))
    properties.put(NORMAL, TreasureClassType.CHAMPION, tc("Act 5 Champ A"))
    properties.put(NORMAL, TreasureClassType.UNIQUE, tc("Act 5 Unique A"))

    properties.put(NIGHTMARE, TreasureClassType.REGULAR, tc("Act 5 (N) Cast A"))
    properties.put(NIGHTMARE, TreasureClassType.CHAMPION, tc("Act 5 (N) Champ A"))
    properties.put(NIGHTMARE, TreasureClassType.UNIQUE, tc("Act 5 (N) Unique A"))

    properties.put(HELL, TreasureClassType.REGULAR, tc("Act 5 (H) Cast A"))
    properties.put(HELL, TreasureClassType.CHAMPION, tc("Act 5 (H) Champ A"))
    properties.put(HELL, TreasureClassType.UNIQUE, tc("Act 5 (H) Unique A"))
    return properties
}

private fun getFetishShamanClassProperties(): HashBasedTable<Difficulty, TreasureClassType, TreasureClass> {
    val properties = HashBasedTable.create<Difficulty, TreasureClassType, TreasureClass>()
    properties.put(NORMAL, TreasureClassType.REGULAR, tc("Act 3 Cast A"))
    properties.put(NORMAL, TreasureClassType.CHAMPION, tc("Act 3 Champ A"))
    properties.put(NORMAL, TreasureClassType.UNIQUE, tc("Act 3 Unique A"))

    properties.put(NIGHTMARE, TreasureClassType.REGULAR, tc("Act 3 (N) Cast A"))
    properties.put(NIGHTMARE, TreasureClassType.CHAMPION, tc("Act 3 (N) Champ A"))
    properties.put(NIGHTMARE, TreasureClassType.UNIQUE, tc("Act 3 (N) Unique A"))

    properties.put(HELL, TreasureClassType.REGULAR, tc("Act 3 (H) Cast A"))
    properties.put(HELL, TreasureClassType.CHAMPION, tc("Act 3 (H) Champ A"))
    properties.put(HELL, TreasureClassType.UNIQUE, tc("Act 3 (H) Unique A"))
    return properties
}

private fun getRadamentClassProperties(): HashBasedTable<Difficulty, TreasureClassType, TreasureClass> {
    val properties = HashBasedTable.create<Difficulty, TreasureClassType, TreasureClass>()
    properties.put(NORMAL, TreasureClassType.REGULAR, tc("Radament"))
    properties.put(NORMAL, TreasureClassType.CHAMPION, tc("Radament"))
    properties.put(NORMAL, TreasureClassType.UNIQUE, tc("Radament"))

    properties.put(NIGHTMARE, TreasureClassType.REGULAR, tc("Radament (N)"))
    properties.put(NIGHTMARE, TreasureClassType.CHAMPION, tc("Radament (N)"))
    properties.put(NIGHTMARE, TreasureClassType.UNIQUE, tc("Radament (N)"))

    properties.put(HELL, TreasureClassType.REGULAR, tc("Radament (H)"))
    properties.put(HELL, TreasureClassType.CHAMPION, tc("Radament (H)"))
    properties.put(HELL, TreasureClassType.UNIQUE, tc("Radament (H)"))
    return properties
}

fun tc(name: String) = TreasureClass(name, 0, TreasureClassProperties(picks = 1), emptySet())

private val skeletonClassProperties = getSkeletonClassProperties()
private val durielClassProperties = getDurielClassProperties()
private val putridDefilerClassProperties = getPutridDefilerClassProperties()
private val fetishShamanClassProperties = getFetishShamanClassProperties()
private val radamentClassProperties = getRadamentClassProperties()

val skeletonMonsterClass = MonsterClass(
    "skeleton1",
    "Skeleton-name",
    minionIds = setOf("skeleton1"),
    monsterClassProperties = skeletonClassProperties,
    monsterLevels = levelsPerDifficulty(2, 37, 68)
)
val durielMonsterClass =
    MonsterClass(
        "duriel",
        "Duriel-name",
        minionIds = setOf("duriel"),
        isBoss = true,
        monsterClassProperties = durielClassProperties,
        monsterLevels = levelsPerDifficulty(22, 55, 88)
    )
val putridDefilerMonsterClass = MonsterClass(
    "putriddefiler2",
    "Putrid Defiler2-name",
    minionIds = setOf("putriddefiler2"),
    isBoss = true,
    monsterClassProperties = putridDefilerClassProperties,
    monsterLevels = levelsPerDifficulty(37, 62, 81)
)
val fetishShamanMonsterClass = MonsterClass(
    "fetishshaman2",
    "FetishShaman-name",
    minionIds = setOf("fetish2", "fetishblow2"),
    monsterClassProperties = fetishShamanClassProperties,
    monsterLevels = levelsPerDifficulty(22, 49, 80)
)
val radamentMonsterClass = MonsterClass(
    "radament",
    "Radament-name",
    minionIds = setOf("skeleton4"),
    isBoss = true,
    monsterClassProperties = radamentClassProperties,
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

val monstersTestData = setOf(
    Monster(
        "skeleton1:Bonebreak",
        "Skeleton-name (Bonebreak-name)",
        skeletonMonsterClass,
        bonebreakAreaData,
        NORMAL,
        MINION,
        skeletonMonsterClass.monsterClassProperties.getValue(NORMAL, TreasureClassType.REGULAR)
    ),
    Monster(
        "skeleton1:Bonebreak",
        "Skeleton-name (Bonebreak-name)",
        skeletonMonsterClass,
        bonebreakAreaData,
        HELL,
        MINION,
        skeletonMonsterClass.monsterClassProperties.getValue(HELL, TreasureClassType.REGULAR)
    ),
    Monster(
        "skeleton1:skeleton1",
        "Skeleton-name (Skeleton-name)",
        skeletonMonsterClass,
        area1Data,
        NORMAL,
        MINION,
        skeletonMonsterClass.monsterClassProperties.getValue(NORMAL, TreasureClassType.REGULAR)
    ),
    Monster(
        "Bonebreak",
        "Bonebreak-name",
        skeletonMonsterClass,
        bonebreakAreaData,
        NORMAL,
        SUPERUNIQUE,
        tc("Bonebreak TC")
    ),
    Monster(
        "Bonebreak",
        "Bonebreak-name",
        skeletonMonsterClass,
        bonebreakAreaData,
        HELL,
        SUPERUNIQUE,
        tc("Bonebreak TC(H)")
    ),
    Monster(
        skeletonMonsterClass.id,
        "Skeleton-name",
        skeletonMonsterClass,
        area1Data,
        NORMAL,
        REGULAR,
        skeletonMonsterClass.monsterClassProperties.getValue(NORMAL, TreasureClassType.REGULAR)
    ),
    Monster(
        skeletonMonsterClass.id,
        "Skeleton-name",
        skeletonMonsterClass,
        area1Data,
        NORMAL,
        UNIQUE,
        skeletonMonsterClass.monsterClassProperties.getValue(NORMAL, TreasureClassType.UNIQUE)
    ),
    Monster(
        fetishShamanMonsterClass.id,
        "FetishShaman-name",
        fetishShamanMonsterClass,
        area1Data,
        NORMAL,
        REGULAR,
        fetishShamanMonsterClass.monsterClassProperties.getValue(NORMAL, TreasureClassType.REGULAR)
    ),
    Monster(
        fetishShamanMonsterClass.id,
        "FetishShaman-name",
        fetishShamanMonsterClass,
        area2Data,
        NORMAL,
        REGULAR,
        fetishShamanMonsterClass.monsterClassProperties.getValue(NORMAL, TreasureClassType.REGULAR)
    ),
    Monster(
        fetishShamanMonsterClass.id,
        "FetishShaman-name",
        fetishShamanMonsterClass,
        area2Data,
        HELL,
        CHAMPION,
        fetishShamanMonsterClass.monsterClassProperties.getValue(HELL, TreasureClassType.CHAMPION)
    )
)

val stubTranslations = object : Translations {
    override fun getTranslationOrNull(key: String): String {
        return "$key-name"
    }
}