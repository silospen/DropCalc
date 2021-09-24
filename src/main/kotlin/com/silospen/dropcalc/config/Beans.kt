package com.silospen.dropcalc.config

import com.silospen.dropcalc.*
import com.silospen.dropcalc.areas.AreasLibrary
import com.silospen.dropcalc.areas.hardcodedBossAreas
import com.silospen.dropcalc.areas.hardcodedSuperUniqueAreas
import com.silospen.dropcalc.files.*
import com.silospen.dropcalc.items.ItemLibrary
import com.silospen.dropcalc.items.ItemTypeCodeLibrary
import com.silospen.dropcalc.items.SingleItemTypeCodeEntry
import com.silospen.dropcalc.monsters.MonsterFactory
import com.silospen.dropcalc.monsters.MonsterLibrary
import com.silospen.dropcalc.resource.VersionedApiResource
import com.silospen.dropcalc.resource.VersionedMetadataResource
import com.silospen.dropcalc.translations.CompositeTranslations
import com.silospen.dropcalc.translations.MapBasedTranslations
import com.silospen.dropcalc.translations.Translations
import com.silospen.dropcalc.treasureclasses.TreasureClassCalculator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Beans {

    @Bean
    fun configLoaders(): Map<Version, ConfigLoader> = Version.values().associateWith { ConfigLoader(it) }

    @Bean
    fun getVersionedApiResources(configLoaders: Map<Version, ConfigLoader>): Map<Version, VersionedApiResource> =
        configLoaders.map { (version, configLoader) ->
            version to configLoader.createVersionedApiResource()
        }.toMap()

    @Bean
    fun getVersionedMetadataResources(configLoaders: Map<Version, ConfigLoader>): Map<Version, VersionedMetadataResource> =
        configLoaders.map { (version, configLoader) ->
            version to configLoader.createVersionedMetadataResource()
        }.toMap()

}

class ConfigLoader(private val version: Version) {
    private val translations = loadTranslations()
    private val itemTypes = loadItemTypes(loadItemTypeCodeLibrary(loadItemTypeCodes()))
    private val baseItems = loadBaseItems(translations, itemTypes)
    private val itemLibrary = ItemLibrary(baseItems, loadItemRatio(), loadItems(translations, baseItems))
    private val treasureClassConfigs = loadTreasureClassConfigs()
    private val treasureClassCalculator = TreasureClassCalculator(treasureClassConfigs, itemLibrary)
    private val monsterLibrary = loadMonsterLibrary(
        loadMonsterClassConfigs(translations),
        loadSuperUniqueMonsterConfigs(translations),
        MonsterFactory(loadAreasLibrary(loadAreas(translations)), treasureClassCalculator)
    )

    companion object {
        private val expansionTranslations =
            MapBasedTranslations.loadTranslations(getResource("d2Files/tbl/expansionstring.tbl"))
        private val coreTranslations = MapBasedTranslations.loadTranslations(getResource("d2Files/tbl/string.tbl"))
    }

    private fun loadTreasureClassConfigs(): List<TreasureClassConfig> = readTsv(
        getResource("d2Files/${version.pathName}/TreasureClassEx.txt"),
        TreasureClassesLineParser()
    )

    private fun loadMonsterClassConfigs(translations: Translations): List<MonsterClass> = readTsv(
        getResource("d2Files/${version.pathName}/monstats.txt"),
        MonstatsLineParser(translations)
    )

    private fun loadSuperUniqueMonsterConfigs(translations: Translations): List<SuperUniqueMonsterConfig> =
        readTsv(
            getResource("d2Files/${version.pathName}/SuperUniques.txt"),
            SuperUniqueLineParser(hardcodedSuperUniqueAreas, translations)
        )

    private fun loadAreas(translations: Translations): List<Area> = readTsv(
        getResource("d2Files/${version.pathName}/Levels.txt"),
        LevelsLineParser(translations, hardcodedBossAreas)
    )

    private fun loadAreasLibrary(areas: List<Area>): AreasLibrary {
        return AreasLibrary.fromAreas(areas)
    }

    private fun loadMonsterLibrary(
        monsterClassConfigs: List<MonsterClass>,
        superUniqueMonsterConfigs: List<SuperUniqueMonsterConfig>,
        monsterFactory: MonsterFactory
    ): MonsterLibrary {
        return MonsterLibrary.fromConfig(monsterClassConfigs, superUniqueMonsterConfigs, monsterFactory)
    }

    private fun loadTranslations(): Translations {
        return CompositeTranslations(
            MapBasedTranslations.loadTranslations(getResource("d2Files/tbl/${version.pathName}/patchstring.tbl")),
            expansionTranslations,
            coreTranslations
        )
    }

    private fun loadBaseItems(translations: Translations, itemTypes: List<ItemType>): List<BaseItem> = readTsv(
        getResource("d2Files/${version.pathName}/weapons.txt"),
        BaseItemLineParser(translations, itemTypes)
    ) + readTsv(
        getResource("d2Files/${version.pathName}/armor.txt"),
        BaseItemLineParser(translations, itemTypes)
    ) + readTsv(
        getResource("d2Files/${version.pathName}/misc.txt"),
        BaseItemLineParser(translations, itemTypes, ItemVersion.NORMAL)
    )

    private fun loadItemTypes(itemTypeCodeLibrary: ItemTypeCodeLibrary): List<ItemType> = readTsv(
        getResource("d2Files/${version.pathName}/ItemTypes.txt"),
        ItemTypeParser(itemTypeCodeLibrary)
    )

    private fun loadItemTypeCodes(): List<SingleItemTypeCodeEntry> = readTsv(
        getResource("d2Files/${version.pathName}/ItemTypes.txt"),
        ItemTypeCodesParser()
    )

    private fun loadItemRatio(): List<ItemRatio> = readTsv(
        getResource("d2Files/${version.pathName}/itemratio.txt"),
        ItemRatioLineParser()
    )

    private fun loadItemTypeCodeLibrary(itemTypeCodes: List<SingleItemTypeCodeEntry>) =
        ItemTypeCodeLibrary.fromIncompleteLineages(itemTypeCodes)

    private fun loadItems(translations: Translations, baseItems: List<BaseItem>): List<Item> {
        val uniqueItems = readTsv(
            getResource("d2Files/${version.pathName}/UniqueItems.txt"),
            UniqueItemLineParser(translations, baseItems)
        )
        val setItems = readTsv(
            getResource("d2Files/${version.pathName}/SetItems.txt"),
            SetItemLineParser(translations, baseItems)
        )
        val rareItems = generateItems(ItemQuality.RARE, baseItems) { it.itemType.canBeRare }
        val magicItems = generateItems(ItemQuality.MAGIC, baseItems) { it.itemType.canBeRare }
        return uniqueItems + setItems + rareItems + magicItems + generateItems(ItemQuality.WHITE, baseItems) { true }
    }

    private fun generateItems(
        itemQuality: ItemQuality,
        baseItems: List<BaseItem>,
        filter: (BaseItem) -> Boolean
    ): List<Item> =
        baseItems
            .filter(filter)
            .map {
                Item(
                    it.id,
                    it.name,
                    itemQuality,
                    it,
                    it.level,
                    it.itemType.rarity
                )
            }

    fun createVersionedApiResource(): VersionedApiResource {

        return VersionedApiResource(
            treasureClassCalculator, monsterLibrary,
            itemLibrary
        )
    }

    fun createVersionedMetadataResource(): VersionedMetadataResource {
        return VersionedMetadataResource(monsterLibrary, itemLibrary)
    }
}