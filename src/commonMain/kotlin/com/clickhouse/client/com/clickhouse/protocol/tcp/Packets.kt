package com.clickhouse.client.com.clickhouse.protocol.tcp


object Packets {

    // Server packets
    object Server {
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

        val MAX = SSHChallenge
    }

    // Client packets
    object Client {
        val Hello                   = 0u   // Name, version, revision, default DB
        val Query                   = 1u   // Query id, query settings, stage up to which the query must be executed,
        // whether the compression must be used,
        // query text (without data for INSERTs).
        val Data                    = 2u   // A block of data (compressed or not).
        val Cancel                  = 3u   // Cancel the query execution.
        val Ping                    = 4u   // Check that connection to the server is alive.
        val TablesStatusRequest     = 5u   // Check status of tables on the server.
        val KeepAlive               = 6u   // Keep the connection alive.
        val Scalar                  = 7u   // A block of data (compressed or not).
        val IgnoredPartUUIDs        = 8u   // List of unique parts ids to exclude from query processing.
        val ReadTaskResponse        = 9u   // A filename to read from s3 (used in s3Cluster).
        val MergeTreeReadTaskResponse = 10u // Coordinator's decision with a modified set of mark ranges allowed to read.
        val SSHChallengeRequest     = 11u  // Request SSH signature challenge.
        val SSHChallengeResponse    = 12u  // Reply to SSH signature challenge.
        val QueryPlan               = 13u  // Query plan.
        val MAX = QueryPlan
    }
}