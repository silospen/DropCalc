package com.silospen.dropcalc

import org.junit.jupiter.api.Assertions.assertEquals
import tools.jackson.core.JsonGenerator
import tools.jackson.core.type.TypeReference
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.json.JsonMapper
import java.io.File
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicLong

class LargeIntegTestRunner(private val jacksonObjectMapper: JsonMapper) {

    private val threadPool = Executors.newFixedThreadPool(8)

    fun <T : Any> generateTestData(file: File, dataGenerator: (Counter) -> List<Callable<T>>) {
        val counter = Counter()
        TestDataExpectationWriter.init(file, jacksonObjectMapper).use { writer ->
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

class TestDataExpectationWriter(private val jsonGenerator: JsonGenerator) : AutoCloseable {

    companion object {
        fun init(file: File, mapper: ObjectMapper): TestDataExpectationWriter {
            val jsonGenerator = mapper.createGenerator(file.bufferedWriter())
            jsonGenerator.writeStartArray()
            return TestDataExpectationWriter(jsonGenerator)
        }
    }

    fun write(o: Any) {
        jsonGenerator.writePOJO(o)
        jsonGenerator.writeRaw("\n")
    }

    override fun close() {
        jsonGenerator.writeEndArray()
        jsonGenerator.close()
    }
}

data class Counter(private val counter: AtomicLong = AtomicLong(0L)) {
    fun incrementAndPossiblyPrint() {
        val value = counter.incrementAndGet()
        if (value % 1000 == 0L) println(value)
    }
}

internal data class ApiResponseEntry(val name: String, val area: String, val prob: String)
