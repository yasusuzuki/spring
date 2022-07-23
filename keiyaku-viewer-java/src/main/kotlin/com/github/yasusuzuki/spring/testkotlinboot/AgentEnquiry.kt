package com.github.yasusuzuki.spring.testkotlinboot

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import javax.annotation.PostConstruct

import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Controller
class AgentEnquiry(var dic: Dictionary, var query: DatabaseQuery){
    val log = LoggerFactory.getLogger(AgentEnquiry::class.java)

    data class Request(
        var agentCodeSubCode: String = "", 
        var verboseMode: Boolean = false
    )
    data class TableResultPair(
        var logicalTableName: String = "",
        var html: () -> String = {""}
    )

    @GetMapping("/agentEnquiry" )
    fun execute(@ModelAttribute req:Request, model:Model ): String {
        log.info("Process agentEnquiry:  req = $req")
        var dataTables = buildDataTables(req)
        model["dataTables"] =  dataTables
        model["appName"] = req.agentCodeSubCode
        model["req"] = req
        return "agentEnquiry"
    }  

    fun buildDataTables(req: Request): MutableList<TableResultPair> {
        var criteria = query.queryCriteria()
        //画面入力された検索キー項目と値を設定する
        // '12345-000,98765-00B'といった複数件が１文字列で表されているので
        //　分解してQueryCriteriaにセットする
        req.agentCodeSubCode
            .split(",")
            .map { it.trim() }
            .map { 
                criteria.putMultiple("代理店＿コード+代理店サブ＿コード",it,"-")
                //代理店コードとサブコードを連結したものが組織グルーピング管理番号
                criteria.put("組織グルーピング管理＿番号",it.replace("-","")) 
                    }
                

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
        dic.listDBTables("代理店").forEach{ 
            e1 -> e1.primaryKeysString.split("+").filter{it !in listOf("代理店＿コード","代理店サブ＿コード")}.forEach{
                e2 -> callback.put(e2, primaryKeysMappingFunc )
            }
        }
        var dataTables : MutableList<TableResultPair> = mutableListOf()
        for ( e in dic.listDBTables("代理店") ) {
            //Thymeleafのテンプレート処理まで実行を遅らせることで、すべての編集処理が終わる前
            //ブラウザの描画が始まるので体感処理速度があがる
            var html: () -> String
            if (req.agentCodeSubCode == "") {
                html = {"<DIV CLASS='message_info'>代理店コードなし</DIV>"}
            } else {
                html = {
                    var sql = "SELECT * FROM %s WHERE %s".format(dic.L2P(e.logicalTableName),criteria.getSQLCondition(e.primaryKeysString))
                    query.buildHTMLFromSQL(sql,callback)
                }
            }
            dataTables += TableResultPair(e.logicalTableName,html)
        }
        return dataTables
    }
}