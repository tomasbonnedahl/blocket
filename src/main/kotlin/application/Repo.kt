package application

import domain.DomainCar
import view.FilterEnum

interface Repo {
    fun write(domainCar: DomainCar)
    fun getCars(): List<DomainCar>
    fun getCars(brand: String, filters: List<FilterEnum>): List<DomainCar>
    fun removeAll()
}