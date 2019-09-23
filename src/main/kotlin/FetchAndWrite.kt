class FetchAndWrite(
    val fetcher: NewFetcher,
    val writer: NewWriter
) {
    fun run() {
        fetcher.fetch().forEach { car ->
            writer.write(car)
        }
    }
}