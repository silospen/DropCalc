package com.silospen.dropcalc.areas

import com.google.common.collect.ImmutableTable
import com.google.common.collect.Table
import com.silospen.dropcalc.MonsterType
import com.silospen.dropcalc.MonsterType.BOSS

private val hardcodedBossAreas = ImmutableTable.builder<String, MonsterType, Set<String>>()
    .put("Act 1 - Catacombs 4", BOSS, setOf("andariel"))
    .put("Act 2 - Duriel's Lair", BOSS, setOf("duriel"))
    .put("Act 2 - Sewer 1 C", BOSS, setOf("radament"))
    .put("Act 3 - Mephisto 3", BOSS, setOf("mephisto"))
    .put("Act 4 - Diablo 1", BOSS, setOf("diablo"))
    .put("Act 2 - Arcane", BOSS, setOf("summoner"))
    .put("Act 4 - Mesa 2", BOSS, setOf("izual"))
    .put("Act 1 - Graveyard", BOSS, setOf("bloodraven"))
    .put("Act 1 - Tristram", BOSS, setOf("griswold"))
    .put("Act 5 - World Stone", BOSS, setOf("baalcrab"))
    .put("Act 5 - Temple 2", BOSS, setOf("putriddefiler1"))
    .put("Act 5 - Temple Boss", BOSS, setOf("putriddefiler1", "putriddefiler2", "nihlathakboss"))
    .put("Act 5 - Baal Temple 1", BOSS, setOf("putriddefiler2", "putriddefiler3"))
    .put("Act 5 - Baal Temple 3", BOSS, setOf("putriddefiler3", "putriddefiler4", "putriddefiler5"))
    .build()

val hardcodedAreas: Table<String, MonsterType, Set<String>> =
    ImmutableTable.builder<String, MonsterType, Set<String>>()
        .putAll(hardcodedBossAreas)
        .build()