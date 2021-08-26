package com.silospen.dropcalc

import com.silospen.dropcalc.monsters.MonsterLibrary
import org.apache.commons.math3.fraction.Fraction
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class MessageResource(
//    private val treasureClassCalculator: TreasureClassCalculator,
    private val monsterClassConfigs: List<MonsterClass>
) {
    @GetMapping("/atomicTcs")
    fun getAtomicTcs(
        @RequestParam("monsterId", required = true) monsterId: String,
        @RequestParam("monsterType", required = true) monsterType: MonsterType,
        @RequestParam("difficulty", required = true) difficulty: Difficulty,
        @RequestParam("players", required = true) nPlayers: Int,
        @RequestParam("party", required = true) partySize: Int
    ): List<Message> {
//        val monster: Monster = monsterLibrary.getMonsters(monsterId)
//
//
//
//        val treasureClass: TreasureClass = monster.getTreasureClass(monsterType, difficulty)
//        val leafOutcomes: Map<ItemClass, Fraction> =
//            treasureClassCalculator.getLeafOutcomes(treasureClass, nPlayers, partySize)


        return listOf(
            Message("1", "Hello!"),
            Message("2", "Bonjour!"),
            Message("3", "Privet!"),
        )
    }
}

data class Message(val id: String?, val text: String)