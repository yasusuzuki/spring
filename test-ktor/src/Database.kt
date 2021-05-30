import org.koin.dsl.module
import org.koin.experimental.builder.singleBy
import org.koin.core.component.KoinComponent

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

class DatabaseImpl (var config: Config) {
    init {println("init DatabaseImpl ${config}")}
     fun connect(): Unit {
        println("Connecting") 
    }
     fun queryForList(): List<Map<String,Any>> {
        return listOf(mapOf("ID" to 100, "name" to "Mike")) 
    }
}
