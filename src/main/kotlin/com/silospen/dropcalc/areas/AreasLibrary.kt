package com.silospen.dropcalc.areas

import com.silospen.dropcalc.Area
import com.silospen.dropcalc.Difficulty
import com.silospen.dropcalc.MonsterType

class AreasLibrary(
    private val areasById: Map<String, Area>,
    private val areasByMonsterProperties: Map<Triple<String, Difficulty, MonsterType>, Set<Area>>
) {
    companion object {
        fun fromAreas(areas: List<Area>): AreasLibrary {
            val areasByMonsterProperties: Map<Triple<String, Difficulty, MonsterType>, Set<Area>> =
                areas.asSequence().flatMap { area ->
                    area.monsterClassIds.cellSet().asSequence()
                        .flatMap {
                            it.value?.asSequence()
                                ?.map { monsterClassId -> Triple(monsterClassId, it.rowKey!!, it.columnKey!!) to area }
                                ?: emptySequence()
                        }
                }
                    .groupBy({ it.first }, { it.second })
                    .mapValues { it.value.toSet() }
            return AreasLibrary(areas.associateBy { it.id }, areasByMonsterProperties)
        }
    }

    fun getAreasForMonsterClassId(monsterClassId: String, difficulty: Difficulty, monsterType: MonsterType): Set<Area> {
        return areasByMonsterProperties.getOrDefault(Triple(monsterClassId, difficulty, monsterType), emptySet())
    }

    fun getArea(areaId: String): Area = areasById.getValue(areaId)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AreasLibrary

        if (areasByMonsterProperties != other.areasByMonsterProperties) return false

        return true
    }

    override fun hashCode(): Int {
        return areasByMonsterProperties.hashCode()
    }

    override fun toString(): String {
        return "AreasLibrary(areasByMonsterProperties=$areasByMonsterProperties)"
    }


}