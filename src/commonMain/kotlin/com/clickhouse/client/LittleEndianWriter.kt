package com.clickhouse.client

import io.ktor.utils.io.*
import io.ktor.utils.io.core.*

/**
 * Ensures data is written in little endian
 */
class LittleEndianWriter(public val channel: ByteWriteChannel) {

    private val isLittleEndian = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN

    suspend fun writeInt(value: Int) {
        if (isLittleEndian) {
            channel.writeInt(value)
        } else {
            channel.writeByte((value and 0xFF).toByte())
            channel.writeByte(((value shr 8) and 0xFF).toByte())
            channel.writeByte(((value shr 16) and 0xFF).toByte())
            channel.writeByte(((value shr 24) and 0xFF).toByte())
        }
    }

    suspend fun writeLong(value: Long) {
        if (isLittleEndian) {
            channel.writeLong(value)
        } else {
            channel.writeByte((value and 0xFF).toByte())
            channel.writeByte(((value shr 8) and 0xFF).toByte())
            channel.writeByte(((value shr 16) and 0xFF).toByte())
            channel.writeByte(((value shr 24) and 0xFF).toByte())
            channel.writeByte(((value shr 32) and 0xFF).toByte())
            channel.writeByte(((value shr 40) and 0xFF).toByte())
            channel.writeByte(((value shr 48) and 0xFF).toByte())
            channel.writeByte(((value shr 56) and 0xFF).toByte())
        }
    }

    suspend fun writeShort(value: Short) {
        if (isLittleEndian) {
            channel.writeShort(value)
        } else {
            channel.writeByte((value.toInt() and 0xFF).toByte())
            channel.writeByte(((value.toInt() shr 8) and 0xFF).toByte())
        }
    }

    suspend fun writeByte(value: Byte) {
        channel.writeByte(value)
    }

    suspend fun writeFloat(value: Float) {
        writeInt(value.toRawBits())
    }

    suspend fun writeDouble(value: Double) {
        writeLong(value.toRawBits())
    }

    // Unsigned variants
    suspend fun writeUInt(value: UInt) {
        writeInt(value.toInt())
    }

    suspend fun writeULong(value: ULong) {
        writeLong(value.toLong())
    }

    suspend fun writeUShort(value: UShort) {
        writeShort(value.toShort())
    }

    suspend fun writeUByte(value: UByte) {
        writeByte(value.toByte())
    }

    // Pass through methods
    suspend fun writeFully(src: ByteArray, offset: Int = 0, length: Int = src.size - offset) {
        channel.writeFully(src, offset, length)
    }

    suspend fun flush() = channel.flush()

    fun close() = channel.close()

    suspend fun writeBinaryString(str: String): UInt {
        val size = str.length.toUInt()
        val bCount = this.writeVarUInt(size.toULong());

        channel.writeStringUtf8(str)
        return bCount + size
    }
    suspend fun writeVarUInt(value: ULong): UInt {
        var v = value
        var i = 0u
        while (v > 0x7Fu) {
            val x = (v and 0x7Fu).toUInt()
            val b: UByte =( 0x80u or x).toUByte()
            this.writeByte(b.toByte())
            v = v shr 7
            i++
        }

        this.writeByte(v.toByte())
        i++
        return i
    }
}