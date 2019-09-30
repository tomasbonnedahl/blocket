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
        }
        val apa = cars.groupBy { car ->
            car.date_added.month.toString() + " " + car.date_added.year.toString()
        }

        val apa2 = apa.map { entry ->
            entry.key to entry.value.map { car ->
                toViewModel(car)
            }
        }.toMap()

        return CarDatas(apa2)
//        return CarDatas(
//            cars.map {car ->
//                car.date_added to toViewModel(car)
//            }
//        )
    }

    private fun toViewModel(car: DomainCar): CarViewModel {
        return CarViewModel(
            car.milage,
            car.price,
            car.title + " " + car.model_year + " (" + car.milage + "/" + car.price + ")"
        )
    }
}
