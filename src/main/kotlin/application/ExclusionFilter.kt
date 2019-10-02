package application

import domain.DomainCar

interface ExclusionFilter {
    fun exclude(car: DomainCar): Boolean
}

class PriceExists : ExclusionFilter {
    override fun exclude(car: DomainCar): Boolean {
        return car.price == 0
    }
}

class Above100kPrice : ExclusionFilter {
    override fun exclude(car: DomainCar): Boolean {
        return car.price < 100_000
    }
}

class NoRsModel : ExclusionFilter {
    override fun exclude(car: DomainCar): Boolean {
        return car.title.contains("RS")
    }
}