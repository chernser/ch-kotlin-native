package com.clickhouse.client.com.clickhouse.protocol.tcp

class HelloReq  : BasePacket(

    definition = PacketDefinition(Packets.Client.Hello, "hello_req", listOf(
        clientNameF,
        majorVersionF,
        minorVersionF,
        protoVersionF,
        dbF,
        usernameF,
        passwordF,
    ))) {

    companion object {
        val clientNameF = string("clientName", 0u)
        val majorVersionF = varInt("majorVersion", 0u)
        val minorVersionF = varInt(name = "minorVersion", 0u)

        val protoVersionF = varInt("protoVersion", 0u)

        val dbF = string("database", 0u)

        val usernameF = string("username", 0u)

        val passwordF = string("password", 0u)

    }

}