import org.jsoup.nodes.Element
import java.time.LocalDate

interface Field {
    fun parseInt(element: Element): Int
    fun parseString(element: Element): String
    fun parseDate(element: Element): LocalDate
}

interface IntField : Field {
    override fun parseString(element: Element): String {
        throw NotImplementedError("parseString() not implemented on IntField")
    }

    override fun parseDate(element: Element): LocalDate {
        throw NotImplementedError("parseDate() not implemented on IntField")
    }
}

interface StringField : Field {
    override fun parseInt(element: Element): Int {
        throw NotImplementedError("parseInt() not implemented on StringField")
    }

    override fun parseDate(element: Element): LocalDate {
        throw NotImplementedError("parseDate() not implemented on StringField")
    }
}

interface DateField : Field {
    override fun parseInt(element: Element): Int {
        throw NotImplementedError("parseInt() not implemented on DateField")
    }

    override fun parseString(element: Element): String {
        throw NotImplementedError("parseString() not implemented on DateField")
    }
}

class TitleField : StringField {
    override fun parseString(element: Element): String {
        return element.select("a[href]").attr("title")
    }
}

class FuelField : StringField {
    override fun parseString(element: Element): String {
        val fuelGearboxMilage = element.select("p.motor-li-thumb-extra-info").text().split(" | ")
        return fuelGearboxMilage[0]
    }
}

class GearboxField : StringField {
    override fun parseString(element: Element): String {
        val fuelGearboxMilage = element.select("p.motor-li-thumb-extra-info").text().split(" | ")
        return fuelGearboxMilage[1]
    }
}

class MilageField : IntField {
    override fun parseInt(element: Element): Int {
        val fuelGearboxMilage = element.select("p.motor-li-thumb-extra-info").text().split(" | ")
        return fuelGearboxMilage[2].replace("mil", "").split("-").last().replace(" ", "").toInt()
    }
}

class PriceField: IntField {
    override fun parseInt(element: Element): Int {
        return try {
            element.select("p.list_price").text().substringBefore("kr").replace(" ", "").toInt()
        } catch (e: NumberFormatException) {
            0
        }
    }
}

class UrlField: StringField {
    override fun parseString(element: Element): String {
        return element.select("h1.h5").select("a[href]").attr("href")
    }
}

// TODO: How to connect to different urls when the Field is stand-alone?
class ModelYearField(val modelYear: Int) : IntField {
    override fun parseInt(element: Element): Int {
        return modelYear
    }
}

class BrandField(val brand: String) : StringField {
    override fun parseString(element: Element): String {
        return brand
    }
}

class DateAddedField : DateField {
    override fun parseDate(element: Element): LocalDate {
        return LocalDate.parse(
            element.select("time").attr("datetime").split(" ").first()
        )
    }
}