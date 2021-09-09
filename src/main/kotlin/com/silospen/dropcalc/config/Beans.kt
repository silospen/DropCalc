package com.silospen.dropcalc.config

import com.silospen.dropcalc.Area
import com.silospen.dropcalc.MonsterClass
import com.silospen.dropcalc.TreasureClassCalculator
import com.silospen.dropcalc.TreasureClassConfig
import com.silospen.dropcalc.areas.AreasLibrary
import com.silospen.dropcalc.areas.hardcodedBossAreas
import com.silospen.dropcalc.files.LevelsLineParser
import com.silospen.dropcalc.files.MonstatsLineParser
import com.silospen.dropcalc.files.TreasureClassesLineParser
import com.silospen.dropcalc.files.readTsv
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
    fun getMonsterClassConfigs(treasureClassCalculator: TreasureClassCalculator): List<MonsterClass> = readTsv(
        File("C:\\Users\\silos\\Downloads\\D2Files\\cleanTextFiles\\1.12a\\monstats.txt"),
        MonstatsLineParser(treasureClassCalculator)
    )

    @Bean
    fun getAreas(translations: Translations): List<Area> {
        val areasFromLevelsTxt = readTsv(
            File("C:\\Users\\silos\\Downloads\\D2Files\\cleanTextFiles\\1.12a\\Levels.txt"),
            LevelsLineParser(translations)
        )
        return areasFromLevelsTxt + hardcodedBossAreas
    }

    @Bean
    fun getAreasLibrary(areas: List<Area>): AreasLibrary {
        return AreasLibrary.fromAreas(areas)
    }

    @Bean
    fun getMonsterLibrary(monsterClassConfigs: List<MonsterClass>, areasLibrary: AreasLibrary): MonsterLibrary {
        return MonsterLibrary.fromConfig(monsterClassConfigs, areasLibrary)
    }

    @Bean
    fun getTranslations(): Translations =
        CompositeTranslations(
            MapBasedTranslations.loadTranslations(File("C:\\Users\\silos\\Downloads\\D2Files\\cleanTextFiles\\tbl\\1.12a\\patchstring.tbl")),
            MapBasedTranslations.loadTranslations(File("C:\\Users\\silos\\Downloads\\D2Files\\cleanTextFiles\\tbl\\expansionstring.tbl")),
            MapBasedTranslations.loadTranslations(File("C:\\Users\\silos\\Downloads\\D2Files\\cleanTextFiles\\tbl\\string.tbl"))
        )
}