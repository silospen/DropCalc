package com.silospen.dropcalc

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import java.io.File
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class LargeIntegTestRunner(private val jacksonObjectMapper: ObjectMapper) {

    private val threadPool = Executors.newFixedThreadPool(6)

    fun <T : Any> generateTestData(file: File, dataGenerator: (Counter) -> List<Callable<T>>) {
        val counter = Counter()
        TestDataExpectationWriter.init(file).use { writer ->
            threadPool.invokeAll(dataGenerator(counter))
                .forEach { writer.write(it.get()) }
        }
    }

    fun <T> runTests(file: File, actualGenerator: (T) -> T, typeReference: TypeReference<List<T>>) {
        val counter = Counter()
        val tests = jacksonObjectMapper.readValue(file, typeReference)
            .map { expected ->
                Callable {
                    assertEquals(
                        expected,
                        actualGenerator(expected)
                    )
                    counter.incrementAndPossiblyPrint()
                }
            }
        threadPool.invokeAll(tests).forEach { it.get() }
    }
}