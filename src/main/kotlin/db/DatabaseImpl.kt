package db

import application.Repo
import domain.DomainCar
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import view.FilterAttribute
import view.FilterEnum
import java.time.LocalDate

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

class NewDatabaseImpl : Repo {
    init {
        org.apache.log4j.BasicConfigurator.configure()
    }

    override fun write(domainCar: DomainCar) {
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
        return transaction {
            Car.selectAll().map {
                toDomainCar(it)
            }
        }
    }
//
//    private fun columnAndStringFromFilter(filter: FilterEnum): StringColumnAndValue {
//        return when (filter) {
//            FilterEnum.DIESEL, FilterEnum.BENSIN -> StringColumnAndValue(Car.fuel,  filter.text)
//            FilterEnum.MANUELL, FilterEnum.AUTOMAT -> StringColumnAndValue(Car.gearbox, filter.text)
//        }
//    }

    private fun columnAndStringFromFilter2(filter: FilterEnum): GenericColumnAndValue<String> {
        return when (filter) {
            FilterEnum.DIESEL, FilterEnum.BENSIN -> GenericColumnAndValue(Car.fuel, filter.text)
            FilterEnum.MANUELL, FilterEnum.AUTOMAT -> GenericColumnAndValue(Car.gearbox, filter.text)
        }
    }

//    private fun columnAndInt(value: Int): IntColumnAndValue {
//        return IntColumnAndValue(Car.model_year, value)
//    }

    private fun columnAndInt2(value: Int): GenericColumnAndValue<Int> {
        return GenericColumnAndValue(Car.model_year, value)
    }

    override fun getCars(
        brand: String,
        filters: List<FilterAttribute<Any>>
    ): List<DomainCar> {
        return transaction {
            val query = Car.select {
                Car.brand eq brand
            }

            // Add filters to query
            filters.forEach { filter ->
                when (filter.attr) {
                    is FilterEnum -> {
                        val columnAndString = columnAndStringFromFilter2(filter.attr)
                        query.andWhere {
                            columnAndString.column eq columnAndString.value.capitalize()
                        }
                    }
                    is Int -> {
                        val columnAndInt = columnAndInt2(filter.attr.toInt())
                        query.andWhere {
                            columnAndInt.column eq columnAndInt.value
                        }
                    }
                }
            }

            query.map { toDomainCar(it) }
        }
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
//
//private data class StringColumnAndValue(
//    val column: Column<String>,
//    val str: String
//)
//
//private data class IntColumnAndValue(
//    val column: Column<Int>,
//    val value: Int
//)

private data class GenericColumnAndValue<T>(
    val column: Column<T>,
    val value: T
)