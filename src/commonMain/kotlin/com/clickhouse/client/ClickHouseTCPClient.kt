package com.clickhouse.client

import com.clickhouse.client.ClickHouseTCPClient.ClientPacketTypes.HELLO
import com.clickhouse.client.ClickHouseTCPClient.ClientPacketTypes.PING
import com.clickhouse.client.ClickHouseTCPClient.ProtoVersions.DBMS_MIN_PROTOCOL_VERSION_WITH_ADDENDUM
import com.clickhouse.client.ClickHouseTCPClient.ProtoVersions.DBMS_MIN_PROTOCOL_VERSION_WITH_CHUNKED_PACKETS
import com.clickhouse.client.ClickHouseTCPClient.ProtoVersions.DBMS_MIN_PROTOCOL_VERSION_WITH_DISTRIBUTED_DEPTH
import com.clickhouse.client.ClickHouseTCPClient.ProtoVersions.DBMS_MIN_PROTOCOL_VERSION_WITH_INITIAL_QUERY_START_TIME
import com.clickhouse.client.ClickHouseTCPClient.ProtoVersions.DBMS_MIN_PROTOCOL_VERSION_WITH_PARAMETERS
import com.clickhouse.client.ClickHouseTCPClient.ProtoVersions.DBMS_MIN_PROTOCOL_VERSION_WITH_PASSWORD_COMPLEXITY_RULES
import com.clickhouse.client.ClickHouseTCPClient.ProtoVersions.DBMS_MIN_PROTOCOL_VERSION_WITH_QUOTA_KEY
import com.clickhouse.client.ClickHouseTCPClient.ProtoVersions.DBMS_MIN_REVISION_WITH_CLIENT_INFO
import com.clickhouse.client.ClickHouseTCPClient.ProtoVersions.DBMS_MIN_REVISION_WITH_INTERSERVER_SECRET_V2
import com.clickhouse.client.ClickHouseTCPClient.ProtoVersions.DBMS_MIN_REVISION_WITH_OPENTELEMETRY
import com.clickhouse.client.ClickHouseTCPClient.ProtoVersions.DBMS_MIN_REVISION_WITH_PARALLEL_REPLICAS
import com.clickhouse.client.ClickHouseTCPClient.ProtoVersions.DBMS_MIN_REVISION_WITH_QUOTA_KEY_IN_CLIENT_INFO
import com.clickhouse.client.ClickHouseTCPClient.ProtoVersions.DBMS_MIN_REVISION_WITH_SERVER_DISPLAY_NAME
import com.clickhouse.client.ClickHouseTCPClient.ProtoVersions.DBMS_MIN_REVISION_WITH_SERVER_TIMEZONE
import com.clickhouse.client.ClickHouseTCPClient.ProtoVersions.DBMS_MIN_REVISION_WITH_SETTINGS_SERIALIZED_AS_STRINGS
import com.clickhouse.client.ClickHouseTCPClient.ProtoVersions.DBMS_MIN_REVISION_WITH_VERSIONED_PARALLEL_REPLICAS_PROTOCOL
import com.clickhouse.client.ClickHouseTCPClient.ProtoVersions.DBMS_MIN_REVISION_WITH_VERSION_PATCH
import com.clickhouse.client.ClickHouseTCPClient.ProtoVersions.DBMS_TCP_PROTOCOL_VERSION
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.date.*
import io.ktor.util.logging.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlin.experimental.and
import kotlin.math.min

