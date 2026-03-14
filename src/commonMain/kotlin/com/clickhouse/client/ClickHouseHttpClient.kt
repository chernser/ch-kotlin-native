package com.clickhouse.client

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.logging.*
import io.ktor.utils.io.*
import kotlinx.serialization.json.*


class ClickHouseHttpClient<THttpEngine : HttpClientEngineConfig>
    (httpEngine: HttpClientEngineFactory<THttpEngine>) {

    val log = KtorSimpleLogger("db_c")
    val baseUrl = "http://localhost:8123"
    val formatMapping = HashMap<String, (source: ByteReadChannel, format: String) -> ResponseData>()

    init {

        val tsvFactory = { source: ByteReadChannel, format: String -> TabSeparatedData(source, format) }
        formatMapping.put("TabSeparatedWithNamesAndTypes", tsvFactory)
        formatMapping.put("TabSeparatedWithNames", tsvFactory)
        formatMapping.put("TabSeparated", tsvFactory)

    }

    val httpClient: HttpClient = HttpClient(httpEngine) {
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    log.info(message)
                }
            }
            level = LogLevel.ALL
//            filter { request ->
//                request.url.host.contains("ktor.io")
//            }
            sanitizeHeader { header -> header == HttpHeaders.Authorization }
        }
    }

    private suspend fun sendRequest(sql: String): Result<ServerResponse> {
        try {


            val response: HttpResponse = httpClient.post(baseUrl) {
                headers {
                    append("X-ClickHouse-Username", "default")
                    append("X-ClickHouse-Database", "default")
                }
                contentType(ContentType.Text.Plain)
                setBody(sql)
            }



            log.info("Server response ${response.status}")
            var srvErr: String = ""
            if (response.status != HttpStatusCode.OK) {
                srvErr = response.body() ?: "error"
            }

            val headers = response.headers
            val format = headers["X-ClickHouse-Format"] ?: ""
            val srvResp = ServerResponse(
                srvErr,
                headers["X-ClickHouse-Summary"] ?: "",
                format,
                headers["X-ClickHouse-Query-Id"] ?: "",
                headers["X-ClickHouse-Timezone"] ?: "",
                formatMapping.get(format)!!.invoke(response.bodyAsChannel(), format)
            )
            log.info("srvResp: $srvResp")


            return Result.success(srvResp)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    suspend fun query(sql: String): Result<ServerResponse> {
        return sendRequest(sql)
    }

    data class ServerResponse(
        val error: String?,
        val summary: String,
        val format: String = "",
        val queryId: String = "",
        val serverTz: String = "",
        val data: ResponseData
    ) {

        val parsedSummary: JsonObject

        init {
            parsedSummary = Json.parseToJsonElement(summary).jsonObject
        }

        fun getReadRows(): UInt {
            return parsedSummary["read_rows"]!!.jsonPrimitive.int.toUInt()
        }

        fun getReadBytes(): UInt {
            return parsedSummary["read_bytes"]!!.jsonPrimitive.int.toUInt()
        }

        fun getWrittenRows(): UInt {
            return parsedSummary["written_rows"]!!.jsonPrimitive.int.toUInt()
        }

        fun getWrittenBytes(): UInt {
            return parsedSummary["written_bytes"]!!.jsonPrimitive.int.toUInt()
        }

        fun getResultRows(): UInt {
            return parsedSummary["result_rows"]!!.jsonPrimitive.int.toUInt()
        }

        fun getResultBytes(): ULong {
            return parsedSummary["result_bytes"]!!.jsonPrimitive.long.toULong()
        }

        fun getServerExecTimeNanos(): Double {
            return parsedSummary["elapsed_ns"]!!.jsonPrimitive.double
        }

        fun getServerExecTime(): Double {
            return getServerExecTimeNanos() / 1_000_000.0
        }


        override fun equals(other: Any?): Boolean {
            if (this === other) return true
//            if (javaClass != other?.javaClass) return false

            other as ServerResponse

            if (error != other.error) return false
            if (summary != other.summary) return false
            if (format != other.format) return false
            if (queryId != other.queryId) return false
            if (serverTz != other.serverTz) return false
            if (parsedSummary != other.parsedSummary) return false

            return true
        }

        override fun hashCode(): Int {
            var result = error?.hashCode() ?: 0
            result = 31 * result + summary.hashCode()
            result = 31 * result + format.hashCode()
            result = 31 * result + queryId.hashCode()
            result = 31 * result + serverTz.hashCode()
            result = 31 * result + parsedSummary.hashCode()
            return result
        }
    }
}