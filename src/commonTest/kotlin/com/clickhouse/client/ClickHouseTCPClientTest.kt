package com.clickhouse.client

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.TimeSource

class ClickHouseTCPClientTest {

    val client = ClickHouseTCPClient("localhost", 9000,
        "default", "", db = "default")

    @Test
    fun `connection test`() = runBlocking {
        client.connect().onSuccess {
            val connection = it

            delay(1000)
            assertTrue(client.ping())

            client.disconnect()
        }
        return@runBlocking
    }

    @Test
    fun `simplest query`() = runBlocking {
        val stmts = listOf(
            "SELECT 1",
            "SELECT number FROM system.numbers"
        )

        client.connect().getOrNull()
        val params = HashMap<String, String>()
        val opSettings = ClickHouseTCPClient.OperationSettings()
        stmts.forEach {
            client.query(it, generateId(), params, opSettings)
        }
    }

    fun generateId(): String =
        TimeSource.Monotonic.markNow()
            .elapsedNow()
            .inWholeNanoseconds
            .toString(16) // hex is more ID-looking
}