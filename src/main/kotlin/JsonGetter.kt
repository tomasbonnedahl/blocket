data class CarData(
    val milage: Int,
    val price: Int,
    val tooltip: String
)

data class CarDatas(
    val data: List<CarData>
)

class JsonGetter(val db: Repo) {
    fun carDatas(): CarDatas {
        val cars = db.getCars()
        return CarDatas(
            cars.map {
                toViewModel(it)
            }
        )
    }

    private fun toViewModel(car: DomainCar): CarData {
        return CarData(
            car.milage,
            car.price,
            car.title + " (" + car.milage + "/" + car.price + ")"
        )
    }
}
