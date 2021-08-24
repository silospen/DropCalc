//package com.silospen.dropcalc
//
//import com.silospen.dropcalc.monsters.MonsterFactory
//import com.silospen.dropcalc.monsters.MonsterLibrary
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.Test
//
//class MonsterLibraryTest {
//
//    @Test
//    fun monsterLibrary() {
//        val monsterFactory = MonsterFactory()
//        val monsterConfigs = setOf(
//            MonsterClass(
//                id = "my-reg-id",
//                hasQuestTreasureClass = false,
//            ),
//            MonsterClass(
//                id = "my-boss-with-treasure-id",
//                hasQuestTreasureClass = true,
//                isBoss = true
//            ),
//            MonsterClass(
//                id = "my-boss-id",
//                isBoss = true
//            )
//        )
//        val actual = MonsterLibrary.fromConfig(monsterFactory, monsterConfigs, emptySet())
//        val expected = MonsterLibrary(
//            regularMonsters = setOf(RegularMonster("my-reg-id")),
//            championMonsters = setOf(ChampionMonster("my-reg-id")),
//            uniqueMonsters = setOf(UniqueMonster("my-reg-id")),
//            superUniqueMonsters = emptySet(),
//            bossMonsters = setOf(BossMonster("my-boss-with-treasure-id"), BossMonster("my-boss-id")),
//            minionMonsters = emptySet()
//        )
//        assertEquals(expected, actual)
//    }
//}