package com.github.yasusuzuki.spring.testkotlinboot

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import javax.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Controller
class TeianEnquiry(var dic: Dictionary, var query: DatabaseQuery) {
    val log = LoggerFactory.getLogger(TeianEnquiry::class.java)

    //ankenNumberはListではなくString。複数指定された場合、SpringMVCがコンマ区切りのStringに変換してセットしてくれる
    data class Request(
        var ankenNumber: String = "", //複数設定される場合は、コンマ区切りになる
        var latestDataVersion: Boolean = false,
        var verboseMode: Boolean = false,
        var omitBlankFieldMode: Boolean = false,
        var omitEqualFieldMode: Boolean = false        
    )
    data class TableResultPair(
        var logicalTableName: String = "",
        var html: () -> String = {""}
    )

    lateinit var tableAndPrimaryKeys: List<Dictionary.TableDefinition>
    @PostConstruct
    fun postConstruct(){
        tableAndPrimaryKeys = dic.listDBTables("提案")
    }

    @GetMapping("/teianEnquiry" )
    fun execute(@ModelAttribute req:Request, model:Model ): String {
        log.info("Process teianEnquiry:  req = $req")
        var dataTables = buildDataTables(req)

        model["dataTables"] =  dataTables
        model["appName"] = req.ankenNumber
        model["req"] = req
        return "teianEnquiry"
    }

    fun buildDataTables(req:Request): MutableList<TableResultPair> {
        //画面入力された検索キー項目と値を設定する
        var criteria = query.queryCriteria()

        // '12345-001-0001-1,98765-001-0001-1'といった複数案件が１文字列で表されているので
        //　分解してQueryCriteriaにセットする
        req.ankenNumber
            .split(",")
            .map { it.trim() }
            // 提案案件＿番号 の最後の3桁を64で割った余り＋１を　"契約管理区分キー＿英数カナ"
            .map { var x = "" + it.split('-').getOrElse(0) {'%'}; 
                    log.info("契約管理区分キー＿英数カナ" + x.substring(x.length-3).toInt().mod(64).toString().padStart(3,'0'))
                   x.substring(x.length-3).toInt().mod(64).plus(1).toString().padStart(3,'0') + '-' + it }
            .map { criteria.putMultiple("契約管理区分キー＿英数カナ+提案案件＿番号+提案案件番号枝番＿番号+提案連続＿番号+提案設計データバージョン番号＿数",it,"-") }

        //callback
        var callback = mapOf(
            "VERBOSE_MODE_FLAG" to fun(_:String?,_:String?,_:List<String>?,_:Map<String,Any?>?):String{
                return if (req.verboseMode) {"on"} else {"off"}
            },
            "OMIT_BLANK_FIELD_MODE" to fun(_:String?,_:String?,_:List<String>?,_:Map<String,Any?>?):String{
                return if (req.omitBlankFieldMode) {"on"} else {"off"}
            },
            "OMIT_EQUAL_FIELD_MODE" to fun(_:String?,_:String?,_:List<String>?,_:Map<String,Any?>?):String{
                return if (req.omitEqualFieldMode) {"on"} else {"off"}
            },
            "取引クレジットカード＿番号" to fun(_:String?,value:String?,_:List<String>?,_:Map<String,Any?>?):String{
                return if ( value != null ) { 
                    "<A HREF='/paymentEnquiry?cardNumber=" + value + "'>" + value + "</A>"
                } else {
                    "NULL"
                }
            },
            "取引金融機関口座＿番号" to fun(_:String?,value:String?,_:List<String>?,_:Map<String,Any?>?):String{
                return if ( value != null ) { 
                    "<A HREF='/paymentEnquiry?accountNumber=" + value + "'>" + value + "</A>"
                } else {
                    "NULL"
                }
            },
        )

        var dataTables : MutableList<TableResultPair> = mutableListOf()
        for ( e in tableAndPrimaryKeys) {
            //Thymeleafのテンプレート処理まで実行を遅らせることで、すべての編集処理が終わる前
            //ブラウザの描画が始まるので体感処理速度があがる
            var html: () -> String
            if (req.ankenNumber == "") {
                html = {"<DIV CLASS='message_info'>案件番号なし</DIV>"}
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
        return dataTables
   }
}