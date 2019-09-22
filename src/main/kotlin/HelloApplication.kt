import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondFile
import io.ktor.response.respondRedirect
import io.ktor.routing.get
import io.ktor.routing.routing
import java.io.File
import java.text.DateFormat

// Entry Point of the application as defined in resources/application.conf.
// @see https://ktor.io/servers/configuration.html#hocon-file
fun Application.main() {
    org.apache.log4j.BasicConfigurator.configure()

    // This adds Date and Server headers to each response, and allows custom additional headers
    install(DefaultHeaders)
    // This uses use the logger to log every call (request/response)
    install(CallLogging)
    install(CORS)

    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)  // TODO: Change to Simple?
            setPrettyPrinting()
        }
    }

    // Registers routes
    routing {
        // Here we use a DSL for building HTML on the route "/"
        // @see https://github.com/Kotlin/kotlinx.html
        get("/") {
            call.respondRedirect("/Skoda")
//            call.respondFile(File("./index.html"))
        }

        get("/json-data/{brand?}") {
            val brand = call.parameters["brand"]
            try {
                val cars = JsonGetter(DirtyFactory.newDb()).carDatas(brand)
                println("cars = ${cars}")
                call.respond(cars)
            } catch (e: java.lang.Exception) {
                println("Error in /json-data: ${e}")
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/{brand}") {
            val brand = call.parameters["brand"]!!
            println("Got to /brand: {$brand}")
            call.respondFile(File("./index.html"))
        }

        get("/fetch-new-data") {
            // TODO: Loop through the new data and see if any good cars (separate/pub-sub?)
            try {
                val fetchAndWrite = FetchAndWrite(
                    DirtyFactory.newFetcher(),
                    DirtyFactory.newWriter(),
                    skodaConfiguration2()
                )
                fetchAndWrite.run()
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/remove-all-entries") {
            DirtyFactory.newDb().removeAll()
        }

        get("/list-all-cars") {
            val db = DirtyFactory.newDb()
            val carsByDateAdded = db.getCars().groupBy { car ->
                car.date_added
            }.toSortedMap(reverseOrder())
            call.respond(carsByDateAdded)
        }
    }
}
