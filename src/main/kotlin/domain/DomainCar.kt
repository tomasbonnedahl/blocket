package domain

import java.time.LocalDate

data class DomainCar(
    val brand: String,
    val title: String,
    val fuel: String,
    val gearbox: String,
    val milage: Int,
    val price: Int,
    val date_added: LocalDate,
    val model_year: Int,
    val url: String
)