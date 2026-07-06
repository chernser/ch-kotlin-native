package com.clickhouse.client.com.clickhouse.protocol.tcp

object Versions {

    val MIN_REVISION_WITH_CLIENT_INFO                                          = 54032u
    val MIN_REVISION_WITH_SERVER_TIMEZONE                                      = 54058u
    val MIN_REVISION_WITH_QUOTA_KEY_IN_CLIENT_INFO                             = 54060u
    val MIN_REVISION_WITH_TABLES_STATUS                                        = 54226u
    val MIN_REVISION_WITH_TIME_ZONE_PARAMETER_IN_DATETIME_DATA_TYPE            = 54337u
    val MIN_REVISION_WITH_SERVER_DISPLAY_NAME                                  = 54372u
    val MIN_REVISION_WITH_VERSION_PATCH                                        = 54401u
    val MIN_REVISION_WITH_SERVER_LOGS                                          = 54406u
    /// Minimum revision with exactly the same set of aggregation methods and rules to select them.
    /// Two-level (bucketed) aggregation is incompatible if servers are inconsistent in these rules
    /// (keys will be placed in different buckets and result will not be fully aggregated).
    val MIN_REVISION_WITH_CURRENT_AGGREGATION_VARIANT_SELECTION_METHOD        = 54448u
    val MIN_MAJOR_VERSION_WITH_CURRENT_AGGREGATION_VARIANT_SELECTION_METHOD   = 21u
    val MIN_MINOR_VERSION_WITH_CURRENT_AGGREGATION_VARIANT_SELECTION_METHOD   = 4u
    val MIN_REVISION_WITH_COLUMN_DEFAULTS_METADATA                            = 54410u
    val MIN_REVISION_WITH_LOW_CARDINALITY_TYPE                                = 54405u
    val MIN_REVISION_WITH_CLIENT_WRITE_INFO                                   = 54420u
    // Minimum revision supporting SettingsBinaryFormat::STRINGS.
    val MIN_REVISION_WITH_SETTINGS_SERIALIZED_AS_STRINGS                      = 54429u
    val MIN_REVISION_WITH_SCALARS                                             = 54429u
    // Minimum revision supporting OpenTelemetry
    val MIN_REVISION_WITH_OPENTELEMETRY                                       = 54442u
    val MIN_REVISION_WITH_AGGREGATE_FUNCTIONS_VERSIONING                      = 54452u

    val CLUSTER_INITIAL_PROCESSING_PROTOCOL_VERSION                           = 1u
    val CLUSTER_PROCESSING_PROTOCOL_VERSION_WITH_DATA_LAKE_METADATA           = 2u
    val CLUSTER_PROCESSING_PROTOCOL_VERSION_WITH_ICEBERG_METADATA             = 3u
    val CLUSTER_PROCESSING_PROTOCOL_VERSION_WITH_FILE_BUCKETS_INFO            = 4u
    val CLUSTER_PROCESSING_PROTOCOL_VERSION                                   = CLUSTER_PROCESSING_PROTOCOL_VERSION_WITH_FILE_BUCKETS_INFO

    val DATA_LAKE_TABLE_STATE_SNAPSHOT_PROTOCOL_VERSION                       = 1u

    val MIN_SUPPORTED_PARALLEL_REPLICAS_PROTOCOL_VERSION                      = 3u
    val PARALLEL_REPLICAS_MIN_VERSION_WITH_MARK_SEGMENT_SIZE_FIELD            = 4u
    val PARALLEL_REPLICAS_MIN_VERSION_WITH_PROJECTION                         = 5u
    val PARALLEL_REPLICAS_PROTOCOL_VERSION                                    = 5u
    val MIN_REVISION_WITH_PARALLEL_REPLICAS                                   = 54453u
    val MIN_REVISION_WITH_QUERY_AND_LINE_NUMBERS                              = 54475u

    val MERGE_TREE_PART_INFO_VERSION                                          = 1u
    val QUERY_PLAN_SERIALIZATION_VERSION                                      = 0u

