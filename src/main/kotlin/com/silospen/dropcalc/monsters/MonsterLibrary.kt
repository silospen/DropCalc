package com.silospen.dropcalc.monsters

import com.silospen.dropcalc.*
import com.silospen.dropcalc.areas.AreasLibrary

class MonsterLibrary(val monsters: Set<Monster>) {
    private val monstersByIdDifficultyType =
        monsters.groupBy { Triple(it.id, it.difficulty, it.type) }.mapValues { it.value.toSet() }

    companion object {
        fun fromConfig(
            monsterClassConfigs: List<MonsterClass>,
            superUniqueMonsterConfigs: List<SuperUniqueMonsterConfig>,
            areasLibrary: AreasLibrary
        ): MonsterLibrary {
            val monsterClassConfigsById = monsterClassConfigs.associateBy { it.id }
            val superUniqueConfigsById = superUniqueMonsterConfigs.associateBy { it.id }
            val monstersFromClassConfigs = monsterClassConfigs.flatMap { monsterClass ->
                monsterClass.monsterClassProperties.cellSet().flatMap {
                    createMonster(areasLibrary, monsterClass, it.rowKey!!, it.columnKey!!)
                }
            }
            val monstersFromSuperUniqueMonsterConfigs = superUniqueMonsterConfigs.flatMap { superUniqueMonsterConfig ->
                superUniqueMonsterConfig.treasureClasses.map { (difficulty, treasureClass) ->
                    Monster(
                        superUniqueMonsterConfig.id,
                        superUniqueMonsterConfig.name,
                        monsterClassConfigsById.getValue(superUniqueMonsterConfig.monsterClassId),
                        areasLibrary.getArea(superUniqueMonsterConfig.areaName),
                        difficulty,
                        MonsterType.SUPERUNIQUE,
                        treasureClass
                    )
                }
            }
            val minions = (monstersFromSuperUniqueMonsterConfigs + monstersFromClassConfigs)
                .filter {
                    it.type == MonsterType.UNIQUE ||
                            (it.type == MonsterType.SUPERUNIQUE && superUniqueConfigsById.getValue(it.id).hasMinions)
                }
                .flatMap { parentMonster ->
                    parentMonster.monsterClass.minionIds.map { minionId ->
                        val monsterClass = monsterClassConfigsById.getValue(minionId)
                        Monster(
                            "$minionId:${parentMonster.id}",
                            "${monsterClass.name} (${parentMonster.name})",
                            parentMonster.monsterClass,
                            parentMonster.area,
                            parentMonster.difficulty,
                            MonsterType.MINION,
                            monsterClass.monsterClassProperties.getValue(
                                parentMonster.difficulty,
                                TreasureClassType.REGULAR
                            )
                        )
                    }
                }

            return MonsterLibrary(
                (minions + monstersFromSuperUniqueMonsterConfigs + monstersFromClassConfigs).toSet()
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
                        "${monsterClass.id}${treasureClassType.idSuffix}",
                        monsterClass.name + if (treasureClassType.idSuffix.isNotBlank()) " (${treasureClassType.idSuffix})" else "",
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
        monstersByIdDifficultyType.getOrDefault(Triple(monsterClassId, difficulty, monsterType), emptySet())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MonsterLibrary

        if (monsters != other.monsters) return false
        if (monstersByIdDifficultyType != other.monstersByIdDifficultyType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = monsters.hashCode()
        result = 31 * result + monstersByIdDifficultyType.hashCode()
        return result
    }

    override fun toString(): String {
        return "MonsterLibrary(monsters=$monsters, monstersByIdDifficultyType=$monstersByIdDifficultyType)"
    }
}