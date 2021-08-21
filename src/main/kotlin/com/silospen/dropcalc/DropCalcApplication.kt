package com.silospen.dropcalc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class DropCalcApplication

fun main(args: Array<String>) {
    runApplication<DropCalcApplication>(*args)
}
