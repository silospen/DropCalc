package com.silospen.dropcalc.areas

import com.google.common.collect.ImmutableTable
import com.silospen.dropcalc.MonsterType
import com.silospen.dropcalc.MonsterType.BOSS

val hardcodedBossAreas = ImmutableTable.builder<String, MonsterType, Set<String>>()
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

val hardcodedSuperUniqueAreas = mapOf(
    "Bishibosh" to "Act 1 - Wilderness 2",
    "Bonebreak" to "Act 1 - Crypt 1 A",
    "Coldcrow" to "Act 1 - Cave 2",
    "Rakanishu" to "Act 1 - Wilderness 3",
    "Treehead WoodFist" to "Act 1 - Wilderness 4",
    "Griswold" to "Act 1 - Tristram",
    "The Countess" to "Act 1 - Crypt 3 E",
    "Pitspawn Fouldog" to "Act 1 - Jail 2",
    "Flamespike the Crawler" to "Act 1 - Moo Moo Farm",
    "Boneash" to "Act 1 - Cathedral",
    "Radament" to "Act 2 - Sewer 1 C",
    "Bloodwitch the Wild" to "Act 2 - Tomb 2 Treasure",
    "Fangskin" to "Act 2 - Tomb 3 Treasure",
    "Beetleburst" to "Act 2 - Desert 3",
    "Leatherarm" to "Act 2 - Tomb 1 Treasure",
    "Coldworm the Burrower" to "Act 2 - Lair 1 Treasure",
    "Fire Eye" to "Act 2 - Basement 3",
    "Dark Elder" to "Act 2 - Desert 4",
    "The Summoner" to "Act 2 - Arcane",
    "Ancient Kaa the Soulless" to "Act 2 - Tomb Tal 1",
    "The Smith" to "Act 1 - Barracks",
    "Web Mage the Burning" to "Act 3 - Spider 2",
    "Witch Doctor Endugu" to "Act 3 - Dungeon 2 Treasure",
    "Stormtree" to "Act 3 - Kurast 1",
    "Sarina the Battlemaid" to "Act 3 - Temple 1",
    "Icehawk Riftwing" to "Act 3 - Sewer 1",
    "Ismail Vilehand" to "Act 3 - Travincal",
    "Geleb Flamefinger" to "Act 3 - Travincal",
    "Bremm Sparkfist" to "Act 3 - Mephisto 3",
    "Toorc Icefist" to "Act 3 - Travincal",
    "Wyand Voidfinger" to "Act 3 - Mephisto 3",
    "Maffer Dragonhand" to "Act 3 - Mephisto 3",
    "Winged Death" to "Act 1 - Moo Moo Farm",
    "The Tormentor" to "Act 1 - Moo Moo Farm",
    "Taintbreeder" to "Act 1 - Moo Moo Farm",
    "Riftwraith the Cannibal" to "Act 1 - Moo Moo Farm",
    "Infector of Souls" to "Act 4 - Diablo 1",
    "Lord De Seis" to "Act 4 - Diablo 1",
    "Grand Vizier of Chaos" to "Act 4 - Diablo 1",
    "The Cow King" to "Act 1 - Moo Moo Farm",
    "Corpsefire" to "Act 1 - Cave 1",
    "The Feature Creep" to "Act 4 - Lava 1",
    "Siege Boss" to "Act 5 - Siege 1",
    "Ancient Barbarian 1" to "Act 5 - Mountain Top",
    "Ancient Barbarian 2" to "Act 5 - Mountain Top",
    "Ancient Barbarian 3" to "Act 5 - Mountain Top",
    "Axe Dweller" to "Act 1 - Moo Moo Farm",
    "Bonesaw Breaker" to "Act 5 - Ice Cave 2",
    "Dac Farren" to "Act 5 - Siege 1",
    "Megaflow Rectifier" to "Act 5 - Barricade 1",
    "Eyeback Unleashed" to "Act 5 - Barricade 1",
    "Threash Socket" to "Act 5 - Barricade 2",
    "Pindleskin" to "Act 5 - Temple Entrance",
    "Snapchip Shatter" to "Act 5 - Ice Cave 3A",
    "Anodized Elite" to "Act 1 - Moo Moo Farm",
    "Vinvear Molech" to "Act 1 - Moo Moo Farm",
    "Sharp Tooth Sayer" to "Act 5 - Barricade 1",
    "Magma Torquer" to "Act 1 - Moo Moo Farm",
    "Blaze Ripper" to "Act 1 - Moo Moo Farm",
    "Frozenstein" to "Act 5 - Ice Cave 1A",
    "Nihlathak Boss" to "Act 5 - Temple Boss",
    "Baal Subject 1" to "Act 5 - Throne Room",
    "Baal Subject 2" to "Act 5 - Throne Room",
    "Baal Subject 3" to "Act 5 - Throne Room",
    "Baal Subject 4" to "Act 5 - Throne Room",
    "Baal Subject 5" to "Act 5 - Throne Room",
)