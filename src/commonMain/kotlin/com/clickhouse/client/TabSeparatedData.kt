package com.clickhouse.client

import io.ktor.utils.io.*

class TabSeparatedData(val data: ByteReadChannel, val format: String): ResponseData {

    init {

    }

    var curValue: Any? = null
    var lineBuffer: String = ""
    var curPos: Int = 0

    override suspend fun getValue(): Any? {
        if (lineBuffer.equals("")) {
            throw IllegalStateException("requires to advance column or row")
        }

        return curValue
    }

    override suspend fun getValue(colIndex: UInt): Any? {
        if (lineBuffer.equals("")) {
            throw IllegalStateException("requires to advance column or row")
        }

        return null;
    }

    override suspend fun advanceColumn() {
        if (lineBuffer.equals("")) {
            throw IllegalStateException("requires advance row first")
        }

        if (curPos >= lineBuffer.length) {
            throw IllegalStateException("cannot advance column: end of line")
        }

        val pos = lineBuffer.indexOf('\t', curPos)
        val endIndex = if (pos > -1) pos else lineBuffer.length
        curValue = lineBuffer.substring(curPos, endIndex)
        curPos = endIndex + 1
    }

    override suspend fun advanceRow() {
        lineBuffer = data.readUTF8Line()!!
        curPos = 0

        advanceColumn()
    }

    override fun canAdvanceRow(): Boolean {
        return true
    }

    override fun canAdvanceColumn(): Boolean {
        return curPos >= lineBuffer.length
    }

    override suspend fun columnName(): String? {
        TODO("Not yet implemented")
    }

    override suspend fun columnType(): String? {
        TODO("Not yet implemented")
    }

    override suspend fun columnIndex(name: String): UInt {
        TODO("Not yet implemented")
    }
}