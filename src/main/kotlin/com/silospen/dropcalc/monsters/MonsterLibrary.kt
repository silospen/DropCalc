package com.silospen.dropcalc.monsters

import com.silospen.dropcalc.*

class MonsterLibrary(monsters: Set<Monster>) {
    private val monstersByMonsterClassId = monsters.groupBy { it.monsterClass.id }.mapValues { it.value.toSet() }

    companion object {
        fun fromConfig(
            monsterClassConfigs: List<MonsterClass>,
            areasLibrary: AreasLibrary
        ): MonsterLibrary {
            return MonsterLibrary(
                monsterClassConfigs.flatMap { monsterClass ->
                    monsterClass.monsterClassProperties.cellSet().mapNotNull {
                        createMonster(areasLibrary, monsterClass, it.rowKey!!, it.columnKey!!)
                    }
                }.toSet()
            )
        }

        private fun createMonster(
            areasLibrary: AreasLibrary,
            monsterClass: MonsterClass,
            difficulty: Difficulty,
            monsterType: MonsterType
        ): Monster? {
            val areas = areasLibrary.getAreasForMonsterClassId(monsterClass.id, difficulty, monsterType)
            return if (areas.isEmpty()) null else Monster(
                monsterClass,
                areas,
                monsterType
            )
        }
    }

    fun getMonsterByClassId(monsterClassId: String) = monstersByMonsterClassId.getOrDefault(monsterClassId, emptySet())

    override fun toString(): String {
        return "MonsterLibrary(monstersByMonsterClassId=$monstersByMonsterClassId)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MonsterLibrary

        if (monstersByMonsterClassId != other.monstersByMonsterClassId) return false

        return true
    }

    override fun hashCode(): Int {
        return monstersByMonsterClassId.hashCode()
    }
}