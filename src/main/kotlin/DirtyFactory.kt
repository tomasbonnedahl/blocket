import com.google.cloud.datastore.DatastoreOptions

// TODO: Interface to factory?
object DirtyFactory {
    init {
        // TODO: Move to a separate class
        NewDbSettings.init(databaseConfig())
    }

    fun newFetcher(fetchConfiguration: FetchConfiguration): NewFetcher {
        return NewFetcherImpl(fetchConfiguration)
    }

    fun newWriter(writeConfiguration: WriteConfiguration): NewWriter {
        return NewWriterImpl(writeConfiguration, newDb())
    }

    fun newDb(): Repo {
        return NewDatabaseImpl()
    }

    private fun databaseConfig(): DatabaseConfig {
        val datastore = DatastoreOptions.newBuilder().setProjectId("nimble-sylph-251712").build().service
        val taskKey = datastore.newKeyFactory().setKind("ENV_VAR").newKey("ENV_VARS")
        val entity = datastore.get(taskKey)
        return DatabaseConfig(
            entity.getString("USER"),
            entity.getString("PASSWD"),
            entity.getString("DATABASE_NAME"),
            entity.getString("INSTANCE_CONN_NAME")
        )
    }
}
