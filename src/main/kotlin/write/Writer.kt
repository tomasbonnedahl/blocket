package write

import application.Repo
import application.WriteConfiguration
import domain.DomainCar

interface Writer {
    fun write(car: DomainCar)
}

class WriterImpl(
    val configuration: WriteConfiguration,
    val repo: Repo
) : Writer {
    override fun write(car: DomainCar) {
        val shouldAdd = configuration.filters().none { filter ->
            filter.exclude(car)
        }

        if (shouldAdd) {
            repo.write(car)
        }
    }
}