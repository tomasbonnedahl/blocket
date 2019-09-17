import com.google.cloud.datastore.DatastoreOptions
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.time.LocalDate

interface NewDatabase {
    fun write(domainCar: DomainCar)
    fun getCars(): List<DomainCar>
    fun removeAll()
}

data class DatabaseConfig(
    val user: String,
    val passwd: String,
    val databaseName: String,
    val instanceConnName: String) {
    val driver
        get() = "com.mysql.jdbc.Driver"
    val connStr
        get() = "jdbc:mysql://google/$databaseName?cloudSqlInstance=$instanceConnName&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false&user=$user&password=$passwd"
}

object NewDbSettings {
    // TODO: lazy? Should/can we guarantee that we only connect once?
    fun init(config: DatabaseConfig) {
        Database.connect(config.connStr,
            driver = config.driver,
            user = config.user,
            password = config.passwd)
    }
}

class NewDatabaseImpl : NewDatabase {
    init {
        org.apache.log4j.BasicConfigurator.configure()
    }

    override fun write(domainCar: DomainCar) {
        // TODO: Add exclusion filter separate from this class
        if (domainCar.price < 100000) {
            // No price or monthly leasing
            return
        }

        if (domainCar.title.contains("RS")) {
            return
        }

        println("Adding ${domainCar.title}")
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
    }

    override fun getCars(): List<DomainCar> {
        var cars = emptyList<DomainCar>()
        transaction {
            println("Car.selectAll().count() = ${Car.selectAll().count()}")
            // TODO: Sort order outside this method
            cars = Car.selectAll().orderBy(Car.milage to SortOrder.ASC).map {
                toDomainCar(it)
            }
        }
        println("cars.size = ${cars.size}")
        return cars // TODO: Check how to do return in transaction {}
    }

    override fun removeAll() {
        transaction {
            Car.deleteAll()
        }
    }

    private fun toDomainCar(resultRow: ResultRow): DomainCar {
        val dateAdded = resultRow.get(Car.date_added)
        return DomainCar(
            brand = resultRow.get(Car.brand),
            title = resultRow.get(Car.title),
            fuel = resultRow.get(Car.fuel),
            gearbox = resultRow.get(Car.gearbox),
            milage = resultRow.get(Car.milage),
            price = resultRow.get(Car.price),
            date_added = LocalDate.of(dateAdded.getYear(), dateAdded.getMonthOfYear(), dateAdded.getDayOfMonth()),
            model_year = resultRow.get(Car.model_year),
            url = resultRow.get(Car.url)
        )
    }
}