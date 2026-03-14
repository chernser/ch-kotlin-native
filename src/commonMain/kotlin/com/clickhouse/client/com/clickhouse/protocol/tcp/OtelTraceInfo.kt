package com.clickhouse.client.com.clickhouse.protocol.tcp


class OtelTraceInfo {

    val definition = FragmentDefinition("traceInfo", listOf())

    companion object {

        val traceIdF = byte("traceId", Versions.MIN_REVISION_WITH_OPENTELEMETRY)
        val uuidWord1F = long("uuidWord1", Versions.MIN_REVISION_WITH_CLIENT_INFO)
        val uuidWord2F = long("uuidWord2", Versions.MIN_REVISION_WITH_CLIENT_INFO)
        val spanId = long("spanId", Versions.MIN_REVISION_WITH_CLIENT_INFO)
        val taceState = string("taceState", Versions.MIN_REVISION_WITH_CLIENT_INFO)
        val traceFlags = byte("traceFlags", Versions.MIN_REVISION_WITH_CLIENT_INFO)
    }
}