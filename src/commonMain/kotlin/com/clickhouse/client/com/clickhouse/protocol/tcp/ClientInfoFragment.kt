package com.clickhouse.client.com.clickhouse.protocol.tcp

class ClientInfoFragment : BaseFragment(

    definition = FragmentDefinition(
        "hello_req", listOf(

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

        )
    )
) {

    companion object {
        val queryTypeF = byte("queryType", Versions.MIN_REVISION_WITH_CLIENT_INFO)
        val usernameF = string("username", Versions.MIN_REVISION_WITH_CLIENT_INFO)
        val queryIdF = string("queryId", Versions.MIN_REVISION_WITH_CLIENT_INFO)
        val ipAddressF = string("ipAddress", Versions.MIN_REVISION_WITH_CLIENT_INFO)
        val nowTimeF = double("timeNow", Versions.MIN_PROTOCOL_VERSION_WITH_INITIAL_QUERY_START_TIME)
        val osUserF = string("osUser", Versions.MIN_REVISION_WITH_CLIENT_INFO)
        val hostnameF = string("hostname", Versions.MIN_REVISION_WITH_CLIENT_INFO)
        val clientName = string("clientName", Versions.MIN_REVISION_WITH_CLIENT_INFO)
        val majorVersion = varInt("majorVersion", Versions.MIN_REVISION_WITH_CLIENT_INFO)
        val minorVersion = varInt("minorVersion", Versions.MIN_REVISION_WITH_CLIENT_INFO)
        val protoVersion = varInt("protoVersion", Versions.MIN_REVISION_WITH_CLIENT_INFO)
        val quotaKeyF = string("quotaKey", Versions.MIN_REVISION_WITH_QUOTA_KEY_IN_CLIENT_INFO)
        val clientVersionPatch = varInt("clientVersionPatch", Versions.MIN_REVISION_WITH_VERSION_PATCH)

        // trace info here
        val collaborateWithInitiatorF = varInt("collaborateWithInitiator", Versions.MIN_REVISION_WITH_PARALLEL_REPLICAS)
        val obsoleteCountPartReplicas =
            varInt("obsoleteCountPartReplicas", Versions.MIN_REVISION_WITH_PARALLEL_REPLICAS)
        val numOfCurrentReplicas = varInt("numOfCurrentRepclicas", Versions.MIN_REVISION_WITH_PARALLEL_REPLICAS)
    }
}