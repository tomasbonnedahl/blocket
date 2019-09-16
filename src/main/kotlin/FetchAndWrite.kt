class FetchAndWrite(
    val fetcher: NewFetcher,
    val writer: NewWriter,
    val fetchConfig: Configuration
) {
    fun run() {
        fetcher.fetch(fetchConfig).forEach { car ->
            println("Writing car.title = ${car.title}")
            writer.write(car)
        }
    }
}