package com.clickhouse.client

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertTrue

class ClickHouseTCPClientTest {


    @Test
    fun testConnect() = runBlocking {

        val client = ClickHouseTCPClient("localhost", 9000)

        client.connect().onSuccess {
            val connection = it

            delay(1000)
            assertTrue(client.ping())

            client.disconnect()
        }
        return@runBlocking
    }
}