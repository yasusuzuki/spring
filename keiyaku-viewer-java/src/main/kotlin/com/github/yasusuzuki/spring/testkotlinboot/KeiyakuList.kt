package com.github.yasusuzuki.spring.testkotlinboot

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.slf4j.Logger
import org.slf4j.LoggerFactory
@Controller
class KeiyakuList(var config: ConfigDef, var dic: Dictionary,var query: DatabaseQuery) {
    val log = LoggerFactory.getLogger(KeiyakuList::class.java)


    data class Request(
        var policyNumber: String = "",
        var policyHolderName: String = "",
        var maxFetchRows: Int = 100,
        var policyType: String = "",
        var agentCode: String = "",
        var amendmentCode: String = "",
        var environment: String = ""
    )
    
    @GetMapping("/keiyakuList","/" )
    fun execute(@ModelAttribute req:Request, model:Model ): String {
        log.info("Process keiyakuList:  req = ${req}")
        
        //環境をスイッチ
        config.setCurrentEnvironment(req.environment)
        req.environment = config.getCurrentEnvironment()

        var sql :String
        if ( config.getCurrentDBServerProduct() == "ACCESS_VIA_ODBC" ) {
            sql = query.getSQL("keiyakuListSQL_Access",req)
        } else {
            sql = query.getSQL("keiyakuListSQL",req)
        }
        //callback
        var callback = mapOf(
            "PREPEND" to fun(_:String?,_:String?,columns:List<String>?,values:Map<String,Any?>?):String{
                //注意：　key と valがブランクになるのでvaluesからpolicyNumberを取得する
                var polNumColumnNumber = 0
                if ( columns == null ){throw Error()}
                if ( values == null ){throw Error()}
                for ( i in columns.indices){
                    if ( columns[i] == "証券＿番号" ) {
                        polNumColumnNumber = i
                        break
                    }
                }
                var targetColumn = columns.get(polNumColumnNumber)
                var policyNumber = values.get(targetColumn) as String
                return "<INPUT TYPE='checkbox'  NAME='policyNumber' value='" + policyNumber + "'>"            },
            "H_PREPEND" to fun(_:String?,_:String?,_:List<String>?,_:Map<String,Any?>?):String{
                var html = "<INPUT TYPE='checkbox' onClick='toggleAllMsg(this, \"policyNumber\");'>&nbsp;"
                html += "<INPUT TYPE='submit' NAME='ACTN' VALUE='契約DB詳細' class='button' onClick='setActionToDataForm(\"/keiyakuEnquiry\");'>"
                return html
            },
            "証券＿番号" to fun(_:String?,value:String?,_:List<String>?,_:Map<String,Any?>?):String{
                return "<A HREF='/keiyakuEnquiry?policyNumber=" + value + "'>" + value + "</A>"
            },
            "団体＿コード" to fun(_:String?,value:String?,_:List<String>?,_:Map<String,Any?>?):String{
                return if ( value != null ) { 
                    "<A HREF='/groupClientEnquiry?groupClientCode=" + value + "'>" + value + "</A>"
                } else {
                    "NULL"
                }
            },
            "代理店＿コード" to fun(_:String?,_:String?,_:List<String>?,values:Map<String,Any?>?):String{
                var agentCode = values?.get("代理店＿コード")
                var agentSubCode = values?.get("代理店サブ＿コード")
                return if ( agentCode != null && agentSubCode != null ) { 
                    "<A HREF='/agentEnquiry?agentCodeSubCode=${agentCode}-${agentSubCode}'>${agentCode}-${agentSubCode}</A>"
                } else if ( agentCode != null ) {
                    "<A HREF='/agentEnquiry?agentCodeSubCode=" + agentCode +  "'>" + agentCode + "</A>"
                } else {
                    "NULL"
                }
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
        model["amendmentCodes"] = dic.CodeMaster.get("契約変更内容＿コード") ?: listOf(Dictionary.CodeValueNamePair("XXX","ディクショナリからコード値取得失敗"))
        return "keiyakuList"
    }
}