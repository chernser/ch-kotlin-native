package com.clickhouse.client

import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals


class TabSeparatedDataTest {
    @Test
    fun getValue() = runBlocking {

        val input = ByteReadChannel("value1\tvalue2\tvalue3\n" +
                "value1\tvalue2\tvalue3\n")
        val data = TabSeparatedData(input, "TabSeparated")

        for ( i in 0..1) {
            data.advanceRow()
            assertEquals("value1", data.getValue())
            assertEquals("value1", data.getValue())

            data.advanceColumn()
            assertEquals("value2", data.getValue())
            data.advanceColumn()
            assertEquals("value3", data.getValue())
        }
    }
}

