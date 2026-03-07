package com.clickhouse.client.com.clickhouse.protocol.tcp

import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlin.experimental.and

class PacketChannelCodec(val output: ByteWriteChannel, val input: ByteReadChannel) {


    private val isLittleEndian = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN

    suspend fun writePacket(packet: BasePacket) {
        writeVarUInt(packet.definition.id);
        packet.definition.fields.forEach { field ->
            writeField(packet.values[field.name], field)
        }
        output.flush()
    }

    suspend fun writeField(value: Any?, field: FieldDefinition) {
        try {
            when (field.type) {
                FieldType.VarInt -> writeVarUInt(value as UInt)
                FieldType.String -> writeBinaryString(value as String)
                FieldType.Byte -> writeByte(value as Byte)
                FieldType.Short -> writeShort(value as Short)
                FieldType.Int -> writeInt(value as Int)
                FieldType.Long -> writeLong(value as Long)
                else -> throw RuntimeException("Unexpected field type: ${field.type}")
            }
        } catch (t: Throwable) {
            throw RuntimeException("Failed writing field ${field.name} of type: ${field.type}", t)
        }
    }

    suspend fun writeFields(fields: List<FieldDefinition>, values: Map<String, Any?>) {
        for (f in fields) {
            writeField(values[f.name], f)
        }
    }

    suspend fun readPacket(packet: BasePacket) {

    }

    suspend fun writeInt(value: Int) {
        if (isLittleEndian) {
            output.writeInt(value)
        } else {
            output.writeByte((value and 0xFF).toByte())
            output.writeByte(((value shr 8) and 0xFF).toByte())
            output.writeByte(((value shr 16) and 0xFF).toByte())
            output.writeByte(((value shr 24) and 0xFF).toByte())
        }
    }

    suspend fun writeLong(value: Long) {
        if (isLittleEndian) {
            output.writeLong(value)
        } else {
            output.writeByte((value and 0xFF).toByte())
            output.writeByte(((value shr 8) and 0xFF).toByte())
            output.writeByte(((value shr 16) and 0xFF).toByte())
            output.writeByte(((value shr 24) and 0xFF).toByte())
            output.writeByte(((value shr 32) and 0xFF).toByte())
            output.writeByte(((value shr 40) and 0xFF).toByte())
            output.writeByte(((value shr 48) and 0xFF).toByte())
            output.writeByte(((value shr 56) and 0xFF).toByte())
        }
    }

    suspend fun writeShort(value: Short) {
        if (isLittleEndian) {
            output.writeShort(value)
        } else {
            output.writeByte((value.toInt() and 0xFF).toByte())
            output.writeByte(((value.toInt() shr 8) and 0xFF).toByte())
        }
    }

    suspend fun writeByte(value: Byte) {
        output.writeByte(value)
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
        output.writeFully(src, offset, length)
    }

    suspend fun flush() = output.flush()

    fun close() = output.close()

    suspend fun writeBinaryString(str: String): UInt {
        val size = str.length.toUInt()
        val bCount = this.writeVarUInt(size);

        output.writeStringUtf8(str)
        return bCount + size
    }
    suspend fun writeVarUInt(value: UInt): UInt {
        var v = value
        var i = 0u
        while (v > 0x7Fu) {
            val x = (v and 0x7Fu)
            val b: UByte =( 0x80u or x).toUByte()
            this.writeByte(b.toByte())
            v = v shr 7
            i++
        }

        this.writeByte(v.toByte())
        i++
        return i
    }


    private suspend fun readBinaryString(): String {
        val size = readVarUInt()
        val dst = ByteArray(size.toInt())
        input.readFully(dst)
        return dst.decodeToString()
    }


    private suspend fun readVarUInt(): UInt {
        var r = 0u
        var bitShift = 0;

        for ( i in 0..10) {

            val b = input.readByte()
            val valuePart = (b and 0x7f).toUInt()
            r = r or (valuePart shl bitShift)
            if (b.toInt() and 0x80 == 0) {
                return r
            }
            bitShift += 7

        }
        return r
    }


}