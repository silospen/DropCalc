package com.silospen.dropcalc.monsters

import com.silospen.dropcalc.*
import com.silospen.dropcalc.MonsterType.MINION
import com.silospen.dropcalc.MonsterType.SUPERUNIQUE
import com.silospen.dropcalc.areas.AreasLibrary
import com.silospen.dropcalc.treasureclasses.TreasureClassCalculator
import org.springframework.stereotype.Component

class MonsterLibrary(val monsters: Set<Monster>) {
    private val monstersByIdDifficultyType =
        monsters.groupBy { Triple(it.id, it.difficulty, it.type) }.mapValues { it.value.toSet() }

    companion object {
        fun fromConfig(
            monsterClassConfigs: List<MonsterClass>,
            superUniqueMonsterConfigs: List<SuperUniqueMonsterConfig>,
            monsterFactory: MonsterFactory
        ): MonsterLibrary {
            val monsterClassConfigsById = monsterClassConfigs.associateBy { it.id }
            val superUniqueConfigsById = superUniqueMonsterConfigs.associateBy { it.id }
            val monstersFromClassConfigs = monsterClassConfigs.flatMap { monsterClass ->
                monsterClass.monsterClassTreasureClasses.cellSet().flatMap {
                    monsterFactory.createMonster(monsterClass, it.rowKey!!, it.columnKey!!)
                }
            }
            val monstersFromSuperUniqueMonsterConfigs = superUniqueMonsterConfigs.flatMap { superUniqueMonsterConfig ->
                superUniqueMonsterConfig.treasureClasses.map { (difficulty, treasureClass) ->
                    monsterFactory.createSuperUniqueMonster(
                        superUniqueMonsterConfig,
                        monsterClassConfigsById.getValue(superUniqueMonsterConfig.monsterClassId),
                        difficulty,
                        treasureClass
                    )
                }
            }
            val minions = (monstersFromSuperUniqueMonsterConfigs + monstersFromClassConfigs)
                .filter {
                    it.type == MonsterType.UNIQUE ||
                            (it.type == SUPERUNIQUE && superUniqueConfigsById.getValue(it.id).hasMinions)
                }
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

@Component
class MonsterFactory(
    private val areasLibrary: AreasLibrary,
    private val treasureClassCalculator: TreasureClassCalculator
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
            "${monsterClass.name} (${parentMonster.name})",
            parentMonster.monsterClass,
            parentMonster.area,
            difficulty,
            MINION,
            getUpgradedTc(
                monsterClass.monsterClassTreasureClasses.getValue(
                    difficulty,
                    TreasureClassType.REGULAR
                ), level, difficulty
            ),
            level
        )
    }

    fun createSuperUniqueMonster(
        superUniqueMonsterConfig: SuperUniqueMonsterConfig,
        monsterClass: MonsterClass,
        difficulty: Difficulty,
        treasureClass: String
    ): Monster {
        val area = areasLibrary.getArea(superUniqueMonsterConfig.areaName)
        val level = constructLevel(monsterClass, difficulty, SUPERUNIQUE, area)
        return Monster(
            superUniqueMonsterConfig.id,
            superUniqueMonsterConfig.name,
            monsterClass,
            area,
            difficulty,
            SUPERUNIQUE,
            getUpgradedTc(treasureClass, level, difficulty),
            level
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
                    monsterClass.name + if (treasureClassType.idSuffix.isNotBlank()) " (${treasureClassType.idSuffix})" else "",
                    monsterClass,
                    it,
                    difficulty,
                    monsterType,
                    getUpgradedTc(
                        monsterClass.monsterClassTreasureClasses.getValue(difficulty, treasureClassType),
                        level,
                        difficulty
                    ),
                    level
                )
            }
        }
    }

    private fun getUpgradedTc(treasureClassName: String, monsterLevel: Int, difficulty: Difficulty) =
        treasureClassCalculator.changeTcBasedOnLevel(
            treasureClassCalculator.getTreasureClass(treasureClassName),
            monsterLevel,
            difficulty
        ).name

    private fun constructLevel(
        monsterClass: MonsterClass,
        difficulty: Difficulty,
        monsterType: MonsterType,
        area: Area
    ): Int {
        return (if (difficulty == Difficulty.NORMAL || monsterType == MonsterType.BOSS)
            monsterClass.monsterLevels.getValue(difficulty)
        else area.monsterLevels.getValue(difficulty)) + getLevelAdjustment(monsterType)
    }

    private fun getLevelAdjustment(monsterType: MonsterType): Int {
        if (monsterType == MonsterType.BOSS) return 0
        if (monsterType == MonsterType.CHAMPION) return 2
        if (monsterType == MonsterType.UNIQUE || monsterType == MINION) return 3
        return 0
    }
}