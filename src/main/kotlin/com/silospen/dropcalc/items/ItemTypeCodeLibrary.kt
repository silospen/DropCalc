package com.silospen.dropcalc.items

class ItemTypeCodeLibrary(itemTypeCodes: List<ItemTypeCodeWithParents>) {

    private val itemTypeCodesByCode = itemTypeCodes.associateBy { it.code }

    fun getAllParentCodes(code: String): Set<String> {
        return itemTypeCodesByCode.getValue(code).parentCodes
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemTypeCodeLibrary

        if (itemTypeCodesByCode != other.itemTypeCodesByCode) return false

        return true
    }

    override fun hashCode(): Int {
        return itemTypeCodesByCode.hashCode()
    }

    override fun toString(): String {
        return "ItemTypeCodeLibrary(itemTypeCodesByCode=$itemTypeCodesByCode)"
    }

    companion object {
        fun fromIncompleteLineages(incompleteItemTypeCodes: List<SingleItemTypeCodeEntry>): ItemTypeCodeLibrary {
            return ItemTypeCodeLibrary(
                incompleteItemTypeCodes.map {
                    val parentCodes = getParentCodesFor(
                        it.code,
                        incompleteItemTypeCodes.associateBy { itemTypeCode -> itemTypeCode.code })
                    ItemTypeCodeWithParents(it.code, parentCodes)
                }
            )
        }

        private fun getParentCodesFor(
            code: String,
            incompleteItemTypeCodesByCode: Map<String, SingleItemTypeCodeEntry>
        ): Set<String> {
            val result = mutableSetOf<String>()
            dfs(result, code, incompleteItemTypeCodesByCode)
            return result
        }

        private fun dfs(
            result: MutableSet<String>,
            code: String,
            incompleteItemTypeCodesByCode: Map<String, SingleItemTypeCodeEntry>
        ) {
            val parentCodes = incompleteItemTypeCodesByCode.getValue(code).parentCodes
            result.addAll(parentCodes)
            parentCodes.forEach { dfs(result, it, incompleteItemTypeCodesByCode) }
        }
    }
}

data class SingleItemTypeCodeEntry(
    val code: String,
    val parentCodes: Set<String>
)

data class ItemTypeCodeWithParents(
    val code: String,
    val parentCodes: Set<String>
)