package com.clickhouse.client.com.clickhouse.protocol.tcp

open class BasePacket(val definition: PacketDefinition, val values : HashMap<String, Any> = HashMap()) {

    fun set(field : FieldDefinition, value : Any)  { values[field.name] = value }
}

fun <T: BasePacket> buildPacket(instance: T, block: T.() -> Unit): T =
    instance.apply(block)