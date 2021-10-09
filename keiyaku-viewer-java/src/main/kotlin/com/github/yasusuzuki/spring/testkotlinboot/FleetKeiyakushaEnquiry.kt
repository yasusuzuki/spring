package com.github.yasusuzuki.spring.testkotlinboot

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute

import org.slf4j.Logger
import org.slf4j.LoggerFactory
@Controller
class FleetKeiyakushaEnquiry(var config: ConfigDef,var dic: Dictionary,var query: DatabaseQuery) {
    val log = LoggerFactory.getLogger(FleetKeiyakushaEnquiry::class.java)

    //fleetKeiyakushaCodeはリストで来る場合とそうでない場合があるが、SpringMVCがコンマ区切りのStringに変換してセットしてくれる
    data class Request(
        var fleetKeiyakushaCode: String = "", //複数設定される場合は、コンマ区切りになる
        var verboseMode: Boolean = false
    )

    @GetMapping("/fleetKeiyakushaEnquiry" )
    fun execute(@ModelAttribute req:Request, model:Model ): String {
        log.info("Process fleetKeiyakushaEnquiry:  req = $req")
        //画面入力された検索キー項目と値を設定する
        var criteria = query.queryCriteria()

        // 複数案件が１文字列で表されているので
        //　分解してQueryCriteriaにセットする
        req.fleetKeiyakushaCode
            .split(",")
            .map { it.trim() }
            .map { criteria.put("自動車保険契約種目フリート契約者＿コード",it) }

        //callback
        var callback = hashMapOf(
            "VERBOSE_MODE_FLAG" to fun(_:String?,_:String?,_:List<String>?,_:Map<String,Any?>?):String{
                return if (req.verboseMode) {"on"} else {"off"}
            },
        )

        //各テーブルを参照して得られた値のうち、後続のSQLの検索キーとして必要な値を保持するコールバック関数
        var primaryKeysMappingFunc = fun(name:String?,value:String?,_:List<String>?,_:Map<String,Any?>?):String{
            criteria.put(name!!, value)
            return value?:"NULL"
        }

        //設定ファイルで主キーとして定義された項目に対して一律callbackを適用する
        //ただし、画面入力される項目は、入力値以外で検索させないように抑止する
        dic.listDBTables("フリート契約者").forEach{ 
            e1 -> e1.primaryKeysString.split("+").filter{it !in listOf("自動車保険契約種目フリート契約者＿コード")}.forEach{
                e2 -> callback.put(e2, primaryKeysMappingFunc )
            }
        }

        data class TableResultPair(
            var logicalTableName: String = "",
            var html: () -> String = {""}
        )
        var dataTables : MutableList<TableResultPair> = mutableListOf()
        for ( e in dic.listDBTables("フリート契約者") ) {
            //Thymeleafのテンプレート処理まで実行を遅らせることで、すべての編集処理が終わる前
            //ブラウザの描画が始まるので体感処理速度があがる
            var html: () -> String
            //処理前のfleetKeiyakushaCodeは、コンマ区切りだが、シングルクォーテーションが設定されていない
            if (req.fleetKeiyakushaCode == "") {
                html = {"<DIV CLASS='message_info'>フリート契約者コードなし</DIV>"}
            } else if ( dic.L2P(e.logicalTableName) == "NO_PHYSICAL_TABLE" ) {
                html = {"<DIV CLASS='message_info'>物理テーブルなし</DIV>"}
            } else {
                html = {
                    var sql = String.format("SELECT * FROM %s WHERE %s",
                                    dic.L2P(e.logicalTableName),
                                    criteria.getSQLCondition(e.primaryKeysString))
                    query.buildHTMLFromSQL(sql,callback)
                }
            }
            dataTables.add(TableResultPair(e.logicalTableName,html))
        }
        model["dataTables"] =  dataTables
        model["appName"] = req.fleetKeiyakushaCode
        model["req"] = req
        return "fleetKeiyakushaEnquiry"
    }
}