package com.silospen.dropcalc

import com.silospen.dropcalc.Difficulty.*
import com.silospen.dropcalc.MonsterType.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import java.io.File

class TestDataGenerator(private val client: HttpClient) {

    fun generateMonsterExpectationDataToFile(
        monsterId: String,
        monsterType: MonsterType,
        difficulty: Difficulty,
        nPlayers: Int,
        partySize: Int,
        quality: ItemQuality
    ) = generateExpectationDataToFile {
        generateMonsterExpectationData(
            monsterId,
            monsterType,
            difficulty,
            nPlayers,
            partySize,
            quality
        )
    }

    fun generateTcExpectationDataToFile(
        monsterId: String,
        monsterType: MonsterType,
        difficulty: Difficulty,
        nPlayers: Int,
        partySize: Int
    ) = generateExpectationDataToFile {
        generateTcExpectationData(
            monsterId,
            monsterType,
            difficulty,
            nPlayers,
            partySize
        )
    }

    private fun generateExpectationDataToFile(expectationDataSource: () -> String): File {
        val tempFile = File.createTempFile("dropcalcIntegTest", null)
        tempFile.deleteOnExit()
        tempFile.writeText(expectationDataSource())
        return tempFile
    }


    private fun generateTcExpectationData(
        monsterId: String,
        monsterType: MonsterType,
        difficulty: Difficulty,
        nPlayers: Int,
        partySize: Int,
    ): String = callSilospenDropCalcAndParseResponse(
        "https://dropcalc.silospen.com/cgi-bin/pyDrop.cgi?type=attc&monID=${monsterId.lowercase()}&diff=${
            toDifficulty(
                difficulty
            )
        }&monClass=${toMonsterType(monsterType)}&nPlayers=$nPlayers&nGroup=$partySize&decMode=true&version=112"
    )

    private fun generateMonsterExpectationData(
        monsterId: String,
        monsterType: MonsterType,
        difficulty: Difficulty,
        nPlayers: Int,
        partySize: Int,
        quality: ItemQuality
    ): String = callSilospenDropCalcAndParseResponse(
        "https://dropcalc.silospen.com/cgi-bin/pyDrop.cgi?type=mon&monID=${monsterId.lowercase()}&diff=${
            toDifficulty(
                difficulty
            )
        }&monClass=${toMonsterType(monsterType)}&nPlayers=$nPlayers&nGroup=$partySize&quality=${toQuality(quality)}&mf=0&decMode=true&version=112"
    )

    private fun toQuality(quality: ItemQuality): String =
        when (quality) {
            ItemQuality.WHITE -> "regItem"
            else -> throw RuntimeException()
        }

    private fun toDifficulty(difficulty: Difficulty) = when (difficulty) {
        NORMAL -> "N"
        NIGHTMARE -> "NM"
        HELL -> "H"
    }

    private fun toMonsterType(monsterType: MonsterType) = when (monsterType) {
        BOSS -> "bossMon"
        REGULAR -> "regMon"
        CHAMPION -> "champMon"
        UNIQUE -> "uniqMon"
        SUPERUNIQUE -> "supUniqMon"
        MINION -> "minMon"
        else -> throw RuntimeException("Unrecognized monsterType $monsterType")
    }

    private fun callSilospenDropCalcAndParseResponse(url: String): String = runBlocking {
        val response: HttpResponse =
            client.get(url)
        if (response.status != HttpStatusCode.OK) throw RuntimeException("Bad status code $response")
        response
            .readText()
            .replace(" </td></tr><tr><td>", "\n")
            .replace("</td><td>", "\t")
            .removePrefix("<tr><td>")
            .removeSuffix(" </td></tr>\n")
    }
}