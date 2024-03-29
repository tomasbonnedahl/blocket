package view

import application.Repo
import domain.DomainCar

data class CarViewModel(
    val milage: Int,
    val price: Int,
    val tooltip: String
)

data class ViewCars(
    val dataByMonth: Map<String, List<CarViewModel>>
)

// TODO: Move to repo file
enum class FilterEnum(var text: String) {
    DIESEL("Diesel"),
    BENSIN("Bensin"),
    MANUELL("Manuell"),
    AUTOMAT("Automat"),
    MODEL_YEAR("")
}

// TODO: Move to repo file
data class FilterAttribute<T>(
    val attr: T
)

class ViewCarsFactory {
    companion object {
        fun of(
            repo: Repo,
            brand: String?,
            filterStrings: List<String?>
        ): ViewCars {

            val filters = filterStrings.mapNotNull {
                enumFromString(it!!)
            }

            val cars = when (brand) {
                null -> repo.getCars()
                else -> repo.getCars(brand, filters)
            }.sortedBy { car ->
                car.milage
            }.groupBy { car ->
                car.date_added.month.toString() + " " + car.date_added.year.toString()
            }.map { entry ->
                entry.key to entry.value.map { car ->
                    toViewModel(car)
                }
            }.toMap()

            return ViewCars(cars)
        }

        private fun enumFromString(str: String): FilterEnum {
            return when (str.toLowerCase()) {
                "diesel" -> FilterEnum.DIESEL
                "bensin" -> FilterEnum.BENSIN
                "manuell" -> FilterEnum.MANUELL
                "automat" -> FilterEnum.AUTOMAT
                else -> {
                    val e = FilterEnum.MODEL_YEAR
                    e.text = str
                    e
                }
            }
        }

        private fun toViewModel(car: DomainCar): CarViewModel {
            return CarViewModel(
                car.milage,
                car.price,
                car.title + " " + car.model_year + " (" + car.milage + "/" + car.price + ")"
            )
        }
    }
}
