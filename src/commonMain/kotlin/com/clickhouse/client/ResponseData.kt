package com.clickhouse.client

interface ResponseData {

    suspend fun getValue(): Any?

    suspend fun getValue(colIndex: UInt): Any?

    suspend fun advanceColumn()

    suspend fun advanceRow()

    fun canAdvanceRow(): Boolean

    fun canAdvanceColumn(): Boolean

    suspend fun columnName(): String?

    suspend fun columnType(): String?

    suspend fun columnIndex(name: String): UInt
}
