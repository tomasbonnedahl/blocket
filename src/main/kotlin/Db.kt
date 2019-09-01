import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.sqlite.SQLiteDataSource
import java.io.File
import java.sql.Connection
import java.time.LocalDate

fun createDbFile(): File {
    var file = File("foo.db")
    file.createNewFile()
    return file
}

class Db {
    init {
        val file = createDbFile()
        val dataSource = SQLiteDataSource()
        dataSource.url = "jdbc:sqlite:" + file.name
        Database.connect(dataSource)

        // SQLite supports only TRANSACTION_SERIALIZABLE and TRANSACTION_READ_UNCOMMITTED
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        transaction {
            SchemaUtils.create(Car)
        }
    }

    fun add() {
        println("Adding to db")
        transaction {
            val uniqueId2 = "Skoda Kodiaq" + 1234 + 299000

            val existing = Car.select {
                Car.unique_id eq uniqueId2
            }.count()

            if (existing == 0) {
                Car.insert { car ->
                    car[brand] = "Skoda"
                    car[title] = "Skoda Kodiaq"
                    car[fuel] = "Diesel"
                    car[gearbox] = "Automat"
                    car[milage] = 1234
                    car[price] = 299000
                    car[date_added] = DateTime.parse(LocalDate.of(2018, 1, 1).toString())
                    car[model_year] = 2018
                    car[unique_id] = uniqueId2
                }
            }
        }
    }

    fun list() {
        println("Querying db:")
        transaction {
            val query = Car.selectAll()
            query.forEach {
                println("it.get(Car.brand) = ${it.get(Car.brand)}")
                println("it.get(Car.title) = ${it.get(Car.title)}")
                println("it.get(Car.fuel) = ${it.get(Car.fuel)}")
                println("it.get(Car.gearbox) = ${it.get(Car.gearbox)}")
                println("it.get(Car.milage) = ${it.get(Car.milage)}")
                println("it.get(Car.price) = ${it.get(Car.price)}")
                println("it.get(Car.date_added) = ${it.get(Car.date_added)}")
                println("it.get(Car.model_year) = ${it.get(Car.model_year)}")
                println("it.get(Car.unique_id) = ${it.get(Car.unique_id)}")
            }
        }
    }
}
