package com.clickhouse.client.com.clickhouse.protocol.tcp

class QueryReq : BasePacket(
    PacketDefinition(
        Packets.Client.Query, "QueryReq", listOf(
            queryIdF,
            clientInfoF,
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

        val clientInfoF = fragment("clientInfo", 0u)
        val settingsFormatF = byte("settingsFormat", 0u)
        val settingsF = string("settings", 0u)
        val interServerSecretF = string("interServerSecret", 0u)

        val queryStageF = varLong("queryStage", 0u)

        val compressionFlagF = varLong("compressionFlag", 0u)

        val sqlF = string("sqlF", 0u)

        val queryParamsFormatF = varLong("queryParamsFormat", Versions.MIN_PROTOCOL_VERSION_WITH_PARAMETERS)
        val queryParamsF = string("queryParams", Versions.MIN_PROTOCOL_VERSION_WITH_PARAMETERS)
    }
}