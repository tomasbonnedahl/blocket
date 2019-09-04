import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.html.respondHtml
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondFile
import io.ktor.routing.routing
import io.ktor.routing.get
import kotlinx.html.body
import kotlinx.html.head
import kotlinx.html.p
import kotlinx.html.title
import org.jetbrains.exposed.sql.selectAll
import java.io.File

// Entry Point of the application as defined in resources/application.conf.
// @see https://ktor.io/servers/configuration.html#hocon-file
fun Application.main() {
    // This adds Date and Server headers to each response, and allows custom additional headers
    install(DefaultHeaders)
    // This uses use the logger to log every call (request/response)
    install(CallLogging)

    // Registers routes
    routing {
        // Here we use a DSL for building HTML on the route "/"
        // @see https://github.com/Kotlin/kotlinx.html
        get("/") {
            call.respondFile(File("./index.html"))
        }

        get("/fetch-new-data") {
            // TODO: Fetch new data
            // TODO: Loop through the new data and see if any good cars (separate?)
//            val runner2 = Runner2(skodaConfiguration())
//            runner2.run()
            call.respond(HttpStatusCode.OK)
        }

        get("/json-data") {
            // TODO: Called from JS, return data from db file

            /*
            Car.slice(Car.milage, Car.price).selectAll().orderBy(Car.milage to SortOrder.ASC)
             */
        }
    }
}