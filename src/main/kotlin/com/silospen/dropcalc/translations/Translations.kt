package com.silospen.dropcalc.translations

import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder.LITTLE_ENDIAN

interface Translations {
    fun getTranslation(key: String): String?
}

class CompositeTranslations(private vararg val translations: Translations) : Translations {
    override fun getTranslation(key: String): String? =
        translations.asSequence().firstNotNullOfOrNull { it.getTranslation(key) }
}

class MapBasedTranslations(private val translationData: Map<String, String>) : Translations {
    companion object {
        fun loadTranslations(file: File): Translations {
            val buffer = ByteBuffer.wrap(file.readBytes()).order(LITTLE_ENDIAN)
            val headerBuffer = buffer.sliceKeepEndian(0, 21)
            val header = TableFileHeader.fromByteBuffer(headerBuffer)
            val hashTableBuffer =
                buffer.sliceKeepEndian(21 + (header.numElements * 2), header.hashTableSize.toInt() * 17)
            return MapBasedTranslations(readDataTable(readHashTable(hashTableBuffer), buffer))
        }

        private fun readDataTable(hashTable: List<TableFileHashEntries>, buffer: ByteBuffer): Map<String, String> =
            hashTable
                .asSequence()
                .filter { it.used > 0 }
                .map {
                    buffer.getString(
                        it.keyOffset.toInt(),
                        it.valueOffset.toInt() - it.keyOffset.toInt()
                    ) to buffer.getString(it.valueOffset.toInt(), it.stringLength)
                }.toMap()

        private fun readHashTable(hashTableBuffer: ByteBuffer): MutableList<TableFileHashEntries> {
            val hashTableAccumulator = mutableListOf<TableFileHashEntries>()
            while (hashTableBuffer.hasRemaining()) {
                hashTableAccumulator.add(
                    TableFileHashEntries.fromByteBuffer(
                        hashTableBuffer.slice(hashTableBuffer.position(), 17).order(LITTLE_ENDIAN)
                    )
                )
                hashTableBuffer.position(hashTableBuffer.position() + 17)
            }
            return hashTableAccumulator
        }
    }

    override fun getTranslation(key: String) = translationData[key]


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
private fun ByteBuffer.getString(offset: Int, length: Int) =
    ByteArray(length).apply { get(offset, this) }.run { decodeToString().removeSuffix("\u0000") }

private fun ByteBuffer.sliceKeepEndian(index: Int, length: Int) = this.slice(index, length).order(this.order())