import com.google.auth.Credentials
import com.google.cloud.storage.BucketInfo
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
import com.google.cloud.storage.Storage.BucketListOption
import com.google.cloud.storage.StorageOptions
import io.ktor.html.respondHtml
import kotlinx.html.head
import kotlinx.html.body
import kotlinx.html.p
import kotlinx.html.title
import org.apache.http.client.methods.RequestBuilder.head
import java.io.FileInputStream
import com.google.auth.oauth2.GoogleCredentials



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
            setDateFormat(DateFormat.LONG)  // TODO: Change to Simple?
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
//             TODO: Abtract some of this code
            try {
                val db = Db()
                val cars = db.getCars()
                call.respond(
                    CarDatas(
                        cars.map {
                            toViewModel(it)
                        }
                    )
                )
                call.respond(HttpStatusCode.OK)
            }
            catch (e: java.lang.Exception) {
                println("Tomas e = ${e}")
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/list-buckets") {
            val storage = StorageOptions.getDefaultInstance().getService()
//            val storage = StorageOptions.
//                newBuilder().
//                setProjectId("nimble-sylph-251712").
//                build().
//                service
//            val credentials = GoogleCredentials.fromStream(
//                FileInputStream("/Users/tomasb/gcp-key/Blocket-85796bf414a3.json")
//            )
//                .createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))
//            val storage = StorageOptions.newBuilder().setCredentials(credentials).build().service

            val buckets = storage.list()
//            for (bucket in buckets.iterateAll()) {
//                val blobs = bucket.list()
//                for (blob in blobs.iterateAll()) {
//                    // do something with the blob
//                }
//            }

            val str = buckets.iterateAll().map {bucket ->
                bucket.name
            }

            call.respondHtml {
                head {
                    title { +"GCP buckets" }
                }
                body {
                    p {
                        +str.joinToString(", ")
                    }
                }
            }
        }
    }
}

private fun toViewModel(car: DomainCar): CarData {
    return CarData(
        car.milage,
        car.price
    )
}