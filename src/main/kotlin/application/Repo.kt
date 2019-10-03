package application

import domain.DomainCar
import view.FilterAttribute  // TODO: Move to application, at least from view

interface Repo {
    fun write(domainCar: DomainCar)
    fun getCars(): List<DomainCar>
    fun getCars(brand: String, filters: List<FilterAttribute<Any>>): List<DomainCar>
    fun removeAll()
}