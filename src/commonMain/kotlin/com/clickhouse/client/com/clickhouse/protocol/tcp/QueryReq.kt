package com.clickhouse.client.com.clickhouse.protocol.tcp

class QueryReq : BasePacket(PacketDefinition(
    Packets.Client.Query, "QueryReq", listOf(
        queryIdF,
        // query Info
    )

)) {

    companion object {
        val queryIdF = string("queryId", 0u)

    }
}