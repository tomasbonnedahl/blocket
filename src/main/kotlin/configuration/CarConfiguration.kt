package configuration

import application.FetchAndWriteConfiguration

class CarConfiguration {
    companion object {
        fun get(): List<FetchAndWriteConfiguration> {
            return listOf(
                skodaConfiguration(),
                opelConfiguration()
            )
        }
    }
}