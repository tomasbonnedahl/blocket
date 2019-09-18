class FetchAndWrite(
    val fetcher: NewFetcher,
    val writer: NewWriter,
    val fetchConfig: Configuration
) {
    fun run() {
        fetcher.fetch(fetchConfig).forEach { car ->
            writer.write(car)
        }
    }
}