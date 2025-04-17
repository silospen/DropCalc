package com.silospen.dropcalc

import com.silospen.dropcalc.Language.BRAZILIAN
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource

class LanguageTest {

    @ParameterizedTest
    @CsvFileSource(resources = ["/languageTestData.csv"])
    fun testLanguageParsing(expected: String, input: String) {
        assertEquals(Language.valueOf(expected), Language.forLangAttribute(input))
    }

    @Test
    fun testLanguageParsingDefault() {
        assertEquals(BRAZILIAN, Language.forLangAttribute(null, BRAZILIAN))
        assertEquals(BRAZILIAN, Language.forLangAttribute("nonsense", BRAZILIAN))
    }

}