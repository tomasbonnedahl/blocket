import org.jsoup.Jsoup
import java.time.LocalDate

// https://www.blocket.se/stockholm?q=skoda+kodiaq&cg=0&w=2&r=11&st=s&ca=11&is=1&l=0&md=th
// https://www.blocket.se/stockholm/bilar?q=skoda+kodiaq&w=2&r=11&st=s&ca=11&is=1&l=0&md=th&cg=1020&st=s

// Car category: cg=1020 (&st=s?)

val url = "https://www.blocket.se/stockholm/bilar?q=skoda+kodiaq&w=2&r=11&st=s&ca=11&is=1&l=0&md=th&cg=1020&st=s"

fun main(args: Array<String>) {
    org.apache.log4j.BasicConfigurator.configure()

    val doc = Jsoup.connect(url).get()
//    println("doc = ${doc}")
//    doc.select(".wikitable:first-of-type tr td:first-of-type a")
//        .map { col -> col.attr("href") }
//        .parallelStream()
//        .map { extractMovieData(it) }
//        .filter { it != null }
//        .forEach { println(it) }
//    val apa = doc.select("blocket:first-of-type")
//    val apa2 = doc.getElementById("item_list")
    val mediaBody = doc.select("div.media-body")
    mediaBody.forEach { it ->
        val title = it.select("a[href]").attr("title")
        val price = it.select("p.list_price").text().substringBefore("kr").replace(" ", "").toInt()
        val fuelGearboxMilage = it.select("p.motor-li-thumb-extra-info").text().split(" | ")
        val (fuel, gearbox, milageTemp) = fuelGearboxMilage
        val milage = milageTemp.replace("mil", "").split("-").last().replace(" ", "")
        val url = it.select("h1.h5").select("a[href]").attr("href")
        val dateAdded = LocalDate.parse(
            it.select("time").attr("datetime").split(" ").first()
        )

        println("mediaBody = ${mediaBody}")

        println("---")
        println("title = ${title}")
        println("dateAdded = ${dateAdded}")
        println("price = ${price}")
        println("fuel = ${fuel}")
        println("gearbox = ${gearbox}")
        println("milage = ${milage}")
        println("url = ${url}")
    }

//    insertToDb()
    val db = Db()
    db.add()  // TODO: Remove RS (caps) models
    db.list()
}
