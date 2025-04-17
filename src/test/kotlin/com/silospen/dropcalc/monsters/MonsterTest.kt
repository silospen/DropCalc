package com.silospen.dropcalc.monsters

import com.silospen.dropcalc.*
import com.silospen.dropcalc.Difficulty.*
import com.silospen.dropcalc.MonsterType.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.streams.asStream

class MonsterTest {

    @Test
    fun getDisplayName() {
        assertEquals("Skeleton-ENGLISH-name", skeletonMonster.getDisplayName(stubTranslations, Language.ENGLISH))
        assertEquals(
            "Skeleton-ENGLISH-name (Bonebreak-ENGLISH-name)",
            bonebreakSkeletonMinion.getDisplayName(stubTranslations, Language.ENGLISH)
        )
        assertEquals("Duriel-GERMAN-name (q)", durielMonsterQuest.getDisplayName(stubTranslations, Language.GERMAN))
    }

    @ParameterizedTest
    @MethodSource("desecratedMonsterLevelProvider")
    fun getDesecratedMonsterLevel(
        difficulty: Difficulty,
        monsterTypes: Set<MonsterType>,
        monsterLevel: Int,
        characterLevel: Int,
        expectedLevel: Int
    ) {
        monsterTypes.forEach { monsterType ->
            assertEquals(
                expectedLevel,
                createMonster(difficulty, monsterLevel, monsterType).getDesecratedMonsterLevel(characterLevel)
            )
        }
    }

    private fun createMonster(difficulty: Difficulty, level: Int, monsterType: MonsterType): Monster =
        skeletonMonster.copy(difficulty = difficulty, level = level, type = monsterType)

