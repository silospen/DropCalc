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

    fun generateTcExpectationDataToFile(
        monsterId: String,
        monsterType: MonsterType,
        difficulty: Difficulty,
        nPlayers: Int,
        partySize: Int
    ): File {
        val tempFile = File.createTempFile("dropcalcIntegTest", null)
        tempFile.deleteOnExit()
        tempFile.writeText(
            generateTcExpectationData(
                monsterId,
                monsterType,
                difficulty,
                nPlayers,
                partySize
            )
        )
        return tempFile
    }


    fun generateTcExpectationData(
        monsterId: String,
        monsterType: MonsterType,
        difficulty: Difficulty,
        nPlayers: Int,
        partySize: Int,
    ): String = callSilospenDropCalcAndParseResponse(
        "attc",
        monsterId,
        when (monsterType) {
            BOSS -> "bossMon"
            REGULAR -> "regMon"
            CHAMPION -> "champMon"
            UNIQUE -> "uniqMon"
            QUEST -> "supUniqMon"
            MINION -> "minMon"
            else -> throw RuntimeException("Unrecognized monsterType $monsterType")
        },
        when (difficulty) {
            NORMAL -> "N"
            NIGHTMARE -> "NM"
            HELL -> "H"
        },
        nPlayers,
        partySize
    )

    private fun callSilospenDropCalcAndParseResponse(
        type: String,
        monId: String,
        monClass: String,
        diff: String,
        nPlayers: Int,
        partySize: Int
    ): String = runBlocking {
        val response: HttpResponse =
            client.get("https://dropcalc.silospen.com/cgi-bin/pyDrop.cgi?type=$type&monID=$monId&diff=$diff&monClass=$monClass&nPlayers=$nPlayers&nGroup=$partySize&decMode=true&version=112")
        if (response.status != HttpStatusCode.OK) throw RuntimeException("Bad status code $response")
        response
            .readText()
            .replace(" </td></tr><tr><td>", "\n")
            .replace("</td><td>", "\t")
            .removePrefix("<tr><td>")
            .removeSuffix(" </td></tr>\n")
    }
}