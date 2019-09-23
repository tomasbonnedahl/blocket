class FetchNewData {
    companion object {
        fun run(configuration: Configuration) {
            val fetchAndWrite = FetchAndWrite(
                DirtyFactory.newFetcher(configuration.fetchConfiguration),
                DirtyFactory.newWriter(configuration.writeConfiguration)
            )
            fetchAndWrite.run()
        }
    }

}