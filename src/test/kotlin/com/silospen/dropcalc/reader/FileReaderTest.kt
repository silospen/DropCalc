package com.silospen.dropcalc.reader

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class FileReaderTest {
    @Test
    fun testReadTsv() {
        val tempFile = File.createTempFile("fileReaderTest", null)
        tempFile.deleteOnExit()
        tempFile.writeText("header1\theader2\nval1\tval2\nval3\tval4\nignore\tignore")
        val actual = readTsv(tempFile) { if (it.contains("ignore")) null else Pair(it[1], it[0]) }
        val expected = listOf(Pair("val2", "val1"), Pair("val4", "val3"))
        assertEquals(expected, actual)
    }
}