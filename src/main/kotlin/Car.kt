import org.jetbrains.exposed.dao.IntIdTable

object Car : IntIdTable() {
    val title = varchar("title", 200)
}