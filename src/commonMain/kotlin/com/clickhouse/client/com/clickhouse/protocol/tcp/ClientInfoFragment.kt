package com.clickhouse.client.com.clickhouse.protocol.tcp

class ClientInfoFragment : BaseFragment(

    definition = FragmentDefinition(
        "hello_req", listOf(

            queryTypeF,
            usernameF,
            queryIdF,
            ipAddressF,
            nowTimeF,
            infTypeF,
            osUserF,
            hostnameF,
            clientNameF,
            majorVersionF,
            minorVersionF,
            protoVersionF,
            quotaKeyF,
            distributionDepthF,
            clientVersionPatchF,

            OtelTraceInfoFragment.traceIdF,
            traceInfoF,

            collaborateWithInitiatorF,
            obsoleteCountPartReplicasF,
            numOfCurrentReplicasF
        )
    ),
    values = HashMap(mapOf(
        queryTypeF.name to QueryKind.INITIAL_QUERY,
        infTypeF.name to InterfaceType.TCP,
        majorVersionF.name to 1u,
        minorVersionF.name to 0u,
        OtelTraceInfoFragment.traceIdF.name to 0.toByte(),
    )),
) {

    companion object {
        val queryTypeF = byte("queryType", Versions.MIN_REVISION_WITH_CLIENT_INFO)
        val usernameF = string("username", Versions.MIN_REVISION_WITH_CLIENT_INFO)
        val queryIdF = string("queryId", Versions.MIN_REVISION_WITH_CLIENT_INFO)
        val ipAddressF = string("ipAddress", Versions.MIN_REVISION_WITH_CLIENT_INFO)
        val nowTimeF = double("timeNow", Versions.MIN_PROTOCOL_VERSION_WITH_INITIAL_QUERY_START_TIME)

        val infTypeF = byte("interfaceType", 0u)
        val osUserF = string("osUser", Versions.MIN_REVISION_WITH_CLIENT_INFO)
        val hostnameF = string("hostname", Versions.MIN_REVISION_WITH_CLIENT_INFO)
        val clientNameF = string("clientName", Versions.MIN_REVISION_WITH_CLIENT_INFO)
        val majorVersionF = varInt("majorVersion", Versions.MIN_REVISION_WITH_CLIENT_INFO)
        val minorVersionF = varInt("minorVersion", Versions.MIN_REVISION_WITH_CLIENT_INFO)
        val protoVersionF = varInt("protoVersion", Versions.MIN_REVISION_WITH_CLIENT_INFO)
        val quotaKeyF = string("quotaKey", Versions.MIN_REVISION_WITH_QUOTA_KEY_IN_CLIENT_INFO)
        val distributionDepthF = varInt("distributionDepth", Versions.MIN_PROTOCOL_VERSION_WITH_DISTRIBUTED_DEPTH)
        val clientVersionPatchF = varInt("clientVersionPatch", Versions.MIN_REVISION_WITH_VERSION_PATCH)

        val traceInfoF = fragment("traceInfo", Versions.MIN_REVISION_WITH_OPENTELEMETRY)
        val collaborateWithInitiatorF = varInt("collaborateWithInitiator", Versions.MIN_REVISION_WITH_PARALLEL_REPLICAS)
        val obsoleteCountPartReplicasF =
            varInt("obsoleteCountPartReplicas", Versions.MIN_REVISION_WITH_PARALLEL_REPLICAS)
        val numOfCurrentReplicasF = varInt("numOfCurrentReplicas", Versions.MIN_REVISION_WITH_PARALLEL_REPLICAS)
    }
}

object QueryKind {
    const val NO_QUERY: Byte = 0            /// Uninitialized object.
    const val INITIAL_QUERY: Byte = 1
    const val SECONDARY_QUERY : Byte = 2    /// Query that was initiated by another query for distributed or ON CLUSTER query execution.
};

private object InterfaceType {
    const val TCP: Byte = 1
    const val HTTP: Byte = 2
    const val GRPC: Byte = 3
    const val MYSQL: Byte = 4
    const val POSTGRESQL: Byte = 5
    const val LOCAL: Byte = 6
    const val TCP_INTERSERVER: Byte = 7
    const val PROMETHEUS: Byte = 8
}