object DirtyFactory {
    fun newFetcher(): NewFetcher {
        return NewFetcherImpl()
    }

    fun newWriter(): NewWriter {
        return NewWriterImpl(newDb())
    }

    fun newDb(): NewDatabase {
        val databaseConfig = DatabaseConfig(
            "blocket",
            "Hej123456",
            "blocket",
            "nimble-sylph-251712:europe-west1:nimble-sylph-251712-1"
        )
        return NewDatabaseImpl(databaseConfig)
    }
}
