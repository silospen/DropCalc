package com.silospen.dropcalc.areas

import com.google.common.collect.ImmutableTable
import com.silospen.dropcalc.Area
import com.silospen.dropcalc.Difficulty
import com.silospen.dropcalc.Difficulty.*
import com.silospen.dropcalc.MonsterType
import com.silospen.dropcalc.MonsterType.REGULAR

val hardcodedBossAreas: List<Area> = listOf(
    bossArea("Act 1 - Catacombs 4", "andariel"),
    bossArea("Act 2 - Duriel's Lair", "duriel"),
    bossArea("Act 2 - Sewer 1 C", "radament"),
    bossArea("Act 3 - Mephisto 3", "mephisto"),
    bossArea("Act 4 - Diablo 1", "diablo"),
    bossArea("Act 2 - Arcane", "summoner"),
    bossArea("Act 4 - Mesa 2", "izual"),
    bossArea("Act 1 - Graveyard", "bloodraven"),
    bossArea("Act 1 - Tristram", "griswold"),
    bossArea("Act 5 - Temple Boss", "nihlathakboss"),
    bossArea("Act 5 - World Stone", "baalcrab"),
    bossArea("Act 5 - Temple 2", "putriddefiler1"),
    bossArea("Act 5 - Temple Boss", "putriddefiler1"),
    bossArea("Act 5 - Baal Temple 1", "putriddefiler2"),
    bossArea("Act 5 - Temple Boss", "putriddefiler2"),
    bossArea("Act 5 - Baal Temple 1", "putriddefiler3"),
    bossArea("Act 5 - Baal Temple 3", "putriddefiler3"),
    bossArea("Act 5 - Baal Temple 3", "putriddefiler4"),
    bossArea("Act 5 - Baal Temple 3", "putriddefiler5"),
)

private fun bossArea(areaId: String, bossId: String) = Area(
    areaId,
    emptyMap(),
    ImmutableTable.builder<Difficulty, MonsterType, Set<String>>()
        .put(NORMAL, REGULAR, setOf(bossId))
        .put(NIGHTMARE, REGULAR, setOf(bossId))
        .put(HELL, REGULAR, setOf(bossId)).build()
)