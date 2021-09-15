package com.silospen.dropcalc.items

import com.silospen.dropcalc.BaseItem
import com.silospen.dropcalc.Outcome
import com.silospen.dropcalc.VirtualTreasureClass
import org.springframework.stereotype.Component

@Component
class ItemLibrary(private val baseItems: List<BaseItem>) {
    private val itemTreasureClassesByName = generateVirtualTreasureClasses().associateBy { it.name }
    private val baseItemsById = baseItems.associateBy { it.id }

    private fun generateVirtualTreasureClasses() =
        baseItems.flatMap { item -> item.treasureClasses.map { it to item } }
            .groupBy({ it.first }) { it.second }
            .map { entry ->
                val items: Set<Pair<BaseItem, Int>> =
                    entry.value.map { baseItem -> baseItem to baseItem.itemType.rarity }.toSet()
                VirtualTreasureClass(
                    entry.key,
                    items.sumOf { it.second },
                    items.map { Outcome(it.first, it.second) }.toSet()
                )
            }

    fun getOrConstructVirtualTreasureClass(name: String): VirtualTreasureClass {
        val virtualTreasureClass = itemTreasureClassesByName[name]
        if (virtualTreasureClass != null) return virtualTreasureClass
        val outcomes = baseItemsById[name]?.let { setOf(Outcome(it, 1)) } ?: emptySet()
        return VirtualTreasureClass(name, outcomes = outcomes)
    }
}