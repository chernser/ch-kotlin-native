import com.clickhouse.client.ClickHouseHttpClient
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*


fun main() {
    println("Starting micro-service")

    val dbClient = ClickHouseHttpClient(io.ktor.client.engine.cio.CIO)
    val log = KtorSimpleLogger("srv")

    embeddedServer(CIO,
        port = 8080) {
        routing {
            get("/") {
                call.respond("Hi! This is me")
                val limit = (call.request.queryParameters.get("limit") ?: "100").toInt()
                log.info("limit: $limit")
            }

            get("/db_info") {
                log.info("getting db info")
                val r = dbClient.query(Queries.DB_INFO)


                if (r.isSuccess) {
                    var response = r.getOrNull()!!

                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "DB Failed")
                }
            }
        }
    }.start(wait = true)
}

object Queries {
    const val DB_INFO = "SELECT timezone() FORMAT TabSeparatedWithNamesAndTypes";
}