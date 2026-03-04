package com.clickhouse.client.com.clickhouse.protocol.tcp


object Packets {
    object Ids {
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
}