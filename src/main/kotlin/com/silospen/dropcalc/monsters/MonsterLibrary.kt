package com.silospen.dropcalc.monsters

import com.silospen.dropcalc.*
import com.silospen.dropcalc.MonsterType.MINION
import com.silospen.dropcalc.MonsterType.SUPERUNIQUE
import com.silospen.dropcalc.areas.AreasLibrary
import com.silospen.dropcalc.treasureclasses.TreasureClassLibrary

data class MonsterLibraryTypeKey(val type: MonsterType, val desecrated: Boolean)
data class MonsterLibraryDifficultyTypeKey(val difficulty: Difficulty, val type: MonsterType, val desecrated: Boolean)
data class MonsterLibraryIdDifficultyTypeKey(
    val id: String,
    val difficulty: Difficulty,
    val type: MonsterType,
    val desecrated: Boolean
)

class MonsterLibrary(val monsters: Set<Monster>, private val treasureClassLibrary: TreasureClassLibrary) {
    private val monstersByType: Map<MonsterLibraryTypeKey, Set<Monster>> =
        constructMonsterMap { monster, isDesecrated -> MonsterLibraryTypeKey(monster.type, isDesecrated) }
    val monstersByDifficultyType: Map<MonsterLibraryDifficultyTypeKey, Set<Monster>> =
        constructMonsterMap { monster, isDesecrated ->
            MonsterLibraryDifficultyTypeKey(
                monster.difficulty,
                monster.type,
                isDesecrated
            )
        }
    private val monstersByIdDifficultyType: Map<MonsterLibraryIdDifficultyTypeKey, Set<Monster>> =
        constructMonsterMap { monster, isDesecrated ->
            MonsterLibraryIdDifficultyTypeKey(
                monster.id,
                monster.difficulty,
                monster.type,
                isDesecrated
            )
        }

    private fun <T> constructMonsterMap(keyCreator: (Monster, Boolean) -> T): Map<T, Set<Monster>> {
        val nonDesecratedSpawns = monsters.filter { !it.isDesecrated }.toSet()

        val desecratedSpawnsWithoutIdAndName = monsters
            .filter { it.isDesecrated }
            .associateBy {
                Triple(it.rawId, it.difficulty, it.type)
            }

        val possibleDesecratedSpawns = if (desecratedSpawnsWithoutIdAndName.isEmpty()) emptySet() else {
            (desecratedSpawnsWithoutIdAndName.values + nonDesecratedSpawns.filter {
                it.treasureClassType == TreasureClassType.QUEST || !desecratedSpawnsWithoutIdAndName.contains(
                    Triple(
                        it.rawId,
                        it.difficulty,
                        it.type
                    )
                )
            }).toSet()
        }

        return (nonDesecratedSpawns.groupBy { keyCreator(it, false) } +
                possibleDesecratedSpawns.groupBy { keyCreator(it, true) }).mapValues { it.value.toSet() }
    }

    companion object {
        fun fromConfig(
            monsterClassConfigs: List<MonsterClass>,
            superUniqueMonsterConfigs: List<SuperUniqueMonsterConfig>,
            monsterFactory: MonsterFactory,
            treasureClassLibrary: TreasureClassLibrary,
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
                (minions + monstersFromSuperUniqueMonsterConfigs + monstersFromClassConfigs).toSet(),
                treasureClassLibrary
            )
        }
    }

    fun getMonsters(
        monsterId: String,
        difficulty: Difficulty,
        monsterType: MonsterType,
        desecrated: Boolean,
        desecratedLevel: Int
    ) =
        monstersByIdDifficultyType.getOrDefault(
            MonsterLibraryIdDifficultyTypeKey(
                monsterId,
                difficulty,
                monsterType,
                desecrated
            ), emptySet()
        ).map { upgradeIfDesecrated(desecrated, it, desecratedLevel) }

    fun getMonsters(difficulty: Difficulty, monsterType: MonsterType, desecrated: Boolean, desecratedLevel: Int) =
        monstersByDifficultyType.getOrDefault(
            MonsterLibraryDifficultyTypeKey(difficulty, monsterType, desecrated),
            emptySet()
        ).map { upgradeIfDesecrated(desecrated, it, desecratedLevel) }

    private fun upgradeIfDesecrated(desecrated: Boolean, monster: Monster, desecratedLevel: Int): Monster =
        if (desecrated) upgradeMonsterToDesecrated(monster, desecratedLevel) else monster

    private fun upgradeMonsterToDesecrated(monster: Monster, characterLevel: Int): Monster {
        val newMonsterLevel = monster.getDesecratedMonsterLevel(characterLevel)
        return monster.copy(
            level = newMonsterLevel,
            treasureClass = treasureClassLibrary.changeTcBasedOnLevel(
                monster.treasureClass,
                newMonsterLevel,
                monster.difficulty,
                true
            )
        )
    }

    fun getMonsters(monsterType: MonsterType, desecrated: Boolean, desecratedLevel: Int) =
        monstersByType.getOrDefault(MonsterLibraryTypeKey(monsterType, desecrated), emptySet())
            .map { upgradeIfDesecrated(desecrated, it, desecratedLevel) }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MonsterLibrary

        if (monsters != other.monsters) return false
        if (treasureClassLibrary != other.treasureClassLibrary) return false

        return true
    }

    override fun hashCode(): Int {
        var result = monsters.hashCode()
        result = 31 * result + treasureClassLibrary.hashCode()
        return result
    }

    override fun toString(): String {
        return "MonsterLibrary(monsters=$monsters, treasureClassLibrary=$treasureClassLibrary)"
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
                    TreasureClassType.REGULAR,
                ), level, difficulty, false
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
                ), level, difficulty, false
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
                        difficulty,
                        false
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