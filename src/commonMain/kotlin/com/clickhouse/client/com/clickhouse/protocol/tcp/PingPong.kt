package com.clickhouse.client.com.clickhouse.protocol.tcp

class Ping : BasePacket(PacketDefinition(Packets.Client.Ping, "ping", listOf()))
class Pong : BasePacket(PacketDefinition(Packets.Server.Pong, "pong", listOf()))