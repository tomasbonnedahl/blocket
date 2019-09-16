import org.jsoup.Jsoup

fun commonFields2(): Map<String, Field> {
    return mapOf(
        "title" to TitleField(),
        "fuel" to FuelField(),
        "gearbox" to GearboxField(),
        "milage" to MilageField(),
        "price" to PriceField(),
        "date_added" to DateAddedField(),
        "url" to UrlField()
    )
}

fun skodaConfiguration2(): Configuration {
    val years = listOf(
        2017,
        2018,
        2019,
        2020,
        2021
    )
//    val url = "https://www.blocket.se/stockholm/bilar?cg=1020&w=1&st=s&ca=11&is=1&l=0&md=th&cb=34"
    val url = "https://www.blocket.se/stockholm/bilar?q=skoda+kodiaq&w=2&r=11&st=s&ca=11&is=1&l=0&md=th&cg=1020&st=s&cb=34"
    val config = Configuration(url, "div.media-body", years)
    config.addFields(commonFields2())
    config.addField("brand", BrandField("Skoda"))
//    config.addField("model_year", ModelYearField(2018))
    return config
}

class DataFetcher {  // TODO: Interface?
    fun fetch(configuration: Configuration) {
        val db = Db()  // TODO: Class/method is doing too much...

        configuration.years().forEach { year ->
            val doc = Jsoup.connect(configuration.urlForYear(year)).get()
            val carListing = doc.select(configuration.section)
            carListing.forEach { element ->
                val car = DomainCar(
                    brand = configuration.getFieldByName("brand").parseString(element),
                    title = configuration.getFieldByName("title").parseString(element),
                    fuel = configuration.getFieldByName("fuel").parseString(element),
                    gearbox = configuration.getFieldByName("gearbox").parseString(element),
                    milage = configuration.getFieldByName("milage").parseInt(element),
                    price = configuration.getFieldByName("price").parseInt(element),
                    date_added = configuration.getFieldByName("date_added").parseDate(element),
                    model_year = year,
                    url = configuration.getFieldByName("url").parseString(element)
                )
                db.addDomainCar(car)
            }
        }
    }
}