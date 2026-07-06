package com.clickhouse.client.com.clickhouse.protocol.tcp

class QueryReq : BasePacket(
    PacketDefinition(
        Packets.Client.Query, "QueryReq", listOf(
            queryIdF,
            clientInfoF,
            settingsF,
            extraRolesF,
            interServerSecretF,
            queryStageF,
            compressionFlagF,
            sqlF,
            queryParamsF,
        )

    )
) {

    companion object {
        val queryIdF = string("queryId", 0u)

        val clientInfoF = fragment("clientInfo", 0u)

        val settingsF = string("settings", 0u)

        val extraRolesF = string("extraRoles", 0u)
        val interServerSecretF = string("interServerSecret", 0u)

        val queryStageF = varInt("queryStage", 0u)

        val compressionFlagF = varInt("compressionFlag", 0u)

        val sqlF = string("sqlF", 0u)
        val queryParamsF = string("queryParams", Versions.MIN_PROTOCOL_VERSION_WITH_PARAMETERS)
    }

    enum class QueryStage(val v: UInt) {
        FetchColumns(0u),
        WithMergeableState(1u),
        Complete(2u),
        WithMergeableStateAfterAggregation(3u),
        WithMergeableStateAfterAggregationAndLimit(4u),
        Max(5u),
        QueryPlan(7u), // when plan used
    }
}