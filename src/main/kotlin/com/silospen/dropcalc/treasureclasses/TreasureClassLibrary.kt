package com.silospen.dropcalc.treasureclasses

import com.silospen.dropcalc.*
import com.silospen.dropcalc.items.ItemLibrary

class TreasureClassLibrary(treasureClassConfigs: List<TreasureClassConfig>, private val itemLibrary: ItemLibrary) {
    val treasureClasses: List<TreasureClass> = generateTreasureClasses(treasureClassConfigs)
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

    fun changeTcBasedOnLevel(
        treasureClassName: String,
        monsterLevel: Int,
        difficulty: Difficulty,
        alwaysUpgrade: Boolean
    ) =
        changeTcBasedOnLevel(
            getTreasureClass(treasureClassName),
            monsterLevel,
            difficulty,
            alwaysUpgrade
        ).name

    fun changeTcBasedOnLevel(
        baseTreasureClass: TreasureClass,
        monsterLevel: Int,
        difficulty: Difficulty,
        alwaysUpgrade: Boolean,
    ): TreasureClass {
        if (!alwaysUpgrade && difficulty == Difficulty.NORMAL) return baseTreasureClass //TODO: Validate if this is true for desecrated
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TreasureClassLibrary

        if (itemLibrary != other.itemLibrary) return false
        if (treasureClasses != other.treasureClasses) return false

        return true
    }

    override fun hashCode(): Int {
        var result = itemLibrary.hashCode()
        result = 31 * result + treasureClasses.hashCode()
        return result
    }
}

enum class TreasureClassOutcomeType {
    DEFINED,
    VIRTUAL
}