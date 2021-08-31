package com.silospen.dropcalc

import com.google.common.collect.HashBasedTable
import com.google.common.collect.ImmutableTable
import com.silospen.dropcalc.Difficulty.HELL
import com.silospen.dropcalc.Difficulty.NORMAL
import com.silospen.dropcalc.MonsterType.*
import com.silospen.dropcalc.monsters.Monster
import java.util.*

private fun getSkeletonClassProperties(): HashBasedTable<Difficulty, MonsterType, MonsterClassProperty> {
    val properties = HashBasedTable.create<Difficulty, MonsterType, MonsterClassProperty>()
    properties.put(NORMAL, REGULAR, MonsterClassProperty(2, tc("Act 1 H2H A")))
    properties.put(NORMAL, CHAMPION, MonsterClassProperty(2 + 2, tc("Act 1 Champ A")))
    properties.put(NORMAL, UNIQUE, MonsterClassProperty(2 + 3, tc("Act 1 Unique A")))

    properties.put(Difficulty.NIGHTMARE, REGULAR, MonsterClassProperty(37, tc("Act 1 (N) H2H A")))
    properties.put(Difficulty.NIGHTMARE, CHAMPION, MonsterClassProperty(37 + 2, tc("Act 1 (N) Champ A")))
    properties.put(Difficulty.NIGHTMARE, UNIQUE, MonsterClassProperty(37 + 3, tc("Act 1 (N) Unique A")))

    properties.put(HELL, REGULAR, MonsterClassProperty(68, tc("Act 1 (H) H2H A")))
    properties.put(HELL, CHAMPION, MonsterClassProperty(68 + 2, tc("Act 1 (H) Champ A")))
    properties.put(HELL, UNIQUE, MonsterClassProperty(68 + 3, tc("Act 1 (H) Unique A")))
    return properties
}

private fun getDurielClassProperties(): HashBasedTable<Difficulty, MonsterType, MonsterClassProperty> {
    val properties = HashBasedTable.create<Difficulty, MonsterType, MonsterClassProperty>()
    properties.put(NORMAL, REGULAR, MonsterClassProperty(22, tc("Duriel")))
    properties.put(NORMAL, CHAMPION, MonsterClassProperty(22, tc("Duriel")))
    properties.put(NORMAL, UNIQUE, MonsterClassProperty(22, tc("Duriel")))
    properties.put(NORMAL, QUEST, MonsterClassProperty(22, tc("Durielq")))

    properties.put(Difficulty.NIGHTMARE, REGULAR, MonsterClassProperty(55, tc("Duriel (N)")))
    properties.put(Difficulty.NIGHTMARE, CHAMPION, MonsterClassProperty(55, tc("Duriel (N)")))
    properties.put(Difficulty.NIGHTMARE, UNIQUE, MonsterClassProperty(55, tc("Duriel (N)")))
    properties.put(Difficulty.NIGHTMARE, QUEST, MonsterClassProperty(55, tc("Durielq (N)")))

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

    properties.put(Difficulty.NIGHTMARE, REGULAR, MonsterClassProperty(62, tc("Act 5 (N) Cast A")))
    properties.put(Difficulty.NIGHTMARE, CHAMPION, MonsterClassProperty(62, tc("Act 5 (N) Champ A")))
    properties.put(Difficulty.NIGHTMARE, UNIQUE, MonsterClassProperty(62, tc("Act 5 (N) Unique A")))

    properties.put(HELL, REGULAR, MonsterClassProperty(81, tc("Act 5 (H) Cast A")))
    properties.put(HELL, CHAMPION, MonsterClassProperty(81, tc("Act 5 (H) Champ A")))
    properties.put(HELL, UNIQUE, MonsterClassProperty(81, tc("Act 5 (H) Unique A")))
    return properties
}

private fun getFetishShamanClassProperties(): HashBasedTable<Difficulty, MonsterType, MonsterClassProperty> {
    val properties = HashBasedTable.create<Difficulty, MonsterType, MonsterClassProperty>()
    properties.put(NORMAL, REGULAR, MonsterClassProperty(22, tc("Act 3 Cast A")))
    properties.put(NORMAL, CHAMPION, MonsterClassProperty(22 + 2, tc("Act 3 Champ A")))
    properties.put(NORMAL, UNIQUE, MonsterClassProperty(22 + 3, tc("Act 3 Unique A")))

    properties.put(Difficulty.NIGHTMARE, REGULAR, MonsterClassProperty(49, tc("Act 3 (N) Cast A")))
    properties.put(Difficulty.NIGHTMARE, CHAMPION, MonsterClassProperty(49 + 2, tc("Act 3 (N) Champ A")))
    properties.put(Difficulty.NIGHTMARE, UNIQUE, MonsterClassProperty(49 + 3, tc("Act 3 (N) Unique A")))

    properties.put(HELL, REGULAR, MonsterClassProperty(80, tc("Act 3 (H) Cast A")))
    properties.put(HELL, CHAMPION, MonsterClassProperty(80 + 2, tc("Act 3 (H) Champ A")))
    properties.put(HELL, UNIQUE, MonsterClassProperty(80 + 3, tc("Act 3 (H) Unique A")))
    return properties
}

private fun getRadamentClassProperties(): HashBasedTable<Difficulty, MonsterType, MonsterClassProperty> {
    val properties = HashBasedTable.create<Difficulty, MonsterType, MonsterClassProperty>()
    properties.put(NORMAL, REGULAR, MonsterClassProperty(16, tc("Radament")))
    properties.put(NORMAL, CHAMPION, MonsterClassProperty(16, tc("Radament")))
    properties.put(NORMAL, UNIQUE, MonsterClassProperty(16, tc("Radament")))

    properties.put(Difficulty.NIGHTMARE, REGULAR, MonsterClassProperty(49, tc("Radament (N)")))
    properties.put(Difficulty.NIGHTMARE, CHAMPION, MonsterClassProperty(49, tc("Radament (N)")))
    properties.put(Difficulty.NIGHTMARE, UNIQUE, MonsterClassProperty(49, tc("Radament (N)")))

    properties.put(HELL, REGULAR, MonsterClassProperty(83, tc("Radament (H)")))
    properties.put(HELL, CHAMPION, MonsterClassProperty(83, tc("Radament (H)")))
    properties.put(HELL, UNIQUE, MonsterClassProperty(83, tc("Radament (H)")))
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
    ImmutableTable.of(NORMAL, REGULAR, setOf("skeleton1", "fetishshaman2"))
)
val area2Data = Area(
    "area-2",
    EnumMap(Difficulty::class.java),
    ImmutableTable.builder<Difficulty, MonsterType, Set<String>>()
        .put(NORMAL, REGULAR, setOf("fetishshaman2"))
        .put(HELL, CHAMPION, setOf("fetishshaman2"))
        .build()
)
val areasTestData = listOf(
    area1Data,
    area2Data
)

val monstersTestData = setOf(
    Monster(skeletonMonsterClass, area1Data, NORMAL, REGULAR),
    Monster(fetishShamanMonsterClass, area1Data, NORMAL, REGULAR),
    Monster(fetishShamanMonsterClass, area2Data, NORMAL, REGULAR),
    Monster(fetishShamanMonsterClass, area2Data, HELL, CHAMPION)
)