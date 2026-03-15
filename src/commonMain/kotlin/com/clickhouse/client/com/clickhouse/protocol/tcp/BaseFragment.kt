package com.clickhouse.client.com.clickhouse.protocol.tcp

open class BaseFragment(val definition: FragmentDefinition, val values : HashMap<String, Any> = HashMap()) {

    fun set(field : FieldDefinition, value : Any)  { values[field.name] = value }
}

fun <T: BaseFragment> buildFragment(instance: T, block: T.() -> Unit): T =
    instance.apply(block)

open class FragmentDefinition(val name: String, val fields : List<FieldDefinition>,
                              val predicate: (BaseFragment) -> Boolean = { true }) {}