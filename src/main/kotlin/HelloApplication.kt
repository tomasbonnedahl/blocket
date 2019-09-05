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
import io.ktor.routing.routing
import io.ktor.routing.get
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.text.DateFormat
import java.time.LocalDate

data class Model(
    val name: String,
    val items: List<Item>,
    val date: LocalDate = LocalDate.of(2018, 4, 13)
)

data class Item(
    val key: String,
    val value: String
)

val model = Model(
    "root",
    listOf(Item("A", "Apache"), Item("B", "Bing"))
)

data class CarData(
    val milage: Int,
    val price: Int
)

data class CarDatas(
    val data: List<CarData>
)

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
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }

    // Registers routes
    routing {
        // Here we use a DSL for building HTML on the route "/"
        // @see https://github.com/Kotlin/kotlinx.html
        get("/") {
            call.respondFile(File("./index.html"))
        }

        get("/fetch-new-data") {
            // TODO: Loop through the new data and see if any good cars (separate?)
            try {
                val fetcher = DataFetcher()  // Static or
                fetcher.fetch(skodaConfiguration2())
                call.respond(HttpStatusCode.OK)
            }
            catch (e: Exception) {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/json-data") {
            // TODO: Abtract some of this code
            val db = Db()
            val cars = db.getCars()
            call.respond(
                CarDatas(
                    cars.map {
                        toViewModel(it)
                    }
                )
            )
        }
    }
}

private fun toViewModel(car: DomainCar): CarData {
    return CarData(
        car.milage,
        car.price
    )
}