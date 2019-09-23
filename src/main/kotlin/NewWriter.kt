interface NewWriter {
    fun write(car: DomainCar)
}

class NewWriterImpl(
    val configuration: WriteConfiguration,
    val db: Repo
) : NewWriter {
    override fun write(car: DomainCar) {
        val shouldAdd = configuration.filters().none { filter ->
            filter.exclude(car)
        }

        if (shouldAdd) {
            db.write(car)
        }
    }
}