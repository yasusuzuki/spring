
data class Config (
    var host : String = "",
    var user : String = ""
) {
    init { println("init Config..")}
}

interface Database {
    fun connect(): Unit
    fun queryForList(): List<Map<String,Any>>
}

class DatabaseMock (var config: Config) : Database {
    init {println("init DatabaseImpl ${config}")}
    override fun connect(): Unit {
        println("Connecting") 
    }
    override fun queryForList(): List<Map<String,Any>> {
        return listOf(mapOf("ID" to 100, "name" to "Mike")) 
    }
}
