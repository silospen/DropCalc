package com.silospen.dropcalc.monsters

import com.silospen.dropcalc.Difficulty
import com.silospen.dropcalc.MonsterClass

class MonsterClassLibrary(monsterClasses: Set<MonsterClass>) {

    private val monsterClassesById = monsterClasses.associateBy { it.id }

    fun getMinionClasses(monsterClassId: String): Set<String> =
        monsterClassesById.getValue(monsterClassId).minionIds

    fun getTreasureClass(monsterClassId: String, difficulty: Difficulty) {

    }

    fun getMonsterClass(monsterClassId: String) = monsterClassesById.getValue(monsterClassId)

}