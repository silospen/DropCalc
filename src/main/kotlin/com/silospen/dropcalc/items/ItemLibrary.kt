package com.silospen.dropcalc.items

import com.silospen.dropcalc.*
import com.silospen.dropcalc.ItemQuality.*
import com.silospen.dropcalc.monsters.Monster

class ItemLibrary(
    private val baseItems: List<BaseItem>,
    private val itemRatios: List<ItemRatio>,
    val items: List<Item>
) {
    private val itemTreasureClassesByNameId = generateVirtualTreasureClasses().associateBy { it.nameId }
    private val baseItemsById = baseItems.associateBy { it.id }
    private val itemsByQualityAndBaseId = items.groupBy { it.quality to it.baseItem.id }
    private val itemsByQualityAndId = items.associateBy { it.quality to it.id }

    fun getItemsForBaseId(itemQuality: ItemQuality, baseItemId: String): List<Item> =
        itemsByQualityAndBaseId.getOrDefault(itemQuality to baseItemId, emptyList())

    fun getItem(itemQuality: ItemQuality, id: String): Item? =
        itemsByQualityAndId[itemQuality to id]

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
        val virtualTreasureClass = itemTreasureClassesByNameId[name]
        if (virtualTreasureClass != null) return virtualTreasureClass
        val outcomes = (baseItemsById[name] ?: ItemQuality.values().firstNotNullOfOrNull { getItem(it, name) })
            ?.let { setOf(Outcome(it, 1)) } ?: emptySet()
        return VirtualTreasureClass(name, outcomes = outcomes)
    }

    private fun getItemQualityModifiers(baseItem: BaseItem, itemQuality: ItemQuality): ItemQualityModifiers {
        val isUber = baseItem.itemVersion != ItemVersion.NORMAL
        return itemRatios
            .first { it.isUber == isUber && it.isClassSpecific == baseItem.itemType.isClassSpecific }
            .modifiers.getValue(itemQuality)
    }

    fun getProbQuality(
        itemQuality: ItemQuality,
        monster: Monster,
        baseItem: BaseItem,
        itemQualityRatios: ItemQualityRatios,
        magicFind: Int
    ): Probability {
        return when (itemQuality) {
            UNIQUE -> getProbOfSingleItemQuality(UNIQUE, monster, baseItem, itemQualityRatios, magicFind)
            SET -> getProbQualitySequentially(SET, listOf(UNIQUE), monster, baseItem, itemQualityRatios, magicFind)
            RARE -> getProbQualitySequentially(
                RARE,
                listOf(UNIQUE, SET),
                monster,
                baseItem,
                itemQualityRatios,
                magicFind
            )

            MAGIC -> getProbQualitySequentially(
                MAGIC,
                listOf(UNIQUE, SET, RARE),
                monster,
                baseItem,
                itemQualityRatios,
                magicFind
            )

            WHITE -> Probability.ONE
        }
    }

    private fun getProbQualitySequentially(
        wantedItemQuality: ItemQuality,
        unwantedItemQualities: List<ItemQuality>,
        monster: Monster,
        baseItem: BaseItem,
        itemQualityRatios: ItemQualityRatios,
        magicFind: Int
    ) = unwantedItemQualities
        .map { getProbOfSingleItemQuality(it, monster, baseItem, itemQualityRatios, magicFind) }
        .map { Probability.ONE.subtract(it) }
        .reduce { acc, Probability -> acc.multiply(Probability) }
        .multiply(getProbOfSingleItemQuality(wantedItemQuality, monster, baseItem, itemQualityRatios, magicFind))

    private fun getProbOfSingleItemQuality(
        itemQuality: ItemQuality,
        monster: Monster,
        baseItem: BaseItem,
        itemQualityRatios: ItemQualityRatios,
        magicFind: Int
    ): Probability {
        val (ratio, divisor, min) = getItemQualityModifiers(baseItem, itemQuality)
        val chance = ratio - ((monster.level - baseItem.level) / divisor)
        val mulChance = chance * 128
        val effectiveMf = getEffectiveMf(magicFind, itemQuality)
        val chanceWithMf = (mulChance * 100) / (100 + effectiveMf)
        val chanceAfterMin = if (min > chanceWithMf) min else chanceWithMf
        val chanceAfterFactor = chanceAfterMin - (chanceAfterMin * itemQualityRatios.get(itemQuality) / 1024)
        return if (chanceAfterFactor == 0) Probability.ONE else Probability(128, chanceAfterFactor)
    }

    private fun getEffectiveMf(magicFind: Int, itemQuality: ItemQuality) = when (itemQuality) {
        UNIQUE -> calculateEffectiveMf(magicFind, 250)
        SET -> calculateEffectiveMf(magicFind, 500)
        RARE -> calculateEffectiveMf(magicFind, 600)
        MAGIC -> magicFind
        WHITE -> 0
    }

    private fun calculateEffectiveMf(magicFind: Int, magicFindFactor: Int) =
        if (magicFind <= 10) magicFind else magicFind * magicFindFactor / (magicFind + magicFindFactor)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemLibrary

        if (baseItems != other.baseItems) return false
        if (itemRatios != other.itemRatios) return false
        if (items != other.items) return false

        return true
    }

    override fun hashCode(): Int {
        var result = baseItems.hashCode()
        result = 31 * result + itemRatios.hashCode()
        result = 31 * result + items.hashCode()
        return result
    }
}