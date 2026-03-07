package com.clickhouse.client.com.clickhouse.protocol.tcp

class PacketDefinition(val id: UInt, name: String, fields: List<FieldDefinition>) : FragmentDefinition(name, fields)
open class
FragmentDefinition(val name: String, val fields : List<FieldDefinition>)