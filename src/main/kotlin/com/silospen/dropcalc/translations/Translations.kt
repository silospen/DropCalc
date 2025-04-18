package com.silospen.dropcalc.translations

import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.silospen.dropcalc.Language
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder.LITTLE_ENDIAN

private val mapper = jsonMapper {
    addModule(kotlinModule())
}

interface Translations {
    fun getTranslationOrNull(key: String, language: Language): String?
    fun getTranslation(key: String, language: Language): String =
        getTranslationOrNull(key, language) ?: throw IllegalArgumentException("No translation for $key")
}

class CompositeTranslations(private vararg val translations: Translations) : Translations {
    override fun getTranslationOrNull(key: String, language: Language): String? =
        translations.firstNotNullOfOrNull { it.getTranslationOrNull(key, language) }
}

class MapBasedTranslations(private val translationData: Map<Language, Map<String, String>>) : Translations {
    companion object {
        fun loadTranslations(inputStream: InputStream, language: Language): Translations {
            val buffer = ByteBuffer.wrap(inputStream.readAllBytes()).order(LITTLE_ENDIAN)
            val headerBuffer = buffer.sliceKeepEndian(0, 21)
            val header = TableFileHeader.fromByteBuffer(headerBuffer)
            val hashTableBuffer =
                buffer.sliceKeepEndian(21 + (header.numElements * 2), header.hashTableSize.toInt() * 17)
            val translationData = readDataTable(readHashTable(hashTableBuffer), buffer)
            return MapBasedTranslations(mapOf(language to translationData))
        }

        fun loadTranslationsFromJsonFile(inputStream: InputStream): Translations {
            val result = Language.values().associateByTo(mutableMapOf(), { it }) { mutableMapOf<String, String>() }

            mapper.readTree(inputStream).forEach { json ->
                val key = json.get("Key").textValue()
                for (fieldName in json.fieldNames()) {
                    val language = Language.forD2String(fieldName)
                    language?.let {
                        result.getValue(language)
                            .put(key, json.get(fieldName).textValue().replace(Regex("^\\[[^]]+]"), ""))
                    }
                }
            }
            return MapBasedTranslations(result)
        }

        private fun readDataTable(hashTable: List<TableFileHashEntries>, buffer: ByteBuffer): Map<String, String> =
            hashTable
                .asSequence()
                .filter { it.used > 0 }
                .map {
                    buffer.position(it.keyOffset.toInt())
                    buffer.getString(
                        it.valueOffset.toInt() - it.keyOffset.toInt()
                    ).trim() to buffer.getString(it.stringLength)
                }.toMap()

        private fun readHashTable(hashTableBuffer: ByteBuffer): MutableList<TableFileHashEntries> {
            val hashTableAccumulator = mutableListOf<TableFileHashEntries>()
            while (hashTableBuffer.hasRemaining()) {
                hashTableAccumulator.add(
                    TableFileHashEntries.fromByteBuffer(
                        hashTableBuffer.slice().limit(17).order(LITTLE_ENDIAN)
                    )
                )
                hashTableBuffer.position(hashTableBuffer.position() + 17)
            }
            return hashTableAccumulator
        }
    }

    override fun getTranslationOrNull(key: String, language: Language): String? {
        return translationData.getOrDefault(language, translationData.getValue(Language.ENGLISH))[key]
    }


    override fun toString(): String {
        return "Translations(translationData=$translationData)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MapBasedTranslations

        if (translationData != other.translationData) return false

        return true
    }

    override fun hashCode(): Int {
        return translationData.hashCode()
    }
}

private data class TableFileHashEntries(
    val used: Short,
    val index: Int,
    val hashValue: Long,
    val keyOffset: Long,
    val valueOffset: Long,
    val stringLength: Int,
) {
    companion object {
        fun fromByteBuffer(buf: ByteBuffer) = TableFileHashEntries(
            buf.getUInt8(),
            buf.getUInt16(),
            buf.getUInt32(),
            buf.getUInt32(),
            buf.getUInt32(),
            buf.getUInt16(),
        )
    }
}

private data class TableFileHeader(
    val crc: Int,
    val numElements: Int,
    val hashTableSize: Long,
    val version: Short,
    val dataStartOffset: Long,
    val maxRetries: Long,
    val dataEndOffset: Long
) {
    companion object {
        fun fromByteBuffer(buf: ByteBuffer) = TableFileHeader(
            buf.getUInt16(),
            buf.getUInt16(),
            buf.getUInt32(),
            buf.getUInt8(),
            buf.getUInt32(),
            buf.getUInt32(),
            buf.getUInt32()
        )
    }
}

private fun ByteBuffer.getUInt8(): Short = java.lang.Byte.toUnsignedInt(get()).toShort()
private fun ByteBuffer.getUInt16(): Int = java.lang.Short.toUnsignedInt(short)

@Suppress("RemoveRedundantQualifierName")
private fun ByteBuffer.getUInt32(): Long = java.lang.Integer.toUnsignedLong(int)
private fun ByteBuffer.getString(length: Int) =
    ByteArray(length).apply { get(this) }.run { decodeToString().removeSuffix("\u0000") }

private fun ByteBuffer.sliceKeepEndian(index: Int, length: Int): ByteBuffer {
    val oldPosition = this.position()
    this.position(index)
    val slice = this.slice().limit(length).order(this.order())
    this.position(oldPosition)
    return slice
}