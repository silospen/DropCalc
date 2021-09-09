package com.silospen.dropcalc.translations

import com.silospen.dropcalc.files.getResource
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TranslationsTest {
    @Test
    fun loadTranslations() {
        val actual =
            Translations.loadTranslations(getResource("translationsTestData/translationsTestData.bin"))
        val expected = Translations(
            mapOf("ModStr4l" to "Slightly Increased Attack Speed", "ModStr4n" to "Greatly Increased Attack Speed")
        )
        assertEquals(expected, actual)
        assertEquals(null, actual.getTranslation("missing-key"))
        assertEquals("Greatly Increased Attack Speed", actual.getTranslation("ModStr4n"))
    }
}