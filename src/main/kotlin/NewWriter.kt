interface NewWriter {
    fun write(car: DomainCar)
}

class NewWriterImpl(val db: NewDatabase) : NewWriter {
    override fun write(car: DomainCar) {
        db.write(car)
    }
}