class ClickHouseTCPClient(private val host: String, private val port: Int,
        private val user: String, private val password: String, private val db: String) {

    private val log = KtorSimpleLogger("db_c")
    private val selectorManager: SelectorManager = SelectorManager(Dispatchers.IO)

    private var protoVersion = DBMS_TCP_PROTOCOL_VERSION;

    private var activeConnection: Connection? = null

    private val address = "127.0.0.1:8123"

    private val clientName = "clickhouse-ktor-client"

    suspend fun connect(): Result<Connection> {

        val connection =
            aSocket(selectorManager).tcp()
                .connect(hostname = host, port = port)
                .connection()

        with(connection.output) {
            writeVarUInt(this, HELLO.toULong()) // hello packet ID
            writeBinaryString(this,clientName)
            writeVarUInt(this, 1u)
            writeVarUInt(this, 0u)
            writeVarUInt(this, DBMS_TCP_PROTOCOL_VERSION.toULong())
            writeBinaryString(this, db)
            writeBinaryString(this, user)
            writeBinaryString(this, password)
            this.flush();
        }



        with(connection.input) {
            val packetType = readVarUInt(this)
            if (packetType != 0u) {
                throw RuntimeException("Invalid response from server: packetType = ${packetType}")
            }
            val versionName = readBinaryString(this)
            val versionMajor = readVarUInt(this);
            val versionMinor = readVarUInt(this);
            val versionProto = readVarUInt(this);
            log.info("versionName: $versionName, version: $versionMajor.$versionMinor, proto: $versionProto")
            // if version > DBMS_MIN_REVISION_WITH_VERSIONED_PARALLEL_REPLICAS_PROTOCOL = 54471
            protoVersion = min(protoVersion, versionProto) // correct version
            if (protoVersion >= DBMS_MIN_REVISION_WITH_VERSIONED_PARALLEL_REPLICAS_PROTOCOL) {
                val versionProtoRep = readVarUInt(this);
                log.info("replica proto version: $versionProtoRep")
            }
            if (protoVersion >= DBMS_MIN_REVISION_WITH_SERVER_TIMEZONE) {
                val timezome = readBinaryString(this)
                log.info("server timezone: $timezome")
            }
            if (protoVersion >= DBMS_MIN_REVISION_WITH_SERVER_DISPLAY_NAME) {
                val serverDisplayName = readBinaryString(this)
                log.info("server display name: $serverDisplayName")
            }
            if (protoVersion >= DBMS_MIN_REVISION_WITH_VERSION_PATCH) {
                val versionPatch = readVarUInt(this)
                log.info("version patch: $versionPatch")
            }

            if (protoVersion >= DBMS_MIN_PROTOCOL_VERSION_WITH_CHUNKED_PACKETS) {
                val protoCapsSend = readBinaryString(this)
                val protoCapsRecv = readBinaryString(this)
                log.info("protoCaps.send: $protoCapsSend")
                log.info("protoCaps.recv: $protoCapsRecv")
            }
            if (protoVersion >= DBMS_MIN_PROTOCOL_VERSION_WITH_PASSWORD_COMPLEXITY_RULES) {
                val count = readVarUInt(this).toInt()
                log.info("password rules: $count")
                for (i in 0 until count) {
                    log.info("reading rules")
                    val pattern = readBinaryString(this)
                    log.info("pattern: $pattern")
                    val exceptionMsg = readBinaryString(this)
                    log.info("exceptionMsg")
                    log.info("rule: $i -> pattern: $pattern, msg: $exceptionMsg")
                }
            }
            if (protoVersion >= DBMS_MIN_REVISION_WITH_INTERSERVER_SECRET_V2) {
                // UInt64
                val nonce = this.readLongLittleEndian().toUInt()
                log.info("nonce: $nonce")
            }
        }

        val quotaKey = "qk1"
        with(connection.output) {
            if (protoVersion >= DBMS_MIN_PROTOCOL_VERSION_WITH_ADDENDUM) {
                if (protoVersion >= DBMS_MIN_PROTOCOL_VERSION_WITH_QUOTA_KEY) {
                    writeBinaryString(this, quotaKey);
                }
            }

            if (protoVersion >= DBMS_MIN_PROTOCOL_VERSION_WITH_CHUNKED_PACKETS) {
                writeBinaryString(this, "notchunked")
                writeBinaryString(this, "notchunked")
            }

            if (protoVersion >= DBMS_MIN_REVISION_WITH_VERSIONED_PARALLEL_REPLICAS_PROTOCOL) {
                writeVarUInt(this, DBMS_MIN_REVISION_WITH_VERSIONED_PARALLEL_REPLICAS_PROTOCOL.toULong())
            }
        }

        activeConnection = connection
        return Result.success(connection)
    }

    suspend fun ping() : Boolean {
        if (activeConnection != null ) {
            writeVarUInt(activeConnection!!.output, PING.toULong())
            activeConnection!!.output.flush()
            val packetType = readVarUInt(activeConnection!!.input)
            if (packetType == ServerPacketTypes.Pong) {
                return true
            }
        }

        return false
    }


    suspend fun query(q: String, qId: String, params: Map<String, String>, opSettings: OperationSettings) : Result<Boolean> {

        with (activeConnection!!.output) {
            writeVarUInt(this, ClientPacketTypes.QUERY.toULong());
            writeBinaryString(this, qId)
            writeClientInfo(this, QueryKind.INITIAL_QUERY.toByte(), qId, opSettings)

            // write settings
            if (protoVersion >= DBMS_MIN_REVISION_WITH_SETTINGS_SERIALIZED_AS_STRINGS) {
                this.writeByte(SettingsWriteFormat.STRINGS_WITH_FLAGS.toByte())
            } else {
                this.writeByte(SettingsWriteFormat.BINARY.toByte())
            }
            writeBinaryString(this, "") // end of settings
            // TODO: implement query settings

            writeBinaryString(this, "") // interserver secret

            writeVarUInt(this, QueryProcessingStage.FetchColumns.toULong()) // stage
            writeVarUInt(this, 0uL) // compression (0 - disabled, 1 - enabled)

            writeBinaryString(this, q) // query itself


            // write params
            if (protoVersion >= DBMS_MIN_PROTOCOL_VERSION_WITH_PARAMETERS) {
                if (protoVersion >= DBMS_MIN_REVISION_WITH_SETTINGS_SERIALIZED_AS_STRINGS) {
                    this.writeByte(SettingsWriteFormat.STRINGS_WITH_FLAGS.toByte())
                } else {
                    this.writeByte(SettingsWriteFormat.BINARY.toByte())
                }
                writeBinaryString(this, "") // end of settings
            }
        }

        return Result.success(true)
    }

    suspend fun disconnect() {
        if (activeConnection != null) {
            activeConnection?.output?.close()
            activeConnection?.input?.cancel()
            activeConnection?.socket?.close();
        }
    }

    private suspend fun writeTraceInfo(out: ByteWriteChannel, opSettings: OperationSettings) {
        if (protoVersion >= DBMS_MIN_REVISION_WITH_OPENTELEMETRY)
        {
            out.writeByte(opSettings.traceId.toByte())
            if (opSettings.traceId > 0u) {
                /* struct TracingContext {
                        UUID trace_id;
                        UInt64 span_id = 0;
                        // The incoming tracestate header and the trace flags, we just pass them
                        // downstream. See https://www.w3.org/TR/trace-context/
                        String tracestate;
                        UInt8 trace_flags = TRACE_FLAG_NONE;
                   }
                 */
                out.writeLong(0L, ByteOrder.LITTLE_ENDIAN) // uuid[0]
                out.writeLong(0L, ByteOrder.LITTLE_ENDIAN) // uuid[1]

                out.writeLong(0L, ByteOrder.LITTLE_ENDIAN) // span ID
                val traceState = ""
                writeBinaryString(out, traceState)
                val traceFlags = 0u.toByte()
                out.writeByte(traceFlags)
            }
        }

    }

    private suspend fun writeClientInfo(out: ByteWriteChannel, queryType: Byte, queryId: String,
                                        opSettings: OperationSettings) {
        if (protoVersion < DBMS_MIN_REVISION_WITH_CLIENT_INFO) {
            return
        }

        out.writeByte(queryType)
        writeBinaryString(out, user)
        writeBinaryString(out, queryId)
        writeBinaryString(out, address)

        val now = GMTDate().timestamp * 1000.0
        if (protoVersion >= DBMS_MIN_PROTOCOL_VERSION_WITH_INITIAL_QUERY_START_TIME) {
            out.writeDouble(now, ByteOrder.LITTLE_ENDIAN)
        }

        out.writeByte(Interface.TCP.toByte())

        writeBinaryString(out, "root") // os user
        writeBinaryString(out, "localhost") // local host name
        writeBinaryString(out, clientName) // local host name
        writeVarUInt(out, 1u)
        writeVarUInt(out, 0u)
        writeVarUInt(out, DBMS_TCP_PROTOCOL_VERSION.toULong())

        val quotaKey = "key1"
        if (protoVersion >= DBMS_MIN_REVISION_WITH_QUOTA_KEY_IN_CLIENT_INFO) {
            writeBinaryString(out, quotaKey)
        }

        if (protoVersion >= DBMS_MIN_PROTOCOL_VERSION_WITH_DISTRIBUTED_DEPTH) {
            writeVarUInt(out, 1u) // distributed depth
        }

        if (protoVersion >= DBMS_MIN_REVISION_WITH_VERSION_PATCH) {
            writeVarUInt(out, 1u) // client_version_patch
        }

        writeTraceInfo(out, opSettings)

        if (protoVersion >= DBMS_MIN_REVISION_WITH_PARALLEL_REPLICAS)
        {
            writeVarUInt(out, 0u.toULong()) // collaborate_with_initiator
            writeVarUInt(out, 0u.toULong()) // obsolete_count_participating_replicas
            writeVarUInt(out, 0u.toULong()) // number_of_current_replica
        }
    }

    private suspend fun writeBinaryString(out: ByteWriteChannel, str: String): UInt {
        val size = str.length.toUInt()
        val bCount = writeVarUInt(out, size.toULong());

        out.writeStringUtf8(str)
        return bCount + size
    }

    private suspend fun readBinaryString(input: ByteReadChannel): String {
        val size = readVarUInt(input)
        val dst = ByteArray(size.toInt())
        input.readFully(dst)
        return dst.decodeToString()
    }

    private suspend fun writeVarUInt(out: ByteWriteChannel, value: ULong): UInt {
        var v = value
        var i = 0u
        while (v > 0x7Fu) {
            val x = (v and 0x7Fu).toUInt()
            val b: UByte =( 0x80u or x).toUByte()
            out.writeByte(b.toByte())
            v = v shr 7
            i++
        }

        out.writeByte(v.toByte())
        i++
        return i
    }

    private suspend fun readVarUInt(read: ByteReadChannel): UInt {
        var r = 0u
        var bitShift = 0;

        for ( i in 0..10) {

            val b = read.readByte()
            val valuePart = (b and 0x7f).toUInt()
            r = r or (valuePart shl bitShift)
            if (b.toInt() and 0x80 == 0) {
                return r
            }
            bitShift += 7

        }
        return r
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

    private object ClientPacketTypes {
        val HELLO = 0u                      /// Name, version, revision, default DB
        val QUERY = 1u                      /// Query id, query settings, stage up to which the query must be executed,
        /// whether the compression must be used,
        /// query text (without data for INSERTs).
        val DATA = 2u                       /// A block of data (compressed or not).
        val CANCEL = 3u                     /// Cancel the query execution.
        val PING = 4u                       /// Check that connection to the server is alive.
        val TABLES_STATUS_REQ = 5u        /// Check status of tables on the server.
        val KEEP_ALIVE = 6u                  /// Keep the connection alive
        val SCALAR = 7u                     /// A block of data (compressed or not).
        val IGNORED_PART_UUIDS = 8u           /// List of unique parts ids to exclude from query processing
        val READ_TASK_RESPONSE = 9u           /// A filename to read from s3 (used in s3Cluster)
        val MERGE_TREE_READ_TASK_RESPONSE = 10u /// Coordinator's decision with a modified set of mark ranges allowed to read

        val SSH_CHALLENGE_REQ = 11u       /// Request SSH signature challenge
        val SSH_CHALLENGE_RESP = 12u      /// Reply to SSH signature challenge
    }

    private object ServerPacketTypes {
        val Hello = 0u                      /// Name, version, revision.
        val Data = 1u                       /// A block of data (compressed or not).
        val Exception = 2u                  /// The exception during query execution.
        val Progress = 3u                   /// Query execution progress: rows read, bytes read.
        val Pong = 4u                       /// Ping response
        val EndOfStream = 5u                /// All packets were transmitted
        val ProfileInfo = 6u                /// Packet with profiling info.
        val Totals = 7u                     /// A block with totals (compressed or not).
        val Extremes = 8u                   /// A block with minimums and maximums (compressed or not).
        val TablesStatusResponse = 9u       /// A response to TablesStatus request.
        val Log = 10u                       /// System logs of the query execution
        val TableColumns = 11u              /// Columns' description for default values calculation
        val PartUUIDs = 12u                 /// List of unique parts ids.
        val ReadTaskRequest = 13u           /// String (UUID) describes a request for which next task is needed
        /// This is such an inverted logic, where server sends requests
        /// And client returns back response
        val ProfileEvents = 14u             /// Packet with profile events from server.
        val MergeTreeAllRangesAnnouncement = 15u
        val MergeTreeReadTaskRequest = 16u  /// Request from a MergeTree replica to a coordinator
        val TimezoneUpdate = 17u            /// Receive server's (session-wide) default timezone
        val SSHChallenge = 18u              /// Return challenge for SSH signature signing
    }

    private object ProtoVersions {
        val DBMS_TCP_PROTOCOL_VERSION = 54471u
        val DBMS_MIN_REVISION_WITH_VERSIONED_PARALLEL_REPLICAS_PROTOCOL = 54471u
        val DBMS_MIN_REVISION_WITH_SERVER_TIMEZONE = 54058u
        val DBMS_MIN_REVISION_WITH_SERVER_DISPLAY_NAME = 54372u
        val DBMS_MIN_REVISION_WITH_VERSION_PATCH = 54401u
        val DBMS_MIN_PROTOCOL_VERSION_WITH_CHUNKED_PACKETS = 54470u
        val DBMS_MIN_PROTOCOL_VERSION_WITH_PASSWORD_COMPLEXITY_RULES = 54461u
        val DBMS_MIN_REVISION_WITH_INTERSERVER_SECRET_V2 = 54462u
        val DBMS_MIN_PROTOCOL_VERSION_WITH_ADDENDUM = 54458u
        val DBMS_MIN_PROTOCOL_VERSION_WITH_QUOTA_KEY = 54458u
        val DBMS_MIN_REVISION_WITH_CLIENT_INFO = 54032u
        val DBMS_MIN_PROTOCOL_VERSION_WITH_INITIAL_QUERY_START_TIME = 54449u
        val DBMS_MIN_REVISION_WITH_OPENTELEMETRY = 54442u
        val DBMS_MIN_REVISION_WITH_PARALLEL_REPLICAS = 54453u
        val DBMS_MIN_REVISION_WITH_QUOTA_KEY_IN_CLIENT_INFO = 54060u
        val DBMS_MIN_PROTOCOL_VERSION_WITH_DISTRIBUTED_DEPTH = 54448u
        val DBMS_MIN_REVISION_WITH_SETTINGS_SERIALIZED_AS_STRINGS = 54429u
        val DBMS_MIN_REVISION_WITH_INTERSERVER_SECRET = 54441u
        val DBMS_MIN_PROTOCOL_VERSION_WITH_PARAMETERS = 54459u
    }
}