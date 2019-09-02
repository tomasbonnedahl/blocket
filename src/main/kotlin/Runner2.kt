import org.jsoup.Jsoup

class Runner2(val configuration: Configuration) {
    fun run() {
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
//        db.list()
        db.priceVsMilage()
    }
}