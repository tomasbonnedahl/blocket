import org.jsoup.Jsoup
import java.time.LocalDate

// https://www.blocket.se/stockholm?q=skoda+kodiaq&cg=0&w=2&r=11&st=s&ca=11&is=1&l=0&md=th
// https://www.blocket.se/stockholm/bilar?q=skoda+kodiaq&w=2&r=11&st=s&ca=11&is=1&l=0&md=th&cg=1020&st=s

// Car category: cg=1020 (&st=s?)

// https://www.blocket.se/stockholm/bilar?cg=1020&w=1&st=s&ca=11&is=1&l=0&md=th&cb=34
// 34 = Skoda
// 26 = Opel

fun commonFields(): Map<String, Field> {
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

fun skodaConfiguration(): Configuration {
    val years = listOf(
        2017,
        2018,
        2019
    )
//    val url = "https://www.blocket.se/stockholm/bilar?cg=1020&w=1&st=s&ca=11&is=1&l=0&md=th&cb=34"
    val url = "https://www.blocket.se/stockholm/bilar?q=skoda+kodiaq&w=2&r=11&st=s&ca=11&is=1&l=0&md=th&cg=1020&st=s&cb=34"
    val config = Configuration(url, "div.media-body", years)
    config.addFields(commonFields())
    config.addField("brand", BrandField("Skoda"))
//    config.addField("model_year", ModelYearField(2018))
    return config
}

fun main(args: Array<String>) {
    org.apache.log4j.BasicConfigurator.configure()

//    val url = "https://www.blocket.se/stockholm/bilar?q=skoda+kodiaq&w=2&r=11&st=s&ca=11&is=1&l=0&md=th&cg=1020&st=s"
//    val runner2 = Runner2(skodaConfiguration())
//    runner2.run()

//    val doc = Jsoup.connect(url).get()
//    val mediaBody = doc.select("div.media-body")
//    mediaBody.forEach { it ->
//        val title = it.select("a[href]").attr("title")
//        val price = it.select("p.list_price").text().substringBefore("kr").replace(" ", "").toInt()
//        val fuelGearboxMilage = it.select("p.motor-li-thumb-extra-info").text().split(" | ")
//        val (fuel, gearbox, milageTemp) = fuelGearboxMilage
//        val milage = milageTemp.replace("mil", "").split("-").last().replace(" ", "")
//        val url = it.select("h1.h5").select("a[href]").attr("href")
//        val dateAdded = LocalDate.parse(
//            it.select("time").attr("datetime").split(" ").first()
//        )
//
//        println("---")
//        println("title = ${title}")
//        println("dateAdded = ${dateAdded}")
//        println("price = ${price}")
//        println("fuel = ${fuel}")
//        println("gearbox = ${gearbox}")
//        println("milage = ${milage}")
//        println("url = ${url}")
//    }
//
////    insertToDb()
//    val db = Db()
//    db.add()  // TODO: Remove RS (caps) models
//    db.list()
}
