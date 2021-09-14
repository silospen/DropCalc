package com.silospen.dropcalc.resource

import com.silospen.dropcalc.Difficulty
import com.silospen.dropcalc.MonsterType
import com.silospen.dropcalc.monsters.MonsterLibrary
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping(value = ["/metadata"])
@RestController
class MetadataResource(monsterLibrary: MonsterLibrary) {

    private val monstersResponsesByDifficultyType =
        monsterLibrary.monsters.groupBy({ it.difficulty to it.type }) { MonstersResponse(it.name, it.id) }
            .mapValues { it.value.toSet() }
            .mapValues { it.value.sortedBy { monstersResponse -> monstersResponse.name } }


    @GetMapping("monsters")
    fun getMonsters(
        @RequestParam("difficulty", required = true) difficulty: Difficulty,
        @RequestParam("monsterType", required = true) monsterType: MonsterType,
    ): List<MonstersResponse> {
        return monstersResponsesByDifficultyType.getOrDefault(difficulty to monsterType, emptyList())
    }

}

data class MonstersResponse(
    val name: String,
    val id: String
)