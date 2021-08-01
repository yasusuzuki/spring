package com.github.yasusuzuki.spring.testkotlinboot

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import javax.annotation.PostConstruct


@Controller
class GroupClientEnquiry{

    data class Request(
        var groupClientCode: String = "", 
        var verboseMode: Boolean = false
    )
    data class TableResultPair(
        var logicalTableName: String = "",
        var html: () -> String = {""}
    )

    @Autowired
    lateinit var query: DatabaseQuery
    @Autowired
    lateinit var dic: Dictionary

    @GetMapping("/groupClientEnquiry" )
    fun execute(@ModelAttribute req:Request, model:Model ): String {
        println("Process groupClientEnquiry:  req = $req")
        var dataTables = buildDataTables(req)
        model["dataTables"] =  dataTables
        model["appName"] = "Keiyaku Viewer"
        model["req"] = req
        return "groupClientEnquiry"
    }

    lateinit var tableAndPrimaryKeys: List<Dictionary.TableDefinition>
    @PostConstruct
    fun init(){
        tableAndPrimaryKeys = dic.listDBTables("団体")
    }

    fun buildDataTables(req: Request): MutableList<TableResultPair> {
        //callback
        var callback = hashMapOf(
            "VERBOSE_MODE_FLAG" to fun(_:String?,_:String?,_:List<String>?,_:Map<String,Any?>?):String{
                return if (req.verboseMode) {"on"} else {"off"}
            },
        )

        //各テーブルで必要な検索キーをコールバック呼び出し時にprimaryKeys変数に格納する
        var primaryKeys = mutableMapOf("団体＿コード" to mutableSetOf("'${req.groupClientCode}'"))
        var primaryKeysMappingFunc = fun(name:String?,value:String?,_:List<String>?,_:Map<String,Any?>?):String{
            var condition = primaryKeys.get(name)
            if ( condition == null ) {primaryKeys.put(name!!,mutableSetOf("'$value'"))}
            else { condition.add("'$value'") }
            if ( value != null && name == "団体集団集約＿コード"){
                //自動車大口割増引 自動車大口集約＿コード　は　”C”＋「団体集団集約＿コードの下5桁」
                var formattedValue = if ( value.length >= 5 ) { "C" + value.substring(0,5)} else { "C" + value }
                var carCondition = primaryKeys.get("自動車大口集約＿コード")
                if ( carCondition == null ) {primaryKeys.put("自動車大口集約＿コード",mutableSetOf("'$formattedValue'"))}
                else { carCondition.add("'$formattedValue'") }
                //火災大口割引 火災大口集約＿コード は　”F”＋「団体集団集約＿コードの下5桁」
                formattedValue = if ( value.length >= 5 ) { "F" + value.substring(0,5)} else { "F" + value }
                var fireCondition = primaryKeys.get("火災大口集約＿コード")
                if ( fireCondition == null ) {primaryKeys.put("火災大口集約＿コード",mutableSetOf("'$formattedValue'"))}
                else { fireCondition.add("'$formattedValue'") }
            }
            return value?:"NULL"
        }
        tableAndPrimaryKeys.forEach{ e -> callback.put(e.primaryKey, primaryKeysMappingFunc )}

        var dataTables : MutableList<TableResultPair> = mutableListOf()
        for ( e in tableAndPrimaryKeys ) {
            //Thymeleafのテンプレート処理まで実行を遅らせることで、すべての編集処理が終わる前
            //ブラウザの描画が始まるので体感処理速度があがる
            var html: () -> String
            if (req.groupClientCode == "") {
                html = {"<DIV CLASS='message_info'>団体コードなし</DIV>"}
            } else {
                html = {
                    query.buildHTMLFromSQL(
                            String.format("SELECT * FROM %s WHERE %s IN (%s)",
                            dic.L2P(e.logicalTableName),
                            dic.L2P(e.primaryKey),
                            primaryKeys.get(e.primaryKey)?.joinToString(",") ),
                    callback)
                }
            }
            dataTables += TableResultPair(e.logicalTableName,html)
        }
        return dataTables
    }
}