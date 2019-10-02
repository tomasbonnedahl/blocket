package application

data class FetchAndWriteConfiguration(
    val fetchConfiguration: FetchConfiguration,
    val writeConfiguration: WriteConfiguration
)