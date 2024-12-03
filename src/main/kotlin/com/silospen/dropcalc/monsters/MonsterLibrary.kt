package com.silospen.dropcalc.monsters

import com.silospen.dropcalc.*
import com.silospen.dropcalc.MonsterType.MINION
import com.silospen.dropcalc.MonsterType.SUPERUNIQUE
import com.silospen.dropcalc.areas.AreasLibrary
import com.silospen.dropcalc.treasureclasses.TreasureClassLibrary

class MonsterLibrary(val monsters: Set<Monster>) {
    private val monstersByIdDifficultyType =
        monsters.groupBy { Triple(it.id, it.difficulty, it.type) }.mapValues { it.value.toSet() }
    private val monstersByDifficultyType =
        monsters.groupBy { it.difficulty to it.type }.mapValues { it.value.toSet() }
    private val monstersByType =
        monsters.groupBy { it.type }.mapValues { it.value.toSet() }

    companion object {
        fun fromConfig(
            monsterClassConfigs: List<MonsterClass>,
            superUniqueMonsterConfigs: List<SuperUniqueMonsterConfig>,
            monsterFactory: MonsterFactory
        ): MonsterLibrary {
            val monsterClassConfigsById = monsterClassConfigs.associateBy { it.id }
            val monstersFromClassConfigs = monsterClassConfigs.flatMap { monsterClass ->
                monsterClass.monsterClassTreasureClasses.cellSet().flatMap {
                    monsterFactory.createMonster(monsterClass, it.rowKey!!, it.columnKey!!)
                }
            }
            val monstersFromSuperUniqueMonsterConfigs = superUniqueMonsterConfigs.flatMap { superUniqueMonsterConfig ->
                superUniqueMonsterConfig.treasureClasses.cellSet().map {
                    monsterFactory.createSuperUniqueMonster(
                        superUniqueMonsterConfig,
                        monsterClassConfigsById.getValue(superUniqueMonsterConfig.monsterClassId),
                        it.rowKey!!,
                        it.columnKey!!
                    )
                }
            }
            val minions = (monstersFromSuperUniqueMonsterConfigs + monstersFromClassConfigs)
                .filter { it.hasMinions }
                .flatMap { parentMonster ->
                    parentMonster.monsterClass.minionIds.map { minionId ->
                        val monsterClass = monsterClassConfigsById.getValue(minionId)
                        monsterFactory.createMinionMonster(minionId, parentMonster, monsterClass)
                    }
                }

            return MonsterLibrary(
                (minions + monstersFromSuperUniqueMonsterConfigs + monstersFromClassConfigs).toSet()
            )
        }
    }

    fun getMonsters(monsterClassId: String, difficulty: Difficulty, monsterType: MonsterType) =
        monstersByIdDifficultyType.getOrDefault(Triple(monsterClassId, difficulty, monsterType), emptySet())

    fun getMonsters(difficulty: Difficulty, monsterType: MonsterType) =
        monstersByDifficultyType.getOrDefault(difficulty to monsterType, emptySet())

    fun getMonsters(monsterType: MonsterType) = monstersByType.getOrDefault(monsterType, emptySet())

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

class MonsterFactory(
    private val areasLibrary: AreasLibrary,
    private val treasureClassLibrary: TreasureClassLibrary
) {
    fun createMinionMonster(
        minionId: String,
        parentMonster: Monster,
        monsterClass: MonsterClass
    ): Monster {
        val difficulty = parentMonster.difficulty
        val level = constructLevel(parentMonster.monsterClass, difficulty, MINION, parentMonster.area)
        return Monster(
            "$minionId:${parentMonster.id}",
            "$minionId:${parentMonster.rawId}",
            "${monsterClass.name} (${parentMonster.name})",
            monsterClass,
            parentMonster.area,
            difficulty,
            MINION,
            treasureClassLibrary.changeTcBasedOnLevel(
                monsterClass.monsterClassTreasureClasses.getValue(
                    difficulty,
                    TreasureClassType.REGULAR
                ), level, difficulty
            ),
            parentMonster.isDesecrated,
            level,
            false,
            TreasureClassType.REGULAR
        )
    }

    fun createSuperUniqueMonster(
        superUniqueMonsterConfig: SuperUniqueMonsterConfig,
        monsterClass: MonsterClass,
        difficulty: Difficulty,
        treasureClassType: TreasureClassType
    ): Monster {
        val area = areasLibrary.getArea(superUniqueMonsterConfig.areaName)
        val level = constructLevel(monsterClass, difficulty, SUPERUNIQUE, area)
        return Monster(
            "${superUniqueMonsterConfig.id}${treasureClassType.idSuffix}",
            superUniqueMonsterConfig.id,
            superUniqueMonsterConfig.name + if (treasureClassType.idSuffix.isNotBlank()) " (${treasureClassType.idSuffix})" else "",
            monsterClass,
            area,
            difficulty,
            SUPERUNIQUE,
            treasureClassLibrary.changeTcBasedOnLevel(
                superUniqueMonsterConfig.treasureClasses.getValue(
                    difficulty,
                    treasureClassType
                ), level, difficulty
            ),
            treasureClassType.isDesecrated,
            level,
            superUniqueMonsterConfig.hasMinions,
            treasureClassType
        )
    }

    fun createMonster(
        monsterClass: MonsterClass,
        difficulty: Difficulty,
        treasureClassType: TreasureClassType
    ): List<Monster> {
        return treasureClassType.validMonsterTypes.flatMap { monsterType ->
            areasLibrary.getAreasForMonsterClassId(monsterClass.id, difficulty, monsterType).map {
                val level = constructLevel(monsterClass, difficulty, monsterType, it)
                Monster(
                    "${monsterClass.id}${treasureClassType.idSuffix}",
                    monsterClass.id,
                    monsterClass.name + if (treasureClassType.idSuffix.isNotBlank()) " (${treasureClassType.idSuffix})" else "",
                    monsterClass,
                    it,
                    difficulty,
                    monsterType,
                    treasureClassLibrary.changeTcBasedOnLevel(
                        monsterClass.monsterClassTreasureClasses.getValue(difficulty, treasureClassType),
                        level,
                        difficulty
                    ),
                    treasureClassType.isDesecrated,
                    level,
                    monsterType == MonsterType.UNIQUE,
                    treasureClassType
                )
            }
        }
    }

    private fun constructLevel(
        monsterClass: MonsterClass,
        difficulty: Difficulty,
        monsterType: MonsterType,
        area: Area
    ): Int {
        return (if (difficulty == Difficulty.NORMAL || monsterType == MonsterType.BOSS || monsterType == MINION && monsterClass.isBoss)
            monsterClass.monsterLevels.getValue(difficulty)
        else area.monsterLevels.getValue(difficulty)) + getLevelAdjustment(monsterType, monsterClass)
    }

    private fun getLevelAdjustment(monsterType: MonsterType, monsterClass: MonsterClass): Int {
        if (monsterType == MonsterType.BOSS || (monsterType == MINION && monsterClass.isBoss)) return 0
        if (monsterType == MonsterType.CHAMPION) return 2
        if (monsterType == MonsterType.UNIQUE || monsterType == MINION || monsterType == SUPERUNIQUE) return 3
        return 0
    }
}