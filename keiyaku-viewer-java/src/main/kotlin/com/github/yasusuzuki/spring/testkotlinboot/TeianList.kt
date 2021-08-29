package com.github.yasusuzuki.spring.testkotlinboot

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.slf4j.Logger
import org.slf4j.LoggerFactory
@Controller
class TeianList (var config: ConfigDef, var dic: Dictionary, var query: DatabaseQuery) {
    val log = LoggerFactory.getLogger(TeianList::class.java)

    data class Request(
        var ankenNumber: String = "",
        var policyHolderName: String = "",
        var maxFetchRows: Int = 100,
        var policyType: String = "",
        var agentCode: String = "",
        var policyNumber: String = "",
        var amendmentCode: String = "",
        var userId: String = "",
        var environment: String = ""

    )
    

    
    @GetMapping("/teianList" )
    fun execute(@ModelAttribute req:Request, model:Model ): String {
        log.info("Process teianList:  req = ${req}")

        //環境をスイッチ
        config.setCurrentEnvironment(req.environment)
        req.environment = config.getCurrentEnvironment()

        var sql = query.getSQL("teianListSQL",req)

        //callback
        var callback = mapOf(
            "PREPEND" to fun(_:String?,_:String?,columns:List<String>?,values:Map<String,Any?>?):String{
                //注意：　key と valがブランクになるのでvaluesからpolicyNumberを取得する
                var ankenNumber = values?.get("提案案件＿番号")
                var ankenEdaban = values?.get("提案案件番号枝番＿番号")
                var ankenRenzoku = values?.get("提案連続＿番号")
                var ankenDataVersion = values?.get("提案設計データバージョン番号＿数")
                return "<INPUT TYPE='checkbox'  NAME='ankenNumber' value='$ankenNumber-$ankenEdaban-$ankenRenzoku-$ankenDataVersion'>"            },
            "H_PREPEND" to fun(_:String?,_:String?,_:List<String>?,_:Map<String,Any?>?):String{
                var html = "<INPUT TYPE='checkbox' onClick='toggleAllMsg(this, \"ankenNumber\");'>&nbsp;"
                html += "<INPUT TYPE='submit' NAME='ACTN' VALUE='提案DB詳細' class='button' onClick='setActionToDataForm(\"/teianEnquiry\");'>"
                return html
            },
            "証券＿番号" to fun(_:String?,value:String?,_:List<String>?,_:Map<String,Any?>?):String{
                return "<A HREF='/keiyakuEnquiry?policyNumber=" + value + "'>" + value + "</A>"
            },
            "提案案件＿番号" to fun(_:String?,_:String?,_:List<String>?,values:Map<String,Any?>?):String{
                var ankenNumber = values?.get("提案案件＿番号")
                var ankenEdaban = values?.get("提案案件番号枝番＿番号")
                var ankenRenzoku = values?.get("提案連続＿番号")
                var ankenDataVersion = values?.get("提案設計データバージョン番号＿数")
                return "<A HREF='/teianEnquiry?ankenNumber=$ankenNumber-$ankenEdaban-$ankenRenzoku-$ankenDataVersion'>$ankenNumber-$ankenEdaban</A>"
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
     
        return "teianList"
    }
}