class CarConfiguration {
    companion object {
        fun get(): List<Configuration> {
            return listOf(
                skodaConfiguration(),
                opelConfiguration()
            )
        }
    }
}