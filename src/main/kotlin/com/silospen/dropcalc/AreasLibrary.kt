package com.silospen.dropcalc

class AreasLibrary(private val areasByMonsterProperties: Map<Triple<String, Difficulty, MonsterType>, Set<Area>>) {
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
            return AreasLibrary(areasByMonsterProperties)
        }
    }

    fun getAreasForMonsterClassId(monsterClassId: String, difficulty: Difficulty, monsterType: MonsterType): Set<Area> {
        return areasByMonsterProperties.getOrDefault(Triple(monsterClassId, difficulty, monsterType), emptySet())
    }

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