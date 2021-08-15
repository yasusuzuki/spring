import io.ktor.thymeleaf.*
import io.ktor.http.Parameters
import io.ktor.util.getValue
class KeiyakuList(var db: Database) {

    data class Request(
        var id: String="",
        var keiyakushaName: String="",
        var maxFetchRows: Int=100,
        var keiyakuType: String="",
        var environment: String="",
    )

    /* 
    //Thymeleafから参照されるとParameeters.getValue()が呼び出される。
    //該当するパラメータがParametersにないと、例外が発生し、Thymeleafの処理が中断される
    //TODO:デフォルト設定ができないものか。。
    class RequestOLD(val map: Parameters) {
        val id: String? by map
        val keiyakushaName: String? by map
        val maxFetchRows: Int? by map //TODO: how to set default 100
        val keiyakuType: String? by map
        val environment: String? by map
    }
    */

    fun processRequest(req:Request): Any {
        println("Process KeiyakuList  req=[${req}]")
        db.connect()

        val html = ThymeleafContent("keiyakuList",
            mapOf("req" to req)
        )
        return html
    }
}
