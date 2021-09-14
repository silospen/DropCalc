package com.silospen.dropcalc.config

import com.silospen.dropcalc.*
import com.silospen.dropcalc.areas.AreasLibrary
import com.silospen.dropcalc.areas.hardcodedBossAreas
import com.silospen.dropcalc.areas.hardcodedSuperUniqueAreas
import com.silospen.dropcalc.files.*
import com.silospen.dropcalc.monsters.MonsterLibrary
import com.silospen.dropcalc.translations.CompositeTranslations
import com.silospen.dropcalc.translations.MapBasedTranslations
import com.silospen.dropcalc.translations.Translations
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File

@Configuration
class Beans {
    @Bean
    fun getTreasureClassConfigs(): List<TreasureClassConfig> = readTsv(
        File("C:\\Users\\silos\\Downloads\\D2Files\\cleanTextFiles\\1.12a\\TreasureClassEx.txt"),
        TreasureClassesLineParser()
    )

    @Bean
    fun getMonsterClassConfigs(
        treasureClassCalculator: TreasureClassCalculator,
        translations: Translations
    ): List<MonsterClass> = readTsv(
        File("C:\\Users\\silos\\Downloads\\D2Files\\cleanTextFiles\\1.12a\\monstats.txt"),
        MonstatsLineParser(treasureClassCalculator, translations)
    )

    @Bean
    fun getSuperUniqueMonsterConfigs(
        treasureClassCalculator: TreasureClassCalculator,
        translations: Translations
    ): List<SuperUniqueMonsterConfig> =
        readTsv(
            File("C:\\Users\\silos\\Downloads\\D2Files\\cleanTextFiles\\1.12a\\SuperUniques.txt"),
            SuperUniqueLineParser(treasureClassCalculator, hardcodedSuperUniqueAreas, translations)
        )

    @Bean
    fun getAreas(translations: Translations): List<Area> = readTsv(
        File("C:\\Users\\silos\\Downloads\\D2Files\\cleanTextFiles\\1.12a\\Levels.txt"),
        LevelsLineParser(translations, hardcodedBossAreas)
    )

    @Bean
    fun getAreasLibrary(areas: List<Area>): AreasLibrary {
        return AreasLibrary.fromAreas(areas)
    }

    @Bean
    fun getMonsterLibrary(
        monsterClassConfigs: List<MonsterClass>,
        superUniqueMonsterConfigs: List<SuperUniqueMonsterConfig>,
        areasLibrary: AreasLibrary
    ): MonsterLibrary {
        return MonsterLibrary.fromConfig(monsterClassConfigs, superUniqueMonsterConfigs, areasLibrary)
    }

    @Bean
    fun getTranslations(): Translations =
        CompositeTranslations(
            MapBasedTranslations.loadTranslations(File("C:\\Users\\silos\\Downloads\\D2Files\\cleanTextFiles\\tbl\\1.12a\\patchstring.tbl")),
            MapBasedTranslations.loadTranslations(File("C:\\Users\\silos\\Downloads\\D2Files\\cleanTextFiles\\tbl\\expansionstring.tbl")),
            MapBasedTranslations.loadTranslations(File("C:\\Users\\silos\\Downloads\\D2Files\\cleanTextFiles\\tbl\\string.tbl"))
        )
}