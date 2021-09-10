package com.silospen.dropcalc.areas

import com.google.common.collect.ImmutableTable
import com.silospen.dropcalc.Area
import com.silospen.dropcalc.Difficulty
import com.silospen.dropcalc.Difficulty.*
import com.silospen.dropcalc.MonsterType
import com.silospen.dropcalc.MonsterType.REGULAR

 private val hardcodedBossAreas = listOf(
    "Act 1 - Catacombs 4" to "andariel",
    "Act 2 - Duriel's Lair" to "duriel",
    "Act 2 - Sewer 1 C" to "radament",
    "Act 3 - Mephisto 3" to "mephisto",
    "Act 4 - Diablo 1" to "diablo",
    "Act 2 - Arcane" to "summoner",
    "Act 4 - Mesa 2" to "izual",
    "Act 1 - Graveyard" to "bloodraven",
    "Act 1 - Tristram" to "griswold",
    "Act 5 - Temple Boss" to "nihlathakboss",
    "Act 5 - World Stone" to "baalcrab",
    "Act 5 - Temple 2" to "putriddefiler1",
    "Act 5 - Temple Boss" to "putriddefiler1",
    "Act 5 - Baal Temple 1" to "putriddefiler2",
    "Act 5 - Temple Boss" to "putriddefiler2",
    "Act 5 - Baal Temple 1" to "putriddefiler3",
    "Act 5 - Baal Temple 3" to "putriddefiler3",
    "Act 5 - Baal Temple 3" to "putriddefiler4",
    "Act 5 - Baal Temple 3" to "putriddefiler5",
)

val hardcodedAreas = hardcodedBossAreas.groupBy({ it.first }) { it.second }
