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

fun skodaFetchConfiguration(): FetchConfiguration {
    val years = listOf(
        2017,
        2018,
        2019,
        2020,
        2021
    )
//    val url = "https://www.blocket.se/stockholm/bilar?cg=1020&w=1&st=s&ca=11&is=1&l=0&md=th&cb=34"
    val url = "https://www.blocket.se/stockholm/bilar?q=skoda+kodiaq&w=2&r=11&st=s&ca=11&is=1&l=0&md=th&cg=1020&st=s&cb=34"
    val config = FetchConfiguration(url, "div.media-body", years)
    config.addFields(commonFields2())
    config.addField("brand", BrandField("Skoda"))
    return config
}

fun opelFetchConfiguration(): FetchConfiguration {
    val years = listOf(
        2006,
        2007,
        2008
    )
    val url = "https://www.blocket.se/stockholm/bilar?q=opel+astra&w=2&r=11&st=s&ca=11&is=1&l=0&md=th&cg=1020&cb=26"
    val config = FetchConfiguration(url, "div.media-body", years)
    config.addFields(commonFields2())
    config.addField("brand", BrandField("Opel"))
    return config
}

fun skodaWriteConfiguration(): WriteConfiguration {
    return WriteConfiguration(
        listOf(
            PriceExists(),
            Above100kPrice(),
            NoRsModel()
        )
    )
}

fun opelWriteConfiguration(): WriteConfiguration {
    return WriteConfiguration(
        listOf(
            PriceExists()
        )
    )
}

fun skodaConfiguration(): Configuration {
    return Configuration(
        skodaFetchConfiguration(),
        skodaWriteConfiguration()
    )
}

fun opelConfiguration(): Configuration {
    return Configuration(
        opelFetchConfiguration(),
        opelWriteConfiguration()
    )
}