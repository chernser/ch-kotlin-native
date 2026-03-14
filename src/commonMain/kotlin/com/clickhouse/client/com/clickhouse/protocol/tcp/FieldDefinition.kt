package com.clickhouse.client.com.clickhouse.protocol.tcp

data class FieldDefinition(val name: String, val type: FieldType, val since: UInt)

fun string(name: String, since: UInt) = FieldDefinition(name, FieldType.String, since)

fun byte(name: String, since: UInt) = FieldDefinition(name, FieldType.Byte, since)

fun short(name: String, since: UInt) = FieldDefinition(name, FieldType.Short, since)

fun int(name: String, since: UInt) = FieldDefinition(name, FieldType.Int, since)

fun long(name: String, since: UInt) = FieldDefinition(name, FieldType.Long, since)

fun float(name: String, since: UInt) = FieldDefinition(name, FieldType.Float, since)

fun double(name: String, since: UInt) = FieldDefinition(name, FieldType.Double, since)

fun varInt(name: String, since: UInt) = FieldDefinition(name, FieldType.VarInt, since)

fun varLong(name: String, since: UInt) = FieldDefinition(name, FieldType.VarLong, since)


// Special
fun struct(name: String, since: UInt) = FieldDefinition(name, FieldType.Struct, since)
fun structEnd() = FieldDefinition("<END>", FieldType.StructEnd, 0u)
fun collection(name: String, since: UInt) = FieldDefinition(name, FieldType.Collection, since)
fun collectionEnd() = FieldDefinition("<END>", FieldType.CollectionEnd, 0u)

sealed class FieldType {

    object String : FieldType()
    object VarInt : FieldType()

    object VarLong : FieldType()

    object Byte : FieldType()

    object Short : FieldType()

    object Int : FieldType()

    object Long : FieldType()

    object Float : FieldType()

    object Double : FieldType()

    object Collection : FieldType()
    object CollectionEnd : FieldType()
    object Struct : FieldType()
    object StructEnd : FieldType()
}