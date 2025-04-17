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
import com.silospen.dropcalc.treasureclasses.TreasureClassLibrary
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
    private val baseItems = loadBaseItems(itemTypes)
    private val itemLibrary = ItemLibrary(baseItems, loadItemRatio(), loadItems(baseItems))
    private val treasureClassConfigs = loadTreasureClassConfigs()
    private val treasureClassLibrary = TreasureClassLibrary(treasureClassConfigs, itemLibrary)
    private val treasureClassCalculator = TreasureClassCalculator(treasureClassLibrary)
    private val monsterLibrary = loadMonsterLibrary(
        loadMonsterClassConfigs(),
        loadSuperUniqueMonsterConfigs(),
        MonsterFactory(loadAreasLibrary(loadAreas()), treasureClassLibrary),
        treasureClassLibrary
    )

    companion object {
        private val expansionTranslations =
            MapBasedTranslations.loadTranslations(getResource("d2Files/tbl/expansionstring.tbl"), Language.ENGLISH)
        private val coreTranslations =
            MapBasedTranslations.loadTranslations(getResource("d2Files/tbl/string.tbl"), Language.ENGLISH)
    }

    private fun loadTreasureClassConfigs(): List<TreasureClassConfig> = readTsv(
        getResource("d2Files/${version.pathName}/treasureclassex.txt"),
        TreasureClassesLineParser()
    )

    private fun loadMonsterClassConfigs(): List<MonsterClass> = readTsv(
        getResource("d2Files/${version.pathName}/monstats.txt"),
        MonstatsLineParser()
    )

    private fun loadSuperUniqueMonsterConfigs(): List<SuperUniqueMonsterConfig> =
        readTsv(
            getResource("d2Files/${version.pathName}/superuniques.txt"),
            SuperUniqueLineParser(hardcodedSuperUniqueAreas)
        )

    private fun loadAreas(): List<Area> = readTsv(
        getResource("d2Files/${version.pathName}/levels.txt"),
        LevelsLineParser(hardcodedBossAreas)
    )

    private fun loadAreasLibrary(areas: List<Area>): AreasLibrary {
        return AreasLibrary.fromAreas(areas)
    }

    private fun loadMonsterLibrary(
        monsterClassConfigs: List<MonsterClass>,
        superUniqueMonsterConfigs: List<SuperUniqueMonsterConfig>,
        monsterFactory: MonsterFactory,
        treasureClassLibrary: TreasureClassLibrary
    ): MonsterLibrary {
        return MonsterLibrary.fromConfig(
            monsterClassConfigs,
            superUniqueMonsterConfigs,
            monsterFactory,
            treasureClassLibrary
        )
    }

    private fun loadTranslations(): Translations {
        return if (version == Version.D2R_V1_0) {
            CompositeTranslations(
                MapBasedTranslations.loadTranslationsFromJsonFile(getResource("d2Files/tbl/${version.pathName}/item-names.json")),
                MapBasedTranslations.loadTranslationsFromJsonFile(getResource("d2Files/tbl/${version.pathName}/item-runes.json")),
                MapBasedTranslations.loadTranslationsFromJsonFile(getResource("d2Files/tbl/${version.pathName}/levels.json")),
                MapBasedTranslations.loadTranslationsFromJsonFile(getResource("d2Files/tbl/${version.pathName}/monsters.json")),
                MapBasedTranslations.loadTranslationsFromJsonFile(getResource("d2Files/tbl/${version.pathName}/npcs.json")),
                MapBasedTranslations.loadTranslations(
                    getResource("d2Files/tbl/${version.pathName}/patchstring.tbl"),
                    Language.ENGLISH
                ),
                expansionTranslations,
                coreTranslations
            )
        } else {
            CompositeTranslations(
                MapBasedTranslations.loadTranslations(
                    getResource("d2Files/tbl/${version.pathName}/patchstring.tbl"),
                    Language.ENGLISH
                ),
                expansionTranslations,
                coreTranslations
            )
        }
    }

    private fun loadBaseItems(itemTypes: List<ItemType>): List<BaseItem> = readTsv(
        getResource("d2Files/${version.pathName}/weapons.txt"),
        BaseItemLineParser(itemTypes)
    ) + readTsv(
        getResource("d2Files/${version.pathName}/armor.txt"),
        BaseItemLineParser(itemTypes)
    ) + readTsv(
        getResource("d2Files/${version.pathName}/misc.txt"),
        BaseItemLineParser(itemTypes, ItemVersion.NORMAL)
    )

    private fun loadItemTypes(itemTypeCodeLibrary: ItemTypeCodeLibrary): List<ItemType> = readTsv(
        getResource("d2Files/${version.pathName}/itemtypes.txt"),
        ItemTypeParser(itemTypeCodeLibrary)
    )

    private fun loadItemTypeCodes(): List<SingleItemTypeCodeEntry> = readTsv(
        getResource("d2Files/${version.pathName}/itemtypes.txt"),
        ItemTypeCodesParser()
    )

    private fun loadItemRatio(): List<ItemRatio> = readTsv(
        getResource("d2Files/${version.pathName}/itemratio.txt"),
        ItemRatioLineParser()
    )

    private fun loadItemTypeCodeLibrary(itemTypeCodes: List<SingleItemTypeCodeEntry>) =
        ItemTypeCodeLibrary.fromIncompleteLineages(itemTypeCodes)

    private fun loadItems(baseItems: List<BaseItem>): List<Item> {
        val uniqueItems = readTsv(
            getResource("d2Files/${version.pathName}/uniqueitems.txt"),
            UniqueItemLineParser(baseItems, version)
        )
        val setItems = readTsv(
            getResource("d2Files/${version.pathName}/setitems.txt"),
            SetItemLineParser(baseItems)
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
                    it.nameId,
                    itemQuality,
                    it,
                    it.level,
                    it.itemType.rarity,
                    false,
                    null
                )
            }

    fun createVersionedApiResource(): VersionedApiResource {
        return VersionedApiResource(
            treasureClassCalculator, monsterLibrary, itemLibrary, translations
        )
    }

    fun createVersionedMetadataResource(): VersionedMetadataResource {
        return VersionedMetadataResource(
            monsterLibrary,
            itemLibrary,
            translations,
            version == Version.D2R_V1_0,
            treasureClassLibrary
        )
    }
}