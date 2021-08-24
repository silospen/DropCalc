//package com.silospen.dropcalc.monsters
//
//import com.silospen.dropcalc.*
//
//class MonsterLibrary(
//    private val monsters: Set<Monster>
//) {
//
//    companion object {
//        fun fromConfig(
//            monsterConfigs: Set<MonsterClass>,
//            superUniqueMonsterConfigs: Set<SuperUniqueMonsterConfig>
//        ): MonsterLibrary {
//            return MonsterLibrary(monsterConfigs.map { createMonster(it) }.toSet())
//        }
//
//        private fun createMonster(config: MonsterClass) = Monster(config.id)
//    }
//}