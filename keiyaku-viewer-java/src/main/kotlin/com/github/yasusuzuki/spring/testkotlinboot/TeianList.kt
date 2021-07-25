package com.github.yasusuzuki.spring.testkotlinboot

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute

@Controller
class TeianList {
    data class Request(
        var ankenNumber: String = "",
        var policyHolderName: String = "",
        var maxFetchRows: Int = 100,
        var policyType: String = "",
        var agentCode: String = "",
        var environment: String = ""
    )
    
    @Autowired
    lateinit var config: ConfigDef
    @Autowired
    lateinit var query: DatabaseQuery
    
    @GetMapping("/teianList" )
    fun execute(@ModelAttribute req:Request, model:Model ): String {
        println("Process teianList:  req = ${req}")

        //環境をスイッチ
        config.setCurrentEnvironment(req.environment)
        req.environment = config.getCurrentEnvironment()


        var sql = query.getSQL("teianListSQL",req)

        //callback
        var callback = mapOf(
            "PREPEND" to fun(_:String?,_:String?,columns:List<String>?,values:Map<String,Any?>?):String{
                //注意：　key と valがブランクになるのでvaluesからpolicyNumberを取得する
                var ankenNumColumnNumber = 0
                if ( columns == null ){throw Error()}
                if ( values == null ){throw Error()}
                for ( i in columns.indices){
                    if ( columns[i] == "提案案件＿番号" ) {
                        ankenNumColumnNumber = i
                        break
                    }
                }
                var targetColumn = columns.get(ankenNumColumnNumber)
                var ankenNumber = values.get(targetColumn) as String
                return "<INPUT TYPE='checkbox'  NAME='ankenNumber' value='" + ankenNumber + "'>"            },
            "H_PREPEND" to fun(_:String?,_:String?,_:List<String>?,_:Map<String,Any?>?):String{
                var html = "<INPUT TYPE='checkbox' onClick='toggleAllMsg(this, \"ankenNumber\");'>&nbsp;"
                html += "<INPUT TYPE='submit' NAME='ACTN' VALUE='提案DB詳細' class='button' onClick='setActionToDataForm(\"/teianEnquiry\");'>"
                return html
            },
            "証券＿番号" to fun(_:String?,value:String?,_:List<String>?,_:Map<String,Any?>?):String{
                return "<A HREF='/keiyakuEnquiry?policyNumber=" + value + "'>" + value + "</A>"
            },
            "提案案件＿番号" to fun(_:String?,value:String?,_:List<String>?,_:Map<String,Any?>?):String{
                return "<A HREF='/teianEnquiry?ankenNumber=" + value + "'>" + value + "</A>"
            },
            "HIDE_DB_SYSTEM_COLUMNS_FLAG" to fun(_:String?,_:String?,_:List<String>?,_:Map<String,Any?>?):String{
                return "off"
            },

        )
        model["dataTable"] = query.buildHTMLFromSQL(sql,callback)

        model["appName"] = "Keiyaku Viewer"
        model["environmentList"] = config.listEnvironment()
        model["req"] = req
        model["SQL"] = sql
        return "teianList"
    }
}