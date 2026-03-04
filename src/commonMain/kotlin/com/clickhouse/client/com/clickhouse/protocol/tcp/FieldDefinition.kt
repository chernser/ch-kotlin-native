package com.clickhouse.client.com.clickhouse.protocol.tcp

data class FieldDefinition(val name: String, val type: FieldType, val since: UInt)

fun string(name: String, since: UInt) = FieldDefinition(name, FieldType.String, since)
fun long(name: String, since: UInt) = FieldDefinition(name, FieldType.Long, since)

fun varInt(name: String, since: UInt) = FieldDefinition(name, FieldType.VarInt, since)


// Special
fun struct(name: String, since: UInt) = FieldDefinition(name, FieldType.Struct, since)
fun structEnd() = FieldDefinition("<END>", FieldType.StructEnd, 0u)
fun collection(name: String, since: UInt) = FieldDefinition(name, FieldType.Collection, since)
fun collectionEnd() = FieldDefinition("<END>", FieldType.CollectionEnd, 0u)

sealed class FieldType {

    object String : FieldType()
    object VarInt : FieldType()
    object Long : FieldType()

    object Collection : FieldType()
    object CollectionEnd : FieldType()
    object Struct : FieldType()
    object StructEnd : FieldType()
}