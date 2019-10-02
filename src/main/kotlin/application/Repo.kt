package application

import db.FilterWrapper
import domain.DomainCar

interface Repo {
    fun write(domainCar: DomainCar)
    fun getCars(): List<DomainCar>
    fun getCars(brand: String, filterClass: FilterWrapper): List<DomainCar>  // TODO: Enum instead of string?
    fun removeAll()
}