package com.clickhouse.client.com.clickhouse.protocol.tcp


class OtelTraceInfoFragment : BaseFragment(
    FragmentDefinition(
        "OtelTraceInfo", listOf(
            uuidWord1F,
            uuidWord2F,
            spanId,
            traceStateF,
            traceFlagsF
        ),
        predicate = { it.values[traceIdF.name].let { v -> v as Number }.toInt() > 0 }),
    values = HashMap(
        mapOf(
            traceIdF.name to 0.toByte(),
        )
    )
) {

    companion object {
        val traceIdF = byte("traceId", Versions.MIN_REVISION_WITH_OPENTELEMETRY)
        val uuidWord1F = long("uuidWord1", Versions.MIN_REVISION_WITH_CLIENT_INFO)
        val uuidWord2F = long("uuidWord2", Versions.MIN_REVISION_WITH_CLIENT_INFO)
        val spanId = long("spanId", Versions.MIN_REVISION_WITH_CLIENT_INFO)
        val traceStateF = string("traceState", Versions.MIN_REVISION_WITH_CLIENT_INFO)
        val traceFlagsF = byte("traceFlags", Versions.MIN_REVISION_WITH_CLIENT_INFO)
    }
}