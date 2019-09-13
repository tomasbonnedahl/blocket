import com.google.appengine.api.datastore.KeyFactory
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
import com.google.cloud.datastore.DatastoreOptions
import com.google.cloud.datastore.Datastore
import sun.security.rsa.RSAPrivateCrtKeyImpl.newKey



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
            }

            val str = transaction {
                Car.selectAll().map { row ->
                    row.get(Car.title)
                }
            }

//            val datastore = DatastoreServiceFactory.getDatastoreService()
//            val datastore = DatastoreOptions.getDefaultInstance().service
//            val asfas = DatastoreOptions.Builder().setProjectId("nimble-sylph-251712").build().service
//            val taskKey = datastore.newKeyFactory().setKind("ENV_VAR").newKey("ENV_VARS")
//            val entity = datastore.get(taskKey)
//            val value = entity.getString("TEST_KEY")

//            val datastore = DatastoreOptions.getDefaultInstance().service
            val datastore = DatastoreOptions.
                newBuilder().
                setProjectId("nimble-sylph-251712").
                build().service
//            val key = KeyFactory.createKey("ENV_VAR", "ENV_VARS")
//            val ds = DatastoreServiceFactory.getDatastoreService()
//            val entity = ds.get(key)
//            val value = entity.toString()
            val taskKey = datastore.newKeyFactory().setKind("ENV_VAR").newKey("ENV_VARS")
            val entity = datastore.get(taskKey)
            val value = entity.getString("TEST_KEY")

//            val key = KeyFactory.createKey("ENV_VAR", "ENV_VARS")
//            val q = Query("ENV_VAR").addSort(Entity.KEY_RESERVED_PROPERTY)
//            val res = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults())
//            val vars = res.map {
//                "${it.key} -> ${datastore.get(it.key)}"
//            }
//            val entity = datastore.get(key)
//            val value = entity.getProperty("TEST_KEY").toString()

//            val afs = GoogleCredentials.fromStream(FileInputStream("asf")) as Credential
//            val PROJECT_ID = "nimble-sylph-251712"
//            val options = DatastoreOptions.Builder().
//                credential(afs).
//                projectId(PROJECT_ID).
//                build()
//            val datastore = options. getService()
//            val keyFactory = datastore.newKeyFactory().setKind(KIND)
//            val key = keyFactory.newKey(keyName)
//            val entity = datastore.get(key)

            call.respondHtml {
                head {
                    title { +"Cloud SQL" }
                }
                body {
                    p {
                        +str.joinToString()
                    }
                    p {
                        //                        +dummyThing
//                        +str.joinToString()
//                        +vars.joinToString()
                        +value
                    }
                }
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