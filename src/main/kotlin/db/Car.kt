package db

import org.jetbrains.exposed.dao.IntIdTable

object Car : IntIdTable() {
    val brand = varchar("brand", 50)
    val title = varchar("title", 200)
    val fuel = varchar("fuel", 20)
    val gearbox = varchar("gearbox", 20)
    val milage = integer("milage")
    val price = integer("price")
    val date_added = date("date_added")
    val model_year = integer("model_year")
    val url = varchar("url", 200)
    val emailed = bool("emailed")
}