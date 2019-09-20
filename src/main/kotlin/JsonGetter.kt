data class CarData(
    val milage: Int,
    val price: Int,
    val tooltip: String
)

data class CarDatas(
    val dataByMonth: Map<String, List<CarData>>
)

class JsonGetter(val db: Repo) {
    fun carDatas(): CarDatas {
        val cars = db.getCars()
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

    private fun toViewModel(car: DomainCar): CarData {
        return CarData(
            car.milage,
            car.price,
            car.title + " (" + car.milage + "/" + car.price + ")"
        )
    }
}
