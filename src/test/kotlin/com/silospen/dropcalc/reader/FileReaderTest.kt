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
        val lineParser = object : LineParser<Pair<String, String>> {
            override fun parseLine(line: List<String>) =
                if (line.contains("ignore")) null else Pair(line[1], line[0])
        }
        val actual = readTsv(tempFile, lineParser)
        val expected = listOf(Pair("val2", "val1"), Pair("val4", "val3"))
        assertEquals(expected, actual)
    }
}