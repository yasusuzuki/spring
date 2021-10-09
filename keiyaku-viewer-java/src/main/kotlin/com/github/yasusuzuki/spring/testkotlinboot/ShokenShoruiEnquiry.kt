package com.github.yasusuzuki.spring.testkotlinboot

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ModelAttribute
import javax.annotation.PostConstruct

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.servlet.http.HttpServletResponse
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.net.URLEncoder

@Controller
class ShokenShoruiEnquiry(var config: ConfigDef, var dic: Dictionary,var db:Database, var query: DatabaseQuery) {
    val log = LoggerFactory.getLogger(ShokenShoruiEnquiry::class.java)

    data class Request(
        var searchKeys: String = "",
        var verboseMode: Boolean = false
    )
    data class TableResultPair(
        var logicalTableName: String = "",
        var html: String = ""
    )
    lateinit var tableAndPrimaryKeys: List<Dictionary.TableDefinition>
    @PostConstruct
    fun postConstruct(){
        tableAndPrimaryKeys = dic.listDBTables("証券書類作成")
    }

    @GetMapping("/shokenShoruiEnquiry")
    fun executeEnquiry(@ModelAttribute req:Request, model:Model ): String {
        log.info("Process GET shokenShoruiEnquiry:  req = ${req}")
        //画面描画
        return execute(req,model,false);
    }

    @PostMapping("/shokenShoruiEnquiry")
    fun executeUpdate(@ModelAttribute req:Request, model:Model ): String {
        log.info("Process POST shokenShoruiEnquiry:  req = ${req}")
        //画面描画とDB更新
        return execute(req,model,true);
    }

    fun execute(@ModelAttribute req:Request, model:Model, POST:Boolean ): String {
        var criteria = query.queryCriteria()

        // '12345-001-0001-1,98765-001-0001-1'といった複数案件が１文字列で表されているので
        //　分解してQueryCriteriaにセットする
        req.searchKeys.split(",")
                .map { it.trim() }
                //この画面は削除機能を含むので、あいまい検索は許容しない。
                //4つの検索条件がすべて設定されているか正規表現で確認する
                //TODO:条件を満たしていない場合にエラーログを吐く。
                .filter { "[A-Z0-9 ]+--[0-9 ]+-[0-9 ]".toRegex().matches(it) }
                .map { criteria.putMultiple("証券＿番号+証券番号枝番＿番号+契約計上枝番＿番号+証券書類作成種類＿コード",it,"-") }

        model["dataTables"] = selectDBRecord(req,criteria)
        //DB更新を行う         
        if ( POST == true ) {
            //登録ボタンの非活性などの描画制御用のフラグをセット
            model["hasBeenPosted"] = POST
            //DBを更新する
            model["DBUpdateLog"] = deleteDBRecord(criteria)
            model["dataTablesAfterUpdate"] = selectDBRecord(req,criteria)
        }
 
        model["appName"] = "Keiyaku Viewer"
        model["req"] = req
        return "shokenShoruiEnquiry"
    }


    fun selectDBRecord(req:Request, criteria:DatabaseQuery.QueryCriteria):List<TableResultPair>{
        //callback
        var callback = mapOf(
            "証券＿番号" to fun(_:String?,value:String?,_:List<String>?,_:Map<String,Any?>?):String{
                return "<A HREF='/keiyakuEnquiry?policyNumber=" + value + "'>" + value + "</A>"
            },
            "証券書類帳票データＩＤ＿番号" to fun(_:String?,value:String?,_:List<String>?,values:Map<String,Any?>?):String{
                require(values != null) { "values must be non-null, but was null" }
                var policyNumber = values.get("証券＿番号")
                var keijouEdaban = values.get("契約計上枝番＿番号")
                return if ( value != null ) { 
                    "<A HREF='/shokenShoruiPDFdownload?dataId=${value}&filename=${policyNumber}-${keijouEdaban}'>${value}</A>"
                } else {
                    "NULL"
                }
            },
            "VERBOSE_MODE_FLAG" to fun(_:String?,_:String?,_:List<String>?,_:Map<String,Any?>?):String{
                return if (req.verboseMode) {"on"} else {"off"}
            },
        )

        var dataTables : MutableList<TableResultPair> = mutableListOf()
        for ( e in tableAndPrimaryKeys) {
            var html:String
            if (req.searchKeys == "") {
                html = "<DIV CLASS='message_info'>検索キーなし</DIV>"
            } else {
                var sql = String.format("SELECT * FROM %s WHERE %s",
                                    dic.L2P(e.logicalTableName),
                                    criteria.getSQLCondition(e.primaryKeysString))
                html = query.buildHTMLFromSQL(sql,callback)
            }
            dataTables.add(TableResultPair(e.logicalTableName,html))
        }
        return dataTables
    }



    fun deleteDBRecord(criteria:DatabaseQuery.QueryCriteria):List<String>{
        val con = db.getConnection(config.getCurrentEnvironment())
        val resultMsgs = mutableListOf<String>()
        for ( e in tableAndPrimaryKeys) {
            var sql = String.format("DELETE %s WHERE %s",
                                dic.L2P(e.logicalTableName),
                                criteria.getSQLCondition(e.primaryKeysString))
            val resultMsg = db.execute(con, sql)
            resultMsgs.add( "<H5>${e.logicalTableName} のレコードを削除します</H5><PRE>${sql}</PRE>${resultMsg}"  )
        }
        con.close()
        return resultMsgs
    }
 
}