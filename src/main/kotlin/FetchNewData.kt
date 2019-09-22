class FetchNewData {
    companion object {
        fun run(configuration: Configuration) {
            val fetchAndWrite = FetchAndWrite(
                DirtyFactory.newFetcher(),
                DirtyFactory.newWriter(),
                configuration
            )
            fetchAndWrite.run()
        }
    }

}