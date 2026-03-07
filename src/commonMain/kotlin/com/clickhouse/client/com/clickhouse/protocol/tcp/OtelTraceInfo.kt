package com.clickhouse.client.com.clickhouse.protocol.tcp

import com.clickhouse.client.ClickHouseTCPClient.ProtoVersions.DBMS_MIN_REVISION_WITH_CLIENT_INFO
import com.clickhouse.client.ClickHouseTCPClient.ProtoVersions.DBMS_MIN_REVISION_WITH_OPENTELEMETRY

class OtelTraceInfo {

    val definition = FragmentDefinition("traceInfo", listOf())

    companion object {

        val traceIdF = byte("traceId", DBMS_MIN_REVISION_WITH_OPENTELEMETRY)
        val uuidWord1F = long("uuidWord1", DBMS_MIN_REVISION_WITH_CLIENT_INFO)
        val uuidWord2F = long("uuidWord2", DBMS_MIN_REVISION_WITH_CLIENT_INFO)
        val spanId = long("spanId", DBMS_MIN_REVISION_WITH_CLIENT_INFO)
        val taceState = string("taceState", DBMS_MIN_REVISION_WITH_CLIENT_INFO)
        val traceFlags = byte("traceFlags", DBMS_MIN_REVISION_WITH_CLIENT_INFO)
    }
}