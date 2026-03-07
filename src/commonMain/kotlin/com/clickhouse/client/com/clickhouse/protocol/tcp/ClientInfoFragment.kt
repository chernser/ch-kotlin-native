package com.clickhouse.client.com.clickhouse.protocol.tcp

import com.clickhouse.client.ClickHouseTCPClient.ProtoVersions.DBMS_MIN_PROTOCOL_VERSION_WITH_INITIAL_QUERY_START_TIME
import com.clickhouse.client.ClickHouseTCPClient.ProtoVersions.DBMS_MIN_REVISION_WITH_CLIENT_INFO
import com.clickhouse.client.ClickHouseTCPClient.ProtoVersions.DBMS_MIN_REVISION_WITH_PARALLEL_REPLICAS
import com.clickhouse.client.ClickHouseTCPClient.ProtoVersions.DBMS_MIN_REVISION_WITH_QUOTA_KEY_IN_CLIENT_INFO
import com.clickhouse.client.ClickHouseTCPClient.ProtoVersions.DBMS_MIN_REVISION_WITH_VERSION_PATCH

class ClientInfoFragment  {

    val fragment = FragmentDefinition("hello_req", listOf(

        queryTypeF,
        usernameF,
        queryIdF,
        ipAddressF,
        nowTimeF,
        osUserF,
        

        // trace info segment
        OtelTraceInfo.traceIdF,
        OtelTraceInfo.uuidWord1F,
        OtelTraceInfo.uuidWord2F,
        OtelTraceInfo.spanId,
        OtelTraceInfo.taceState,
        OtelTraceInfo.traceFlags,
        // - trace info segment end

    ))

    companion object {
        val queryTypeF = byte("queryType", DBMS_MIN_REVISION_WITH_CLIENT_INFO)
        val usernameF = string("username", DBMS_MIN_REVISION_WITH_CLIENT_INFO)
        val queryIdF = string("queryId", DBMS_MIN_REVISION_WITH_CLIENT_INFO)
        val ipAddressF = string("ipAddress", DBMS_MIN_REVISION_WITH_CLIENT_INFO)
        val nowTimeF = double("timeNow", DBMS_MIN_PROTOCOL_VERSION_WITH_INITIAL_QUERY_START_TIME)
        val osUserF = string("osUser", DBMS_MIN_REVISION_WITH_CLIENT_INFO)
        val hostnameF = string("hostname", DBMS_MIN_REVISION_WITH_CLIENT_INFO)
        val clientName = string("clientName", DBMS_MIN_REVISION_WITH_CLIENT_INFO)
        val majorVersion = varInt("majorVersion", DBMS_MIN_REVISION_WITH_CLIENT_INFO)
        val minorVersion = varInt("minorVersion", DBMS_MIN_REVISION_WITH_CLIENT_INFO)
        val protoVersion = varInt("protoVersion", DBMS_MIN_REVISION_WITH_CLIENT_INFO)
        val quotaKeyF = string("quotaKey", DBMS_MIN_REVISION_WITH_QUOTA_KEY_IN_CLIENT_INFO)
        val clientVersionPatch = varInt("clientVersionPatch", DBMS_MIN_REVISION_WITH_VERSION_PATCH)
        // trace info here
        val collaborateWithInitiatorF = varInt("collaborateWithInitiator", DBMS_MIN_REVISION_WITH_PARALLEL_REPLICAS)
        val obsoleteCountPartReplicas = varInt("obsoleteCountPartReplicas", DBMS_MIN_REVISION_WITH_PARALLEL_REPLICAS)
        val numOfCurrentReplicas = varInt("numOfCurrentRepclicas", DBMS_MIN_REVISION_WITH_PARALLEL_REPLICAS)
    }
}