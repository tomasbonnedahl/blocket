import application.FetchConfiguration
import application.Repo
import application.WriteConfiguration
import com.google.cloud.datastore.DatastoreOptions
import db.DatabaseConfig
import db.NewDatabaseImpl
import db.NewDbSettings
import fetch.Fetcher
import fetch.FetcherImpl
import write.Writer
import write.WriterImpl

// TODO: Interface to factory?
object DirtyFactory {
    init {
        // TODO: Move to a separate class? Best practise?
        NewDbSettings.init(databaseConfig())
    }

    fun newFetcher(fetchConfiguration: FetchConfiguration): Fetcher {
        return FetcherImpl(fetchConfiguration)
    }

    fun newWriter(writeConfiguration: WriteConfiguration): Writer {
        return WriterImpl(writeConfiguration, newRepo())
    }

    fun newRepo(): Repo {
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
