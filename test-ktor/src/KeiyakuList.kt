import io.ktor.thymeleaf.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module


class KeiyakuList(var db: DatabaseImpl) : KoinComponent {
    data class Request(
        var id: String = "",
        var keiyakushaName: String = "",
        var maxFetchRows: Int = 100,
        var keiyakuType: String = "",
        var environment: String = ""
    )
    //val db : IDatabase by inject()
    fun processRequest(): Any {
        var req = Request()
        println("Process KeiyakuList  req=[${req}]")
        db.connect()

        val html = ThymeleafContent("keiyakuList",
            mapOf("req" to req)
        )
        return html
    }
}
