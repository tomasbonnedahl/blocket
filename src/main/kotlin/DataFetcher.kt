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


fun opelConfiguration(): Configuration {
    val years = listOf(
        2006,
        2007,
        2008
    )
//    val url = "https://www.blocket.se/stockholm/bilar?cg=1020&w=1&st=s&ca=11&is=1&l=0&md=th&cb=34"
    val url = "https://www.blocket.se/stockholm?q=opel+astra&cg=1020&w=2&r=11&st=s&ps=&pe=&mys=&mye=&ms=&me=&cxpf=&cxpt=&fu=&pl=&gb=&ca=11&is=1&l=0&md=th&cp="
    val config = Configuration(url, "div.media-body", years)
    config.addFields(commonFields2())
    config.addField("brand", BrandField("Opel"))
    return config
}

