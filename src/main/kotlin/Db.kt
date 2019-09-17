import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.andWhere
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
    val created = file.createNewFile()
    println("created new db file = ${created}")
    return file
}

object DbSettings {
    val db by lazy {
        val user = "blocket"
        val passwd = "Hej123456"
        val databaseName = "blocket"
        val instanceConnName = "nimble-sylph-251712:europe-west1:nimble-sylph-251712-1"
        val connStr = "jdbc:mysql://google/$databaseName?cloudSqlInstance=$instanceConnName&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false&user=$user&password=$passwd"
        Database.connect(connStr,
            driver = "com.mysql.jdbc.Driver",
            user = user,
            password = passwd
        )
    }
}

// TODO: Should be an interface?
class Db {
    init {
        org.apache.log4j.BasicConfigurator.configure()

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

    fun addDomainCar(domainCar: DomainCar) {
        // TODO: Add exclusion filter separate from this class
        /*
        .map { runExclusionFilters(it) }
            .filter { it != null }
            .forEach { store(it) }
         */
        if (domainCar.price == 0) {
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
}