    companion object {
        @JvmStatic
        fun desecratedMonsterLevelProvider(): Stream<Arguments> {
            return sequenceOf(
                arguments(NORMAL, setOf(REGULAR), 10, 9, 10),
                arguments(NORMAL, setOf(REGULAR), 10, 10, 12),
                arguments(NORMAL, setOf(REGULAR), 10, 11, 13),
                arguments(NORMAL, setOf(REGULAR), 10, 42, 44),
                arguments(NORMAL, setOf(REGULAR), 10, 43, 45),
                arguments(NORMAL, setOf(REGULAR), 10, 44, 45),
                arguments(NORMAL, setOf(REGULAR), 10, 99, 45),
                arguments(NORMAL, setOf(REGULAR), 45, 99, 45),
                arguments(NORMAL, setOf(REGULAR), 50, 99, 45),
                arguments(NORMAL, setOf(CHAMPION), 10, 9, 10),
                arguments(NORMAL, setOf(CHAMPION), 10, 10, 14),
                arguments(NORMAL, setOf(CHAMPION), 10, 11, 15),
                arguments(NORMAL, setOf(CHAMPION), 10, 42, 46),
                arguments(NORMAL, setOf(CHAMPION), 10, 43, 47),
                arguments(NORMAL, setOf(CHAMPION), 10, 44, 47),
                arguments(NORMAL, setOf(CHAMPION), 10, 99, 47),
                arguments(NORMAL, setOf(CHAMPION), 45, 99, 47),
                arguments(NORMAL, setOf(CHAMPION), 50, 99, 47),
                arguments(NORMAL, setOf(UNIQUE, SUPERUNIQUE, BOSS, MINION), 10, 9, 10),
                arguments(NORMAL, setOf(UNIQUE, SUPERUNIQUE, BOSS, MINION), 10, 10, 15),
                arguments(NORMAL, setOf(UNIQUE, SUPERUNIQUE, BOSS, MINION), 10, 11, 16),
                arguments(NORMAL, setOf(UNIQUE, SUPERUNIQUE, BOSS, MINION), 10, 42, 47),
                arguments(NORMAL, setOf(UNIQUE, SUPERUNIQUE, BOSS, MINION), 10, 43, 48),
                arguments(NORMAL, setOf(UNIQUE, SUPERUNIQUE, BOSS, MINION), 10, 44, 48),
                arguments(NORMAL, setOf(UNIQUE, SUPERUNIQUE, BOSS, MINION), 10, 99, 48),
                arguments(NORMAL, setOf(UNIQUE, SUPERUNIQUE, BOSS, MINION), 45, 99, 48),
                arguments(NORMAL, setOf(UNIQUE, SUPERUNIQUE, BOSS, MINION), 50, 99, 48),

                arguments(NIGHTMARE, setOf(REGULAR), 10, 9, 10),
                arguments(NIGHTMARE, setOf(REGULAR), 10, 10, 12),
                arguments(NIGHTMARE, setOf(REGULAR), 10, 11, 13),
                arguments(NIGHTMARE, setOf(REGULAR), 10, 68, 70),
                arguments(NIGHTMARE, setOf(REGULAR), 10, 69, 71),
                arguments(NIGHTMARE, setOf(REGULAR), 10, 70, 71),
                arguments(NIGHTMARE, setOf(REGULAR), 10, 99, 71),
                arguments(NIGHTMARE, setOf(REGULAR), 45, 99, 71),
                arguments(NIGHTMARE, setOf(REGULAR), 50, 99, 71),
                arguments(NIGHTMARE, setOf(CHAMPION), 10, 9, 10),
                arguments(NIGHTMARE, setOf(CHAMPION), 10, 10, 14),
                arguments(NIGHTMARE, setOf(CHAMPION), 10, 11, 15),
                arguments(NIGHTMARE, setOf(CHAMPION), 10, 68, 72),
                arguments(NIGHTMARE, setOf(CHAMPION), 10, 69, 73),
                arguments(NIGHTMARE, setOf(CHAMPION), 10, 70, 73),
                arguments(NIGHTMARE, setOf(CHAMPION), 10, 99, 73),
                arguments(NIGHTMARE, setOf(CHAMPION), 45, 99, 73),
                arguments(NIGHTMARE, setOf(CHAMPION), 50, 99, 73),
                arguments(NIGHTMARE, setOf(UNIQUE, SUPERUNIQUE, BOSS, MINION), 10, 9, 10),
                arguments(NIGHTMARE, setOf(UNIQUE, SUPERUNIQUE, BOSS, MINION), 10, 10, 15),
                arguments(NIGHTMARE, setOf(UNIQUE, SUPERUNIQUE, BOSS, MINION), 10, 11, 16),
                arguments(NIGHTMARE, setOf(UNIQUE, SUPERUNIQUE, BOSS, MINION), 10, 68, 73),
                arguments(NIGHTMARE, setOf(UNIQUE, SUPERUNIQUE, BOSS, MINION), 10, 69, 74),
                arguments(NIGHTMARE, setOf(UNIQUE, SUPERUNIQUE, BOSS, MINION), 10, 70, 74),
                arguments(NIGHTMARE, setOf(UNIQUE, SUPERUNIQUE, BOSS, MINION), 10, 99, 74),
                arguments(NIGHTMARE, setOf(UNIQUE, SUPERUNIQUE, BOSS, MINION), 45, 99, 74),
                arguments(NIGHTMARE, setOf(UNIQUE, SUPERUNIQUE, BOSS, MINION), 50, 99, 74),

                arguments(HELL, setOf(REGULAR), 10, 9, 10),
                arguments(HELL, setOf(REGULAR), 10, 10, 12),
                arguments(HELL, setOf(REGULAR), 10, 11, 13),
                arguments(HELL, setOf(REGULAR), 10, 93, 95),
                arguments(HELL, setOf(REGULAR), 10, 94, 96),
                arguments(HELL, setOf(REGULAR), 10, 95, 96),
                arguments(HELL, setOf(REGULAR), 10, 102, 96),
                arguments(HELL, setOf(REGULAR), 45, 102, 96),
                arguments(HELL, setOf(REGULAR), 50, 102, 96),
                arguments(HELL, setOf(CHAMPION), 10, 9, 10),
                arguments(HELL, setOf(CHAMPION), 10, 10, 14),
                arguments(HELL, setOf(CHAMPION), 10, 11, 15),
                arguments(HELL, setOf(CHAMPION), 10, 93, 97),
                arguments(HELL, setOf(CHAMPION), 10, 94, 98),
                arguments(HELL, setOf(CHAMPION), 10, 95, 98),
                arguments(HELL, setOf(CHAMPION), 10, 102, 98),
                arguments(HELL, setOf(CHAMPION), 45, 102, 98),
                arguments(HELL, setOf(CHAMPION), 50, 102, 98),
                arguments(HELL, setOf(UNIQUE, SUPERUNIQUE, BOSS, MINION), 10, 9, 10),
                arguments(HELL, setOf(UNIQUE, SUPERUNIQUE, BOSS, MINION), 10, 10, 15),
                arguments(HELL, setOf(UNIQUE, SUPERUNIQUE, BOSS, MINION), 10, 11, 16),
                arguments(HELL, setOf(UNIQUE, SUPERUNIQUE, BOSS, MINION), 10, 93, 98),
                arguments(HELL, setOf(UNIQUE, SUPERUNIQUE, BOSS, MINION), 10, 94, 99),
                arguments(HELL, setOf(UNIQUE, SUPERUNIQUE, BOSS, MINION), 10, 95, 99),
                arguments(HELL, setOf(UNIQUE, SUPERUNIQUE, BOSS, MINION), 10, 102, 99),
                arguments(HELL, setOf(UNIQUE, SUPERUNIQUE, BOSS, MINION), 45, 102, 99),
                arguments(HELL, setOf(UNIQUE, SUPERUNIQUE, BOSS, MINION), 50, 102, 99),

                ).asStream()
        }
    }

}