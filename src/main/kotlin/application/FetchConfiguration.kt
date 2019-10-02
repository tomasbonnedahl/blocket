package application

class FetchConfiguration(
    val baseUrl: String,
    val section: String,
    val years: List<Int>
) {
    private val fieldByName = mutableMapOf<String, Field>()

    fun years(): List<Int> {
        return years
    }

    fun urlForYear(year: Int): String {
        return "$baseUrl&mys=$year&mye=$year"
    }

    fun addField(name: String, field: Field) {
        fieldByName[name] = field
    }

    fun addFields(fields: Map<String, Field>) {
        fields.forEach { k, v ->
            fieldByName[k] = v
        }
    }

    fun getFieldByName(name: String): Field {
//        application.ModelYearField(2018)
        return fieldByName[name]!!  // TODO: Solve in a better way...
    }
}