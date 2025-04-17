package com.silospen.dropcalc.translations

import com.silospen.dropcalc.Language
import com.silospen.dropcalc.files.getResource
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TranslationsTest {
    @Test
    fun loadTranslations() {
        val actual =
            MapBasedTranslations.loadTranslations(
                getResource("translationsTestData/translationsTestData.bin"),
                Language.ENGLISH
            )
        val expected = MapBasedTranslations(
            mapOf(
                Language.ENGLISH to mapOf(
                    "ModStr4l" to "Slightly Increased Attack Speed",
                    "ModStr4n" to "Greatly Increased Attack Speed"
                )
            )
        )
        assertEquals(expected, actual)
        assertEquals(null, actual.getTranslationOrNull("missing-key", Language.ENGLISH))
        assertEquals("Greatly Increased Attack Speed", actual.getTranslationOrNull("ModStr4n", Language.ENGLISH))
    }

    @Test
    fun getTranslationForUnsupportedLanguage() {
        val translations =
            MapBasedTranslations.loadTranslations(
                getResource("translationsTestData/translationsTestData.bin"),
                Language.ENGLISH
            )
        assertEquals("Greatly Increased Attack Speed", translations.getTranslationOrNull("ModStr4n", Language.RUSSIAN))
    }

    @Test
    fun loadTranslationsFromJsonFile() {
        val actual =
            MapBasedTranslations.loadTranslationsFromJsonFile(getResource("translationsTestData/translationsTestData.json"))
        val expected = MapBasedTranslations(
            mapOf(
                Language.ENGLISH to mapOf(
                    "ModStr4l" to "Slightly Increased Attack Speed",
                    "ModStr4n" to "Greatly Increased Attack Speed"
                ),
                Language.TAIWANESE to mapOf(
                    "ModStr4l" to "小幅攻擊速度提高",
                    "ModStr4n" to "大幅攻擊速度提高"
                ),
                Language.GERMAN to mapOf(
                    "ModStr4l" to "Leicht erhöhte Angriffsgeschwindigkeit",
                    "ModStr4n" to "Stark erhöhte Angriffsgeschwindigkeit"
                ),
                Language.SPANISH to mapOf(
                    "ModStr4l" to "Ligero aumento de velocidad de ataque",
                    "ModStr4n" to "Gran aumento de velocidad de ataque"
                ),
                Language.FRENCH to mapOf(
                    "ModStr4l" to "Vitesse d’attaque légèrement augmentée",
                    "ModStr4n" to "Vitesse d’attaque fortement augmentée"
                ),
                Language.ITALIAN to mapOf(
                    "ModStr4l" to "Velocità d'attacco leggermente aumentata",
                    "ModStr4n" to "Velocità d'attacco enormemente aumentata"
                ),
                Language.KOREAN to mapOf(
                    "ModStr4l" to "공격 속도 약간 증가",
                    "ModStr4n" to "공격 속도 대폭 증가"
                ),
                Language.POLISH to mapOf(
                    "ModStr4l" to "Lekko zwiększona szybkość ataku",
                    "ModStr4n" to "Znacznie zwiększona szybkość ataku"
                ),
                Language.MEXICAN to mapOf(
                    "ModStr4l" to "Aumenta levemente la velocidad de ataque",
                    "ModStr4n" to "Aumenta en gran medida la velocidad de ataque"
                ),
                Language.JAPANESE to mapOf(
                    "ModStr4l" to "攻撃速度上昇（小）",
                    "ModStr4n" to "攻撃速度上昇（大）"
                ),
                Language.BRAZILIAN to mapOf(
                    "ModStr4l" to "Velocidade de ataque ligeiramente aumentada",
                    "ModStr4n" to "Velocidade de ataque consideravelmente aumentada"
                ),
                Language.RUSSIAN to mapOf(
                    "ModStr4l" to "Скорость атаки слегка повышена",
                    "ModStr4n" to "Скорость атаки значительно повышена"
                ),
                Language.CHINESE to mapOf(
                    "ModStr4l" to "轻微提升攻击速度",
                    "ModStr4n" to "大幅提高攻击速度"
                ),
            )
        )
        assertEquals(expected, actual)
        assertEquals(null, actual.getTranslationOrNull("missing-key", Language.ENGLISH))
        assertEquals("Greatly Increased Attack Speed", actual.getTranslationOrNull("ModStr4n", Language.ENGLISH))
    }

}