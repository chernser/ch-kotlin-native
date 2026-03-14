package com.clickhouse.client.com.clickhouse.protocol.tcp

import com.clickhouse.client.ClickHouseTCPClient.ProtoVersions.DBMS_MIN_PROTOCOL_VERSION_WITH_PARAMETERS

class QueryReq : BasePacket(
    PacketDefinition(
        Packets.Client.Query, "QueryReq", listOf(
            queryIdF,
            // query Info
            settingsFormatF,
            settingsF,
            interServerSecretF,
            queryStageF,
            compressionFlagF,
            sqlF,
            queryParamsFormatF,
            queryParamsF,
        )

    )
) {

    companion object {
        val queryIdF = string("queryId", 0u)
        val settingsFormatF = byte("settingsFormat", 0u)
        val settingsF = string("settings", 0u)
        val interServerSecretF = string("interServerSecret", 0u)

        val queryStageF = varLong("queryStage", 0u)

        val compressionFlagF = varLong("compressionFlag", 0u)

        val sqlF = string("sqlF", 0u)

        val queryParamsFormatF = varLong("queryParamsFormat", DBMS_MIN_PROTOCOL_VERSION_WITH_PARAMETERS)
        val queryParamsF = string("queryParams", DBMS_MIN_PROTOCOL_VERSION_WITH_PARAMETERS)
    }
}