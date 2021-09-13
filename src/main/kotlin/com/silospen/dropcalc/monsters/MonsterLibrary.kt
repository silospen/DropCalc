package com.silospen.dropcalc.monsters

import com.silospen.dropcalc.*
import com.silospen.dropcalc.areas.AreasLibrary

class MonsterLibrary(monsters: Set<Monster>) {
    private val monstersByMonsterClassIdDifficultyType =
        monsters.groupBy { Triple(it.id, it.difficulty, it.type) }.mapValues { it.value.toSet() }

    companion object {
        fun fromConfig(
            monsterClassConfigs: List<MonsterClass>,
            superUniqueMonsterConfigs: List<SuperUniqueMonsterConfig>,
            areasLibrary: AreasLibrary
        ): MonsterLibrary {
            val monstersFromClassConfigs = monsterClassConfigs.flatMap { monsterClass ->
                monsterClass.monsterClassProperties.cellSet().flatMap {
                    createMonster(areasLibrary, monsterClass, it.rowKey!!, it.columnKey!!)
                }
            }
            val monsterClassConfigsById = monsterClassConfigs.associateBy { it.id }
            val monstersFromSuperUniqueMonsterConfigs = superUniqueMonsterConfigs.flatMap { superUniqueMonsterConfig ->
                superUniqueMonsterConfig.treasureClasses.map { (difficulty, treasureClass) ->
                    Monster(
                        superUniqueMonsterConfig.id,
                        monsterClassConfigsById.getValue(superUniqueMonsterConfig.monsterClassId),
                        areasLibrary.getArea(superUniqueMonsterConfig.areaName),
                        difficulty,
                        MonsterType.SUPERUNIQUE,
                        treasureClass
                    )
                }
            }
            return MonsterLibrary(
                (monstersFromSuperUniqueMonsterConfigs + monstersFromClassConfigs).toSet()
            )
        }

        private fun createMonster(
            areasLibrary: AreasLibrary,
            monsterClass: MonsterClass,
            difficulty: Difficulty,
            treasureClassType: TreasureClassType
        ): List<Monster> {
            return treasureClassType.validMonsterTypes.flatMap { monsterType ->
                areasLibrary.getAreasForMonsterClassId(monsterClass.id, difficulty, monsterType).map {
                    Monster(
                        monsterClass.id + treasureClassType.idSuffix,
                        monsterClass,
                        it,
                        difficulty,
                        monsterType,
                        monsterClass.monsterClassProperties.getValue(difficulty, treasureClassType)
                    )
                }
            }
        }
    }

    fun getMonsters(monsterClassId: String, difficulty: Difficulty, monsterType: MonsterType) =
        monstersByMonsterClassIdDifficultyType.getOrDefault(Triple(monsterClassId, difficulty, monsterType), emptySet())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MonsterLibrary

        if (monstersByMonsterClassIdDifficultyType != other.monstersByMonsterClassIdDifficultyType) return false

        return true
    }

    override fun hashCode(): Int {
        return monstersByMonsterClassIdDifficultyType.hashCode()
    }

    override fun toString(): String {
        return "MonsterLibrary(monstersByMonsterClassIdDifficultyType=$monstersByMonsterClassIdDifficultyType)"
    }

}