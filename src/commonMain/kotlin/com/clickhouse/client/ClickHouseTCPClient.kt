package com.clickhouse.client

import com.clickhouse.client.com.clickhouse.protocol.tcp.*
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.logging.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlin.time.Clock

class ClickHouseTCPClient(private val host: String, private val port: Int,
        private val user: String, private val password: String, private val db: String) {

    private val log = KtorSimpleLogger("db_c")
    private val selectorManager: SelectorManager = SelectorManager(Dispatchers.IO)

    private var protoVersion = Versions.TCP_PROTOCOL_VERSION;

    private var activeConnection: Connection? = null

    private var activeConnCodec: PacketChannelCodec? = null

    private val address = "127.0.0.1:8123"

    private val clientName = "clickhouse-ktor-client"

    private fun getQuotaKey() : String {
        return "quota_key1";
    }

    suspend fun connect(): Result<Connection> {

        val connection =
            aSocket(selectorManager).tcp()
                .connect(hostname = host, port = port)
                .connection()

        val codec = PacketChannelCodec(connection.output, connection.input)

        val helloReq = buildPacket(HelloReq()){
            set(HelloReq.clientNameF, clientName)
            set(HelloReq.majorVersionF, 1U)
            set(HelloReq.minorVersionF, 1U)
            set(HelloReq.protoVersionF, protoVersion)
            set(HelloReq.dbF, db)
            set(HelloReq.usernameF, user)
            set(HelloReq.passwordF, password)
        }

        codec.writePacket(helloReq)

        val helloResp = HelloResp()

        codec.readPacket(helloResp)

        // This is addendum
        val answerFields = listOf<FieldDefinition>(
            string("quotaKey", Versions.MIN_PROTOCOL_VERSION_WITH_QUOTA_KEY),
            string("capsSend", Versions.MIN_PROTOCOL_VERSION_WITH_CHUNKED_PACKETS ), // check name
            string("capsRecv", Versions.MIN_PROTOCOL_VERSION_WITH_CHUNKED_PACKETS ), // check name
            varInt("repProtoVersion", Versions.MIN_SUPPORTED_PARALLEL_REPLICAS_PROTOCOL_VERSION),
        )

        val answerValues = mapOf<String, Any?>(
            "quotaKey" to getQuotaKey(),
            "capsSend" to "notchunked",
            "capsRecv" to "notchunked",
            "repProtoVersion" to Versions.MIN_REVISION_WITH_VERSIONED_PARALLEL_REPLICAS_PROTOCOL,
        )

        codec.writeFields(answerFields, answerValues)

        log.info("Handshake completed")
        codec.flush()
        activeConnection = connection
        activeConnCodec = codec
        return Result.success(connection)
    }

    suspend fun ping() : Boolean {
        if (activeConnection != null && activeConnCodec != null) {

            val pingReq = buildPacket(Ping()){}
            activeConnCodec!!.writePacket(pingReq)

            val pong = Pong()
            activeConnCodec!!.readPacket(pong)
            return true
        }

        return false
    }

    private fun localhostIp() : String {
        return "192.168.50.234"
    }

    private fun localHost() : String {
        return "local.local"
    }

    private fun osUser() : String {
        return "localuser"
    }

    private fun getDistributionDepth() : UInt {
        return 1u;
    }

    private fun getClientVersionPatch() : UInt {
        return 1u;
    }

    private fun fillReplicasConfig(fragment: ClientInfoFragment) {
        fragment.set(ClientInfoFragment.collaborateWithInitiatorF, 0u)
        fragment.set(ClientInfoFragment.obsoleteCountPartReplicasF, 0u)
        fragment.set(ClientInfoFragment.numOfCurrentReplicasF, 0u)
    }

    private fun createClientInfo(qId: String, traceInfo: OtelTraceInfoFragment): ClientInfoFragment {
        val clientInfo = buildFragment(ClientInfoFragment(), {
            set(ClientInfoFragment.usernameF, user)
            set(ClientInfoFragment.queryIdF, qId)
            set(ClientInfoFragment.ipAddressF, localhostIp())
            set(ClientInfoFragment.nowTimeF, Clock.System.now().toEpochMilliseconds().toDouble())
            set(ClientInfoFragment.osUserF, osUser())
            set(ClientInfoFragment.hostnameF, localHost())
            set(ClientInfoFragment.clientNameF, clientName)
            set(ClientInfoFragment.protoVersionF, protoVersion)
            set(ClientInfoFragment.quotaKeyF, getQuotaKey())
            set(ClientInfoFragment.distributionDepthF, getDistributionDepth())
            set(ClientInfoFragment.clientVersionPatchF, getClientVersionPatch())
            traceInfo.values[OtelTraceInfoFragment.traceIdF.name]?.let { set(OtelTraceInfoFragment.traceIdF, it) }
            set(ClientInfoFragment.traceInfoF, traceInfo)
            fillReplicasConfig(this)
        })

        return clientInfo
    }

    private fun createOtelTraceInfo() : OtelTraceInfoFragment {
        return buildFragment(OtelTraceInfoFragment(), {})
    }

    suspend fun query(sqlStmt: String, qId: String, params: Map<String, String>, opSettings: OperationSettings) : Result<Boolean> {

        val queryReq = buildPacket(QueryReq(), {
            set(QueryReq.queryIdF, qId)
            set(QueryReq.clientInfoF, createClientInfo(qId, createOtelTraceInfo()))
            if (protoVersion >= Versions.MIN_REVISION_WITH_SETTINGS_SERIALIZED_AS_STRINGS) {
                set(QueryReq.settingsF, "") // with flags
                // todo: write settings as strings
            } else {
                // todo: write settings as binary
                set(QueryReq.settingsF, "")
            }

            set(QueryReq.extraRolesF, "") // todo: fill
            set(QueryReq.interServerSecretF, "") // empty because we not in inter server mode
            set(QueryReq.queryStageF, QueryReq.QueryStage.FetchColumns.v)  // todo: should be only for select ?
            set(QueryReq.compressionFlagF, 0u)
            set(QueryReq.sqlF, sqlStmt)

            if (protoVersion >= Versions.MIN_REVISION_WITH_SETTINGS_SERIALIZED_AS_STRINGS) {
                set(QueryReq.queryParamsF, "") // with flags
            } else {
                set(QueryReq.queryParamsF, "") // without flags
            }
        })


        activeConnCodec!!.writePacket(queryReq)

        // at this point we should return and call deferred method
        return Result.success(true)
    }

    suspend fun disconnect() {
        if (activeConnection != null) {
            log.info("Disconnecting from server ${activeConnection!!.socket}")
            activeConnection?.output?.flushAndClose()
            activeConnection?.input?.cancel()
            activeConnection?.socket?.dispose()
        }
    }

    class OperationSettings(val traceId: UInt = 0u) {
    }

    private object QueryProcessingStage {
        /// Only read/have been read the columns specified in the query.
        val FetchColumns       = 0u
        /// Until the stage where the results of processing on different servers can be combined.
        val WithMergeableState = 1u
        /// Completely.
        val Complete           = 2u
        /// Until the stage where the aggregate functions were calculated and finalized.
        ///
        /// It is used for auto distributed_group_by_no_merge optimization for distributed engine.
        /// (See comments in StorageDistributed).
        val WithMergeableStateAfterAggregation = 3u
        /// Same as WithMergeableStateAfterAggregation but also will apply limit on each shard.
        ///
        /// This query stage will be used for auto
        /// distributed_group_by_no_merge/distributed_push_down_limit
        /// optimization.
        /// (See comments in StorageDistributed).
        val WithMergeableStateAfterAggregationAndLimit = 4u

        val MAX = 5u
    }

    private object SettingsWriteFormat { // uint8
        val BINARY = 0u /// Part of the settings are serialized as strings, and other part as variants. This is the old behaviour.
        val STRINGS_WITH_FLAGS = 1u /// All settings are serialized as strings. Before each value the flag `is_important` is serialized.
        val DEFAULT = STRINGS_WITH_FLAGS
    };

    private object Interface {
        val TCP = 1u
        val HTTP = 2u
        val GRPC = 3u
        val MYSQL = 4u
        val POSTGRESQL = 5u
        val LOCAL = 6u
        val TCP_INTERSERVER = 7u
        val PROMETHEUS = 8u
    }

    private object QueryKind {
        val NO_QUERY = 0u            /// Uninitialized object.
        val INITIAL_QUERY = 1u
        val SECONDARY_QUERY = 2u    /// Query that was initiated by another query for distributed or ON CLUSTER query execution.
    };
}