package com.clickhouse.client.com.clickhouse.protocol.tcp

data class FieldDefinition(val name: String, val type: FieldType, val since: UInt)

fun string(name: String, since: UInt) = FieldDefinition(name, FieldType.String, since)
fun varInt(name: String, since: UInt) = FieldDefinition(name, FieldType.VarInt, since)

sealed class FieldType {

    object String : FieldType()
    object VarInt : FieldType()
    object Long : FieldType()

}