package com.github.yasusuzuki.spring.testkotlinboot

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import javax.annotation.PostConstruct


@Controller
class GroupClientEnquiry(var query: DatabaseQuery, var dic: Dictionary){

    data class Request(
        var groupClientCode: String = "", 
        var verboseMode: Boolean = false
    )
    data class TableResultPair(
        var logicalTableName: String = "",
        var html: () -> String = {""}
    )

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
    fun postConstruct(){
        tableAndPrimaryKeys = dic.listDBTables("団体")
    }

    fun buildDataTables(req: Request): MutableList<TableResultPair> {
        var criteria = query.queryCriteria()
        //画面入力された検索キー項目と値を設定する
        criteria.put("団体＿コード", req.groupClientCode)
        //callback
        var callback = hashMapOf(
            "VERBOSE_MODE_FLAG" to fun(_:String?,_:String?,_:List<String>?,_:Map<String,Any?>?):String{
                return if (req.verboseMode) {"on"} else {"off"}
            },
        )
        //各テーブルを参照して得られた値のうち、後続のSQLの検索キーとして必要な値を保持するコールバック関数
        //いくつかの検索キーは、他のSQLで取得した値から導出する必要がある
        var primaryKeysMappingFunc = fun(name:String?,value:String?,_:List<String>?,_:Map<String,Any?>?):String{
            criteria.put(name!!, value)
            if ( value != null && name == dic.P2L("IGR_SDN_CSD_CD")){ //"団体集団集約＿コード"
                criteria.put(dic.P2L("CAR_LGO_CSD_CD"), "C" + value.takeLast(5))  //自動車大口集約＿コード
                criteria.put(dic.P2L("FIR_LGO_CSD_CD"), "F" + value.takeLast(5))  //火災大口集約＿コード
            } else if ( value != null && name == dic.P2L("CCO_CSD_CD")) { //集金契約書集約＿コード
                criteria.put(dic.P2L("DGR_CSD_CD"), "L" + value.takeLast(5)) //債務者集団扱集約＿コード
            }
            return value?:"NULL"
        }

        //設定ファイルで主キーとして定義された項目に対して一律callbackを適用する
        //ただし、画面入力される項目は、入力値以外で検索させないように抑止する
        tableAndPrimaryKeys.forEach{ 
            e1 -> e1.primaryKeysString.split("+").filter{it !in listOf("団体＿コード")}.forEach{
                e2 -> callback.put(e2, primaryKeysMappingFunc )
            }
        }

        var dataTables : MutableList<TableResultPair> = mutableListOf()
        for ( e in tableAndPrimaryKeys ) {
            //Thymeleafのテンプレート処理まで実行を遅らせることで、すべての編集処理が終わる前
            //ブラウザの描画が始まるので体感処理速度があがる
            var html: () -> String
            if (req.groupClientCode == "") {
                html = {"<DIV CLASS='message_info'>団体コードなし</DIV>"}
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