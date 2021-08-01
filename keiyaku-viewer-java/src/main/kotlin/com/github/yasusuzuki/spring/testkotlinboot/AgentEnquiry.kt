package com.github.yasusuzuki.spring.testkotlinboot

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import javax.annotation.PostConstruct


@Controller
class AgentEnquiry{

    data class Request(
        var agentCodeSubCode: String = "", 
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

    @GetMapping("/agentEnquiry" )
    fun execute(@ModelAttribute req:Request, model:Model ): String {
        println("Process agentEnquiry:  req = $req")
        var dataTables = buildDataTables(req)
        model["dataTables"] =  dataTables
        model["appName"] = "Keiyaku Viewer"
        model["req"] = req
        return "agentEnquiry"
    }

    lateinit var tableAndPrimaryKeys: List<Dictionary.TableDefinition>
    @PostConstruct
    fun init(){
        tableAndPrimaryKeys = dic.listDBTables("代理店")
    }

    fun buildDataTables(req: Request): MutableList<TableResultPair> {
        //先頭５桁をagentCodeに設定しagentSubCodeは未設定
        var agentCode = req.agentCodeSubCode.take(5)
        var agentSubCode = "%"   //もし抽出されない場合のためのワイルドカードを設定
        if ( req.agentCodeSubCode.length > 5 ) {
            agentSubCode = req.agentCodeSubCode.slice(5..req.agentCodeSubCode.lastIndex)
        }

        //callback
        var callback = hashMapOf(
            "VERBOSE_MODE_FLAG" to fun(_:String?,_:String?,_:List<String>?,_:Map<String,Any?>?):String{
                return if (req.verboseMode) {"on"} else {"off"}
            },
        )
        var dataTables : MutableList<TableResultPair> = mutableListOf()
        for ( e in tableAndPrimaryKeys ) {
            //Thymeleafのテンプレート処理まで実行を遅らせることで、すべての編集処理が終わる前
            //ブラウザの描画が始まるので体感処理速度があがる
            var html: () -> String
            if (req.agentCodeSubCode == "") {
                html = {"<DIV CLASS='message_info'>団体コードなし</DIV>"}
            } else {
                html = {
                    query.buildHTMLFromSQL(
                            String.format("SELECT * FROM %s WHERE %s LIKE '%s' AND %s LIKE '%s'",
                            dic.L2P(e.logicalTableName),
                            dic.L2P("代理店＿コード"),
                            agentCode,
                            dic.L2P("代理店サブ＿コード"),
                            agentSubCode,
                            ),
                    callback)
                }
            }
            dataTables += TableResultPair(e.logicalTableName,html)
        }
        return dataTables
    }
}