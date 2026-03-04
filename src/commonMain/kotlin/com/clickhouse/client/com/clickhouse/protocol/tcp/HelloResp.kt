package com.clickhouse.client.com.clickhouse.protocol.tcp

class HelloResp : BasePacket(
    definition = PacketDefinition(Packets.Server.Hello, "hello_resp", listOf(
        versionNameF,
        majorVersionF,
        minorVersionF,
        protoVersionF,
        repProtoVersionF,
        timezoneF,
        serverDisplayNameF,
        serverPatchVersionF,
        protoCapsSendF,
        protoCapsRecvF,
        passwordRulesF,
        passwordRuleF,
        passwordRulePatternF,
        passwordRuleExceptionF,
        structEnd(),
        collectionEnd(),
        interServerSecretF,

    ))
) {

    companion object {
        val versionNameF = string("versionName", 0u)
        val majorVersionF = varInt("majorVersion", 0u)
        val minorVersionF = varInt("minorVersion", 0u)
        val protoVersionF = varInt("protoVersion", 0u)
        val repProtoVersionF =
            varInt("repProtoVersion", Versions.MIN_REVISION_WITH_VERSIONED_PARALLEL_REPLICAS_PROTOCOL)
        val timezoneF = string("timezone", Versions.MIN_REVISION_WITH_SERVER_TIMEZONE)
        val serverDisplayNameF = string("serverDisplayName", Versions.MIN_REVISION_WITH_SERVER_DISPLAY_NAME)
        val serverPatchVersionF = varInt("serverPatchVersion", Versions.MIN_REVISION_WITH_VERSION_PATCH)
        val protoCapsSendF = varInt("capsSend", Versions.MIN_PROTOCOL_VERSION_WITH_CHUNKED_PACKETS)
        val protoCapsRecvF = varInt("capsRecv", Versions.MIN_PROTOCOL_VERSION_WITH_CHUNKED_PACKETS)
        val passwordRulesF = collection("passwordRules", Versions.MIN_PROTOCOL_VERSION_WITH_PASSWORD_COMPLEXITY_RULES)
        val passwordRuleF = struct("passwordRule", Versions.MIN_PROTOCOL_VERSION_WITH_PASSWORD_COMPLEXITY_RULES)
        val passwordRulePatternF =
            string("passwordRulePattern", Versions.MIN_PROTOCOL_VERSION_WITH_PASSWORD_COMPLEXITY_RULES)
        val passwordRuleExceptionF =
            string("passwordRuleException", Versions.MIN_PROTOCOL_VERSION_WITH_PASSWORD_COMPLEXITY_RULES)
        val interServerSecretF = long("interServerSecret", Versions.MIN_REVISION_WITH_INTERSERVER_SECRET_V2)
    }
}