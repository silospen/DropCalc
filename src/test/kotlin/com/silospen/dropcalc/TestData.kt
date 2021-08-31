package com.silospen.dropcalc

import com.google.common.collect.HashBasedTable
import com.google.common.collect.ImmutableTable
import com.silospen.dropcalc.monsters.Monster
import java.util.*

private fun getSkeletonClassProperties(): HashBasedTable<Difficulty, MonsterType, MonsterClassProperty> {
    val properties = HashBasedTable.create<Difficulty, MonsterType, MonsterClassProperty>()
    properties.put(Difficulty.NORMAL, MonsterType.REGULAR, MonsterClassProperty(2, tc("Act 1 H2H A")))
    properties.put(Difficulty.NORMAL, MonsterType.CHAMPION, MonsterClassProperty(2 + 2, tc("Act 1 Champ A")))
    properties.put(Difficulty.NORMAL, MonsterType.UNIQUE, MonsterClassProperty(2 + 3, tc("Act 1 Unique A")))

    properties.put(Difficulty.NIGHTMARE, MonsterType.REGULAR, MonsterClassProperty(37, tc("Act 1 (N) H2H A")))
    properties.put(Difficulty.NIGHTMARE, MonsterType.CHAMPION, MonsterClassProperty(37 + 2, tc("Act 1 (N) Champ A")))
    properties.put(Difficulty.NIGHTMARE, MonsterType.UNIQUE, MonsterClassProperty(37 + 3, tc("Act 1 (N) Unique A")))

    properties.put(Difficulty.HELL, MonsterType.REGULAR, MonsterClassProperty(68, tc("Act 1 (H) H2H A")))
    properties.put(Difficulty.HELL, MonsterType.CHAMPION, MonsterClassProperty(68 + 2, tc("Act 1 (H) Champ A")))
    properties.put(Difficulty.HELL, MonsterType.UNIQUE, MonsterClassProperty(68 + 3, tc("Act 1 (H) Unique A")))
    return properties
}

private fun getDurielClassProperties(): HashBasedTable<Difficulty, MonsterType, MonsterClassProperty> {
    val properties = HashBasedTable.create<Difficulty, MonsterType, MonsterClassProperty>()
    properties.put(Difficulty.NORMAL, MonsterType.REGULAR, MonsterClassProperty(22, tc("Duriel")))
    properties.put(Difficulty.NORMAL, MonsterType.CHAMPION, MonsterClassProperty(22, tc("Duriel")))
    properties.put(Difficulty.NORMAL, MonsterType.UNIQUE, MonsterClassProperty(22, tc("Duriel")))
    properties.put(Difficulty.NORMAL, MonsterType.QUEST, MonsterClassProperty(22, tc("Durielq")))

    properties.put(Difficulty.NIGHTMARE, MonsterType.REGULAR, MonsterClassProperty(55, tc("Duriel (N)")))
    properties.put(Difficulty.NIGHTMARE, MonsterType.CHAMPION, MonsterClassProperty(55, tc("Duriel (N)")))
    properties.put(Difficulty.NIGHTMARE, MonsterType.UNIQUE, MonsterClassProperty(55, tc("Duriel (N)")))
    properties.put(Difficulty.NIGHTMARE, MonsterType.QUEST, MonsterClassProperty(55, tc("Durielq (N)")))

    properties.put(Difficulty.HELL, MonsterType.REGULAR, MonsterClassProperty(88, tc("Duriel (H)")))
    properties.put(Difficulty.HELL, MonsterType.CHAMPION, MonsterClassProperty(88, tc("Duriel (H)")))
    properties.put(Difficulty.HELL, MonsterType.UNIQUE, MonsterClassProperty(88, tc("Duriel (H)")))
    properties.put(Difficulty.HELL, MonsterType.QUEST, MonsterClassProperty(88, tc("Durielq (H)")))
    return properties
}

private fun getPutridDefilerClassProperties(): HashBasedTable<Difficulty, MonsterType, MonsterClassProperty> {
    val properties = HashBasedTable.create<Difficulty, MonsterType, MonsterClassProperty>()
    properties.put(Difficulty.NORMAL, MonsterType.REGULAR, MonsterClassProperty(37, tc("Act 5 Cast A")))
    properties.put(Difficulty.NORMAL, MonsterType.CHAMPION, MonsterClassProperty(37, tc("Act 5 Champ A")))
    properties.put(Difficulty.NORMAL, MonsterType.UNIQUE, MonsterClassProperty(37, tc("Act 5 Unique A")))

    properties.put(Difficulty.NIGHTMARE, MonsterType.REGULAR, MonsterClassProperty(62, tc("Act 5 (N) Cast A")))
    properties.put(Difficulty.NIGHTMARE, MonsterType.CHAMPION, MonsterClassProperty(62, tc("Act 5 (N) Champ A")))
    properties.put(Difficulty.NIGHTMARE, MonsterType.UNIQUE, MonsterClassProperty(62, tc("Act 5 (N) Unique A")))

    properties.put(Difficulty.HELL, MonsterType.REGULAR, MonsterClassProperty(81, tc("Act 5 (H) Cast A")))
    properties.put(Difficulty.HELL, MonsterType.CHAMPION, MonsterClassProperty(81, tc("Act 5 (H) Champ A")))
    properties.put(Difficulty.HELL, MonsterType.UNIQUE, MonsterClassProperty(81, tc("Act 5 (H) Unique A")))
    return properties
}

