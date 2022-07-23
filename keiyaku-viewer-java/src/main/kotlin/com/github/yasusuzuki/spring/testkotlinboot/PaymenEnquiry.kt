package com.github.yasusuzuki.spring.testkotlinboot

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute

import org.slf4j.Logger
import org.slf4j.LoggerFactory
@Controller
class PaymentEnquiry(var config: ConfigDef,var dic: Dictionary,var query: DatabaseQuery) {
    val log = LoggerFactory.getLogger(PaymentEnquiry::class.java)

    data class Request(
        var cardNumber: String = "",
        var accountNumber: String = "",
        var verboseMode: Boolean = false
    )

    @GetMapping("/paymentEnquiry" )
    fun execute(@ModelAttribute req:Request, model:Model ): String {
        log.info("Process paymentEnquiry:  req = $req")

        //callback
        var callback = mapOf(
            //TODO: 証券番号、提案番号をここで取得する
            "VERBOSE_MODE_FLAG" to fun(_:String?,_:String?,_:List<String>?,_:Map<String,Any?>?):String{
                return if (req.verboseMode) {"on"} else {"off"}
            },
        )
        data class TableResultPair(
            var logicalTableName: String = "",
            var html: () -> String = {""}
        )
        var dataTables : MutableList<TableResultPair> = mutableListOf()
        for ( e in dic.listDBTables("収納手段") ) {
            //Thymeleafのテンプレート処理まで実行を遅らせることで、すべての編集処理が終わる前
            //ブラウザの描画が始まるので体感処理速度があがる
            var html: () -> String
            //TODO:証券番号など、PKによってSQLを変化させる
            var cardOrAccount = ""
            if ( e.primaryKeysString == "取引クレジットカード＿番号" ){ 
                cardOrAccount = "CARD"
            } else if (e.primaryKeysString == "取引クレジットカード＿番号" ){
                cardOrAccount = "ACCOUNT"
            }
            val searchKey = if ( cardOrAccount == "CARD" ) {req.cardNumber} else {req.accountNumber}

            if ( searchKey == "" ) {
                html = {"<DIV CLASS='message_info'>検索対象外</DIV>"}
            } else {
                html = {query.buildHTMLFromSQL(
                String.format("SELECT * FROM %s WHERE %s = '%s' ",
                dic.L2P(e.logicalTableName),
                dic.L2P(e.primaryKeysString),
                searchKey),callback)}
            }
            dataTables.add(TableResultPair(e.logicalTableName,html))
        }
        model["dataTables"] =  dataTables
        model["appName"] = "Keiyaku Viewer"
        model["req"] = req
        return "paymentEnquiry"
    }
}