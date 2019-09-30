import org.jetbrains.exposed.sql.Column

data class Filter2<T>(
    val attr: Column<T>,
    val value: T
)

class FilterFactory2 private constructor(
    val stringFilters: List<Filter2<String>>,
    val intFilters: List<Filter2<Int>>
) {
    companion object {
        fun of(values: List<String?>): FilterFactory2 {
            val stringFilters = mutableListOf<Filter2<String>>()
            val intFilters = mutableListOf<Filter2<Int>>()

            values.forEach { value ->
                when (value) {
                    null -> {}  // Do nothing
                    else -> when (value.toLowerCase()) {
                        "diesel", "bensin" -> stringFilters.add(
                            createStringFilter(Car.fuel, value)
                        )
                        "automat", "manuell" -> stringFilters.add(
                            createStringFilter(Car.gearbox, value)
                        )
                        else -> {
                            try {
                                val intValue = value.toInt()
                                intFilters.add(
                                    Filter2(Car.model_year, intValue)
                                )
                            } catch (e: NumberFormatException) {
                                throw Exception("Unknown attribute for {$value}")
                            }
                        }
                    }
                }
            }
            return FilterFactory2(stringFilters, intFilters)
        }

        private fun createStringFilter(attr: Column<String>, value: String): Filter2<String> {
            return Filter2(attr, value.capitalize())
        }

    }
}
