package com.silospen.dropcalc.treasureclasses

import com.silospen.dropcalc.*
import com.silospen.dropcalc.items.ItemLibrary

class TreasureClassLibrary(treasureClassConfigs: List<TreasureClassConfig>, private val itemLibrary: ItemLibrary) {
    private val treasureClasses: List<TreasureClass> = generateTreasureClasses(treasureClassConfigs)
    private val treasureClassesByName: Map<String, TreasureClass> = treasureClasses.associateBy { it.name }
    private val treasureClassesByGroup = treasureClasses
        .filter { it.properties.group != null && it.properties.level != null }
        .groupBy { it.properties.group }

    private fun generateTreasureClasses(treasureClassConfigs: List<TreasureClassConfig>): List<TreasureClass> {
        val treasureClasses = mutableListOf<TreasureClass>()
        for (treasureClassConfig in treasureClassConfigs) {
            treasureClasses.add(
                DefinedTreasureClass(
                    treasureClassConfig.name,
                    treasureClassConfig.items.sumOf { it.second },
                    treasureClassConfig.properties,
                    treasureClassConfig.items.map { item -> toOutcome(item, treasureClasses) }.toSet()
                )
            )
        }
        return treasureClasses
    }

    private fun toOutcome(itemAndProbability: Pair<String, Int>, treasureClasses: List<TreasureClass>): Outcome {
        val treasureClass = treasureClasses.find { it.name == itemAndProbability.first }
        return if (treasureClass != null) {
            Outcome(treasureClass, itemAndProbability.second)
        } else {
            Outcome(itemLibrary.getOrConstructVirtualTreasureClass(itemAndProbability.first), itemAndProbability.second)
        }
    }

    fun changeTcBasedOnLevel(treasureClassName: String, monsterLevel: Int, difficulty: Difficulty) =
        changeTcBasedOnLevel(
            getTreasureClass(treasureClassName),
            monsterLevel,
            difficulty
        ).name

    fun changeTcBasedOnLevel(
        baseTreasureClass: TreasureClass,
        monsterLevel: Int,
        difficulty: Difficulty
    ): TreasureClass {
        if (difficulty == Difficulty.NORMAL) return baseTreasureClass
        val treasureClassGroup = treasureClassesByGroup[baseTreasureClass.properties.group] ?: return baseTreasureClass
        val baseTcIndex = treasureClassGroup.indexOf(baseTreasureClass)
        var nextIndex = baseTcIndex + 1
        var result = baseTreasureClass
        while (nextIndex < treasureClassGroup.size) {
            if (treasureClassGroup[nextIndex].properties.level!! > monsterLevel) break
            result = treasureClassGroup[nextIndex]
            nextIndex++
        }
        return result
    }

    fun getTreasureClass(treasureClassName: String) =
        treasureClassesByName.getOrDefault(treasureClassName, VirtualTreasureClass(treasureClassName))
}

enum class TreasureClassOutcomeType {
    DEFINED,
    VIRTUAL
}