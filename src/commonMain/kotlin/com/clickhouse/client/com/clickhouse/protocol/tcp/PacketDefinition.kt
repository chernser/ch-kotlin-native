package com.clickhouse.client.com.clickhouse.protocol.tcp

data class PacketDefinition(val id: UInt, val name: String, val fields: List<FieldDefinition>)