private fun getFetishShamanClassProperties(): HashBasedTable<Difficulty, MonsterType, MonsterClassProperty> {
    val properties = HashBasedTable.create<Difficulty, MonsterType, MonsterClassProperty>()
    properties.put(Difficulty.NORMAL, MonsterType.REGULAR, MonsterClassProperty(22, tc("Act 3 Cast A")))
    properties.put(Difficulty.NORMAL, MonsterType.CHAMPION, MonsterClassProperty(22 + 2, tc("Act 3 Champ A")))
    properties.put(Difficulty.NORMAL, MonsterType.UNIQUE, MonsterClassProperty(22 + 3, tc("Act 3 Unique A")))

    properties.put(Difficulty.NIGHTMARE, MonsterType.REGULAR, MonsterClassProperty(49, tc("Act 3 (N) Cast A")))
    properties.put(Difficulty.NIGHTMARE, MonsterType.CHAMPION, MonsterClassProperty(49 + 2, tc("Act 3 (N) Champ A")))
    properties.put(Difficulty.NIGHTMARE, MonsterType.UNIQUE, MonsterClassProperty(49 + 3, tc("Act 3 (N) Unique A")))

    properties.put(Difficulty.HELL, MonsterType.REGULAR, MonsterClassProperty(80, tc("Act 3 (H) Cast A")))
    properties.put(Difficulty.HELL, MonsterType.CHAMPION, MonsterClassProperty(80 + 2, tc("Act 3 (H) Champ A")))
    properties.put(Difficulty.HELL, MonsterType.UNIQUE, MonsterClassProperty(80 + 3, tc("Act 3 (H) Unique A")))
    return properties
}

private fun getRadamentClassProperties(): HashBasedTable<Difficulty, MonsterType, MonsterClassProperty> {
    val properties = HashBasedTable.create<Difficulty, MonsterType, MonsterClassProperty>()
    properties.put(Difficulty.NORMAL, MonsterType.REGULAR, MonsterClassProperty(16, tc("Radament")))
    properties.put(Difficulty.NORMAL, MonsterType.CHAMPION, MonsterClassProperty(16, tc("Radament")))
    properties.put(Difficulty.NORMAL, MonsterType.UNIQUE, MonsterClassProperty(16, tc("Radament")))

    properties.put(Difficulty.NIGHTMARE, MonsterType.REGULAR, MonsterClassProperty(49, tc("Radament (N)")))
    properties.put(Difficulty.NIGHTMARE, MonsterType.CHAMPION, MonsterClassProperty(49, tc("Radament (N)")))
    properties.put(Difficulty.NIGHTMARE, MonsterType.UNIQUE, MonsterClassProperty(49, tc("Radament (N)")))

    properties.put(Difficulty.HELL, MonsterType.REGULAR, MonsterClassProperty(83, tc("Radament (H)")))
    properties.put(Difficulty.HELL, MonsterType.CHAMPION, MonsterClassProperty(83, tc("Radament (H)")))
    properties.put(Difficulty.HELL, MonsterType.UNIQUE, MonsterClassProperty(83, tc("Radament (H)")))
    return properties
}

fun tc(name: String) = TreasureClass(name, 0, TreasureClassProperties(picks = 1), emptySet())

private val skeletonClassProperties = getSkeletonClassProperties()
private val durielClassProperties = getDurielClassProperties()
private val putridDefilerClassProperties = getPutridDefilerClassProperties()
private val fetishShamanClassProperties = getFetishShamanClassProperties()
private val radamentClassProperties = getRadamentClassProperties()

val skeletonMonsterClass = MonsterClass("skeleton1", monsterClassProperties = skeletonClassProperties)
val durielMonsterClass =
    MonsterClass("duriel", monsterClassType = MonsterClassType.BOSS, monsterClassProperties = durielClassProperties)
val putridDefilerMonsterClass = MonsterClass(
    "putriddefiler2",
    monsterClassType = MonsterClassType.BOSS,
    monsterClassProperties = putridDefilerClassProperties
)
val fetishShamanMonsterClass = MonsterClass(
    "fetishshaman2",
    minionIds = setOf("fetish2", "fetishblow2"),
    monsterClassProperties = fetishShamanClassProperties
)
val radamentMonsterClass = MonsterClass(
    "radament",
    minionIds = setOf("skeleton4"),
    monsterClassType = MonsterClassType.BOSS,
    monsterClassProperties = radamentClassProperties
)
val monsterClassTestdata = setOf(
    skeletonMonsterClass,
    durielMonsterClass,
    putridDefilerMonsterClass,
    fetishShamanMonsterClass,
    radamentMonsterClass
)

val area1Data = Area(
    "area-1",
    EnumMap(Difficulty::class.java),
    ImmutableTable.of(Difficulty.NORMAL, MonsterType.REGULAR, setOf("skeleton1", "fetishshaman2"))
)
val area2Data = Area(
    "area-2",
    EnumMap(Difficulty::class.java),
    ImmutableTable.builder<Difficulty, MonsterType, Set<String>>()
        .put(Difficulty.NORMAL, MonsterType.REGULAR, setOf("fetishshaman2"))
        .put(Difficulty.HELL, MonsterType.CHAMPION, setOf("fetishshaman2"))
        .build()
)
val areasTestData = listOf(
    area1Data,
    area2Data
)

val monstersTestData = setOf(
    Monster(skeletonMonsterClass, setOf(area1Data), MonsterType.REGULAR),
    Monster(fetishShamanMonsterClass, setOf(area1Data, area2Data), MonsterType.REGULAR),
    Monster(fetishShamanMonsterClass, setOf(area2Data), MonsterType.CHAMPION)
)