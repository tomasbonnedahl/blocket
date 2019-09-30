data class CarViewModel(
    val milage: Int,
    val price: Int,
    val tooltip: String
)

data class CarDatas(
    val dataByMonth: Map<String, List<CarViewModel>>
)

class JsonGetter(val db: Repo) {
    fun carDatas(brand: String?, filterClass: FilterFactory2): CarDatas {
        val cars = when (brand) {
            null -> db.getCars()
            else -> db.getCars(brand, filterClass)
        }.sortedBy { car ->
            car.milage
        }.groupBy { car ->
            car.date_added.month.toString() + " " + car.date_added.year.toString()
        }.map { entry ->
            entry.key to entry.value.map { car ->
                toViewModel(car)
            }
        }.toMap()

        return CarDatas(cars)
    }

    private fun toViewModel(car: DomainCar): CarViewModel {
        return CarViewModel(
            car.milage,
            car.price,
            car.title + " " + car.model_year + " (" + car.milage + "/" + car.price + ")"
        )
    }
}
