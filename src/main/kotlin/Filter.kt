import org.jetbrains.exposed.sql.Column

data class Filter<T>(
    val attr: Column<T>,
    val value: T
)

class FilterWrapper private constructor(
    val stringFilters: List<Filter<String>>,
    val intFilters: List<Filter<Int>>
) {
    companion object {
        fun of(values: List<String?>): FilterWrapper {
            val stringFilters = mutableListOf<Filter<String>>()
            val intFilters = mutableListOf<Filter<Int>>()

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
                                    Filter(Car.model_year, intValue)
                                )
                            } catch (e: NumberFormatException) {
                                throw Exception("Unknown attribute for {$value}")
                            }
                        }
                    }
                }
            }
            return FilterWrapper(stringFilters, intFilters)
        }

        private fun createStringFilter(attr: Column<String>, value: String): Filter<String> {
            return Filter(attr, value.capitalize())
        }

    }
}