    val MIN_REVISION_WITH_INTERSERVER_SECRET                                  = 54441u
    val MIN_REVISION_WITH_X_FORWARDED_FOR_IN_CLIENT_INFO                     = 54443u
    val MIN_REVISION_WITH_REFERER_IN_CLIENT_INFO                              = 54447u
    val MIN_PROTOCOL_VERSION_WITH_DISTRIBUTED_DEPTH                           = 54448u
    val MIN_PROTOCOL_VERSION_WITH_INCREMENTAL_PROFILE_EVENTS                  = 54451u
    val MIN_REVISION_WITH_CUSTOM_SERIALIZATION                                = 54454u
    val MIN_PROTOCOL_VERSION_WITH_INITIAL_QUERY_START_TIME                    = 54449u
    val MIN_PROTOCOL_VERSION_WITH_PROFILE_EVENTS_IN_INSERT                    = 54456u
    val MIN_PROTOCOL_VERSION_WITH_VIEW_IF_PERMITTED                           = 54457u
    val MIN_PROTOCOL_VERSION_WITH_ADDENDUM                                    = 54458u
    val MIN_PROTOCOL_VERSION_WITH_QUOTA_KEY                                   = 54458u
    val MIN_PROTOCOL_VERSION_WITH_PARAMETERS                                  = 54459u
    // The server will send query elapsed run time in the Progress packet.
    val MIN_PROTOCOL_VERSION_WITH_SERVER_QUERY_TIME_IN_PROGRESS               = 54460u
    val MIN_PROTOCOL_VERSION_WITH_PASSWORD_COMPLEXITY_RULES                   = 54461u
    val MIN_REVISION_WITH_INTERSERVER_SECRET_V2                               = 54462u
    val MIN_PROTOCOL_VERSION_WITH_TOTAL_BYTES_IN_PROGRESS                     = 54463u
    val MIN_PROTOCOL_VERSION_WITH_TIMEZONE_UPDATES                            = 54464u
    val MIN_REVISION_WITH_SPARSE_SERIALIZATION                                = 54465u
    val MIN_REVISION_WITH_SSH_AUTHENTICATION                                  = 54466u
    // Send read-only flag for Replicated tables as well
    val MIN_REVISION_WITH_TABLE_READ_ONLY_CHECK                               = 54467u
    val MIN_REVISION_WITH_SYSTEM_KEYWORDS_TABLE                               = 54468u
    val MIN_REVISION_WITH_ROWS_BEFORE_AGGREGATION                             = 54469u
    // Packets size header
    val MIN_PROTOCOL_VERSION_WITH_CHUNKED_PACKETS                             = 54470u
    val MIN_REVISION_WITH_VERSIONED_PARALLEL_REPLICAS_PROTOCOL                = 54471u
    // Push externally granted roles to other nodes
    val MIN_PROTOCOL_VERSION_WITH_INTERSERVER_EXTERNALLY_GRANTED_ROLES        = 54472u
    val MIN_REVISION_WITH_V2_DYNAMIC_AND_JSON_SERIALIZATION                   = 54473u
    val MIN_REVISION_WITH_SERVER_SETTINGS                                     = 54474u
    val MIN_REVISON_WITH_JWT_IN_INTERSERVER                                   = 54476u
    val MIN_REVISION_WITH_QUERY_PLAN_SERIALIZATION                            = 54477u
    val MIN_REVISON_WITH_PARALLEL_BLOCK_MARSHALLING                           = 54478u
    val MIN_REVISION_WITH_VERSIONED_CLUSTER_FUNCTION_PROTOCOL                 = 54479u
    val MIN_REVISION_WITH_OUT_OF_ORDER_BUCKETS_IN_AGGREGATION                 = 54480u
    val MIN_REVISION_WITH_COMPRESSED_LOGS_PROFILE_EVENTS_COLUMNS              = 54481u
    val MIN_REVISION_WITH_REPLICATED_SERIALIZATION                            = 54482u
    val MIN_REVISION_WITH_NULLABLE_SPARSE_SERIALIZATION                       = 54483u

    // Version of ClickHouse TCP protocol.
    // Should be incremented manually on protocol changes.
    val TCP_PROTOCOL_VERSION                                                  = 54483u
}