package com.silospen.dropcalc.config

import com.silospen.dropcalc.*
import com.silospen.dropcalc.monsters.MonsterLibrary
import com.silospen.dropcalc.parser.LevelsLineParser
import com.silospen.dropcalc.parser.MonstatsLineParser
import com.silospen.dropcalc.parser.TreasureClassesLineParser
import com.silospen.dropcalc.reader.readTsv
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
    fun getAreas(): List<Area> = readTsv(
        File("C:\\Users\\silos\\Downloads\\D2Files\\cleanTextFiles\\1.12a\\Levels.txt"),
        LevelsLineParser()
    )

    @Bean
    fun getAreasLibrary(areas: List<Area>): AreasLibrary {
        return AreasLibrary.fromAreas(areas)
    }

    @Bean
    fun getMonsterLibrary(monsterClassConfigs: List<MonsterClass>, areasLibrary: AreasLibrary): MonsterLibrary {
        return MonsterLibrary.fromConfig(monsterClassConfigs, areasLibrary)
    }
}