package com.silospen.dropcalc.reader

import java.io.File

fun <T> readTsv(file: File, lineParser: (List<String>) -> T): List<T> =
    file.useLines { lines ->
        lines.drop(1)
            .map { it.split('\t') }
            .map(lineParser)
            .filter { it != null }
            .toList()
    }
