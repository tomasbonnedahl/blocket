import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.sqlite.SQLiteDataSource
import java.io.File
import java.sql.Connection

fun apa() {
    var file = File("foo.db")
    file.createNewFile()

    val dataSource = SQLiteDataSource()
    dataSource.url = "jdbc:sqlite:" + file.name
    val db = Database.connect(dataSource)

    // SQLite supports only TRANSACTION_SERIALIZABLE and TRANSACTION_READ_UNCOMMITTED
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    transaction {
        SchemaUtils.create(Car)
    }

    transaction {
        Car.insert { car ->
            car[title] = "A Skoda"
        }
    }

    transaction {
        val query = Car.selectAll()
        query.forEach {
            println("it = ${it}")
        }
    }
}