package view

import db.FilterWrapper
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

class ViewCarsFactory {
    companion object {
        fun of(
            repo: Repo,
            brand: String?,
            filterClass: FilterWrapper
        ): ViewCars {
            val cars = when (brand) {
                null -> repo.getCars()
                else -> repo.getCars(brand, filterClass)
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

        private fun toViewModel(car: DomainCar): CarViewModel {
            return CarViewModel(
                car.milage,
                car.price,
                car.title + " " + car.model_year + " (" + car.milage + "/" + car.price + ")"
            )
        }
    }
}
