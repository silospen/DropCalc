package com.silospen.dropcalc.files

import java.io.File

fun <T : Any> readTsv(file: File, lineParser: LineParser<T?>): List<T> =
    file.useLines { lines ->
        lines.drop(1)
            .map { it.split('\t') }
            .map(lineParser::parseLine)
            .filterNotNull()
            .toList()
    }

interface LineParser<T> {
    fun parseLine(line: List<String>): T?
}