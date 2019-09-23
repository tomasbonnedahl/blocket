import org.jsoup.Jsoup

interface NewFetcher {
    fun fetch(): List<DomainCar>
}

class NewFetcherImpl(val config: FetchConfiguration) : NewFetcher {
    override fun fetch(): List<DomainCar> {
        return config.years().flatMap { year ->
            val doc = Jsoup.connect(config.urlForYear(year)).get()
            val carListing = doc.select(config.section)
            carListing.map { element ->
                DomainCar(
                    brand = config.getFieldByName("brand").parseString(element),
                    title = config.getFieldByName("title").parseString(element),
                    fuel = config.getFieldByName("fuel").parseString(element),
                    gearbox = config.getFieldByName("gearbox").parseString(element),
                    milage = config.getFieldByName("milage").parseInt(element),
                    price = config.getFieldByName("price").parseInt(element),
                    date_added = config.getFieldByName("date_added").parseDate(element),
                    model_year = year,
                    url = config.getFieldByName("url").parseString(element)
                )
            }
        }
    }
}