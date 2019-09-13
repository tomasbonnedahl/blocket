import com.google.cloud.datastore.DatastoreOptions
import com.google.cloud.storage.StorageOptions
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.html.respondHtml
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondFile
import io.ktor.routing.get
import io.ktor.routing.routing
import kotlinx.html.body
import kotlinx.html.head
import kotlinx.html.p
import kotlinx.html.title
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
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
            } catch (e: Exception) {
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
            } catch (e: java.lang.Exception) {
                println("Tomas e = ${e}")
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/cloudsql") {
            //            val dummyThing = System.getProperty("cloudsql")
            val dummyThing = "null"
            val db = DbSettings.db
            val apa = transaction {
                SchemaUtils.create(Car)
            }

            val domainCar = DomainCar(
                brand = "Skoda",
                title = "Skoda Kodiaq",
                fuel = "Diesel",
                gearbox = "Automat",
                milage = 1234,
                price = 299000,
                date_added = LocalDate.of(2019, 8, 1),
                model_year = 2018,
                url = "http://www.blocket.se/skoda"
            )

            transaction {
                val existing = Car.select {
                    Car.url eq domainCar.url
                }.count() > 0

                if (!existing) {
                    println("Adding, doesn't exist")
                    Car.insert { car ->
                        car[brand] = domainCar.brand
                        car[title] = domainCar.title
                        car[fuel] = domainCar.fuel
                        car[gearbox] = domainCar.gearbox
                        car[milage] = domainCar.milage
                        car[price] = domainCar.price
                        car[date_added] = DateTime.parse(domainCar.date_added.toString())
                        car[model_year] = domainCar.model_year
                        car[url] = domainCar.url
                        car[emailed] = false
                    }
                }
                else {
                    println("Not adding, already existing")
                }
            }

            val str = transaction {
                Car.selectAll().map { row ->
                    row.get(Car.title)
                }
            }

            val datastore = DatastoreOptions.newBuilder().setProjectId("nimble-sylph-251712").build().service
            val taskKey = datastore.newKeyFactory().setKind("ENV_VAR").newKey("ENV_VARS")
            val entity = datastore.get(taskKey)
            val values = listOf("TEST_KEY", "TEST_KEY2").map {
                entity.getString(it)
            }

            call.respondHtml {
                head {
                    title { +"Cloud SQL" }
                }
                body {
                    p {
                        +str.joinToString()
                    }
                    p {
                        +values.joinToString()
                    }
                }
            }
        }

        get("/list-buckets") {
            val storage = StorageOptions.
                newBuilder().
                setProjectId("nimble-sylph-251712").
                build().
                service

            val buckets = storage.list()
            val str = buckets.iterateAll().map { bucket ->
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