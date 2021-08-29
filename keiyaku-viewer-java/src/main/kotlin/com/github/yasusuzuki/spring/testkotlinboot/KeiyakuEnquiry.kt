package com.github.yasusuzuki.spring.testkotlinboot

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute

import org.slf4j.Logger
import org.slf4j.LoggerFactory
@Controller
class KeiyakuEnquiry(var config: ConfigDef,var dic: Dictionary,var query: DatabaseQuery) {
    val log = LoggerFactory.getLogger(KeiyakuEnquiry::class.java)

    //policyNumberはリストで来る場合とそうでない場合があるが、SpringMVCがコンマ区切りのStringに変換してセットしてくれる
    data class Request(
        var policyNumber: String = "", //複数設定される場合は、コンマ区切りになる
        var verboseMode: Boolean = false
    )

    @GetMapping("/keiyakuEnquiry" )
    fun execute(@ModelAttribute req:Request, model:Model ): String {
        log.info("Process keiyakuEnquiry:  req = $req")

        //callback
        var callback = mapOf(
            "VERBOSE_MODE_FLAG" to fun(_:String?,_:String?,_:List<String>?,_:Map<String,Any?>?):String{
                return if (req.verboseMode) {"on"} else {"off"}
            },
        )
        data class TableResultPair(
            var logicalTableName: String = "",
            var html: () -> String = {""}
        )
        var dataTables : MutableList<TableResultPair> = mutableListOf()
        for ( e in dic.listDBTables("契約") ) {
            //Thymeleafのテンプレート処理まで実行を遅らせることで、すべての編集処理が終わる前
            //ブラウザの描画が始まるので体感処理速度があがる
            var html: () -> String
            //処理前のpolicyNumberは、コンマ区切りだが、シングルクォーテーションが設定されていない
            val polNumsString = req.policyNumber
                .split(",")
                .map { t -> "'" + t + "'" }
                .joinToString(",")
            if (polNumsString == "") {
                html = {"<DIV CLASS='message_info'>証券番号なし</DIV>"}
            } else if ( dic.L2P(e.logicalTableName) == "NO_PHYSICAL_TABLE" ) {
                html = {"<DIV CLASS='message_info'>物理テーブルなし</DIV>"}
            } else {
                if ( config.getCurrentDBServerProduct() == "ACCESS_VIA_ODBC" ) {
                    html = {query.buildHTMLFromSQL(
                    String.format("SELECT * FROM [%s] WHERE [%s] IN (%s) ",
                    e.logicalTableName,
                    "証券＿番号",
                    polNumsString),callback)}
                } else {
                    html = {query.buildHTMLFromSQL(
                    String.format("SELECT * FROM %s WHERE %s IN (%s) ",
                    dic.L2P(e.logicalTableName),
                    dic.L2P("証券＿番号"),
                    polNumsString),callback)}
                }
            }
            dataTables.add(TableResultPair(e.logicalTableName,html))
        }
        model["dataTables"] =  dataTables
        model["appName"] = req.policyNumber
        model["environmentList"] = config.listEnvironment()
        model["req"] = req
        return "keiyakuEnquiry"
    }
}