package com.silospen.dropcalc

import com.silospen.dropcalc.MonsterType.*
import com.silospen.dropcalc.items.ItemLibrary
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import java.io.File

@Component
class TestDataGenerator(private val itemLibrary: ItemLibrary) {

    private val client: HttpClient = HttpClient()

    fun generateItemExpectationDataToFile(
        itemId: String,
        monsterType: MonsterType,
        difficulty: Difficulty?,
        nPlayers: Int,
        partySize: Int,
        quality: ItemQuality,
        magicFind: Int,
    ) = generateExpectationDataToFile {
        generateItemExpectationData(
            itemId,
            monsterType,
            difficulty,
            nPlayers,
            partySize,
            quality,
            magicFind
        )
    }

    fun generateMonsterExpectationDataToFile(
        monsterId: String,
        monsterType: MonsterType,
        difficulty: Difficulty,
        nPlayers: Int,
        partySize: Int,
        quality: ItemQuality,
        magicFind: Int,
    ) = generateExpectationDataToFile {
        generateMonsterExpectationData(
            monsterId,
            monsterType,
            difficulty,
            nPlayers,
            partySize,
            quality,
            magicFind
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
        "https://dropcalc.silospen.com/cgi-bin/pyDrop.cgi?type=attc&monID=${monsterId.lowercase()}&diff=${difficulty.displayString}&monClass=${
            toMonsterType(
                monsterType
            )
        }&nPlayers=$nPlayers&nGroup=$partySize&decMode=true&version=112"
    )

    private fun generateMonsterExpectationData(
        monsterId: String,
        monsterType: MonsterType,
        difficulty: Difficulty,
        nPlayers: Int,
        partySize: Int,
        quality: ItemQuality,
        magicFind: Int
    ): String = callSilospenDropCalcAndParseResponse(
        "https://dropcalc.silospen.com/cgi-bin/pyDrop.cgi?type=mon&monID=${monsterId.lowercase()}&diff=${difficulty.displayString}&monClass=${
            toMonsterType(
                monsterType
            )
        }&nPlayers=$nPlayers&nGroup=$partySize&quality=${toQuality(quality)}&mf=$magicFind&decMode=true&version=112"
    )
//    Request URL: https://dropcalc.silospen.com/cgi-bin/pyDrop.cgi?type=item&itemName=akaran%20targe&diff=H&monClass=minMon&nPlayers=1&nGroup=1&mf=0&quality=rareItem&decMode=true&version=112

    private fun generateItemExpectationData(
        itemId: String,
        monsterType: MonsterType,
        difficulty: Difficulty?,
        nPlayers: Int,
        partySize: Int,
        quality: ItemQuality,
        magicFind: Int
    ): String = callSilospenDropCalcAndParseResponse(
        "https://dropcalc.silospen.com/cgi-bin/pyDrop.cgi?type=item&itemName=${
            itemLibrary.getItem(
                quality,
                itemId
            )!!.name.lowercase()
        }&diff=${difficulty?.displayString ?: "A"}&monClass=${toMonsterType(monsterType)}&nPlayers=$nPlayers&nGroup=$partySize&quality=${
            toQuality(
                quality
            )
        }&mf=$magicFind&decMode=true&version=112"
    )

    private fun toQuality(quality: ItemQuality): String =
        when (quality) {
            ItemQuality.WHITE -> "regItem"
            ItemQuality.UNIQUE -> "uniqItem"
            ItemQuality.SET -> "setItem"
            ItemQuality.RARE -> "rareItem"
            ItemQuality.MAGIC -> "magicItem"
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