package com.github.yasusuzuki.spring.testkotlinboot

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestParam

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.servlet.http.HttpServletResponse
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.net.URLEncoder

@Controller
class ShokenShoruiList(var config: ConfigDef, var dic: Dictionary,var db:Database, var query: DatabaseQuery) {
    val log = LoggerFactory.getLogger(ShokenShoruiList::class.java)

    data class Request(
        var policyNumber: String = "",
        var processDateFrom: String = "",
        var processDateTo: String = "",
        var maxFetchRows: Int = 100,
        var verboseMode: Boolean = false
    )
    
    @GetMapping("/shokenShoruiList")
    fun execute(@ModelAttribute req:Request, model:Model ): String {
        log.info("Process shokenShoruiList:  req = ${req}")

        var sql = "SELECT * FROM 証券書類作成 WHERE 1 = 1 "
        if ( req.policyNumber != "" ){
            sql += " AND 証券＿番号 LIKE '%${req.policyNumber}%' "
        }
        if ( req.processDateFrom != "" && req.processDateTo != ""){
            sql += " AND イベント発生タイムスタンプ＿日付 BETWEEN '${req.processDateFrom} 00:00:00' AND '${req.processDateTo} 23:59:59.999' "
        } else if ( req.processDateFrom != "" ) {
            sql += " AND イベント発生タイムスタンプ＿日付 >= '${req.processDateFrom} 00:00:00' "
        } else if ( req.processDateTo != "" ) {
            sql += " AND イベント発生タイムスタンプ＿日付 <= '${req.processDateTo} 23:59:59.999' "
        }
        sql += " ORDER BY イベント発生タイムスタンプ＿日付 DESC "
        sql += " FETCH FIRST ${req.maxFetchRows} ROWS ONLY "

        //callback
        var callback = mapOf(
            "証券＿番号" to fun(_:String?,value:String?,_:List<String>?,_:Map<String,Any?>?):String{
                return "<A HREF='/keiyakuEnquiry?policyNumber=" + value + "'>" + value + "</A>"
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
            "証券書類帳票データＩＤ＿番号" to fun(_:String?,value:String?,_:List<String>?,values:Map<String,Any?>?):String{
                var policyNumber = values?.get("証券＿番号")
                var transactionNumber = values?.get("契約計上枝番＿番号")
                return if ( value != null ) { 
                    "<A HREF='/shokenShoruiPDFdownload?dataId=${value}&filename=${policyNumber}-${transactionNumber}'>${value}</A>"
                } else {
                    "NULL"
                }
            },
            "VERBOSE_MODE_FLAG" to fun(_:String?,_:String?,_:List<String>?,_:Map<String,Any?>?):String{
                return if (req.verboseMode) {"on"} else {"off"}
            },
            "PREPEND" to fun(_:String?,_:String?,_:List<String>?,values:Map<String,Any?>?):String{
                //注意：　key と valがブランクになるのでvaluesから取得する
                require( values != null ) { "values must be non-null, but was null" }
                var searchKeys = "%s-%s-%s-%s".format(values.get("証券＿番号"),values.get("証券番号枝番＿番号"),values.get("契約計上枝番＿番号"),values.get("証券書類作成種類＿コード"))
                var html = "<INPUT TYPE='checkbox' NAME='searchKeys' value='${searchKeys}'>"
                html += "<A HREF='/shokenShoruiEnquiry?searchKeys=${searchKeys}'>${searchKeys}</A>"
                return html 
            },
            "H_PREPEND" to fun(_:String?,_:String?,_:List<String>?,_:Map<String,Any?>?):String{
                var html = "<INPUT TYPE='checkbox' onClick='toggleAllMsg(this, \"searchKeys\");'>&nbsp"
                html    += "<INPUT TYPE='submit' NAME='ACTN' VALUE='証券書類作成DB詳細' class='button' onClick='setActionToDataForm(\"/shokenShoruiEnquiry\");'>"
                return html
            },
        )
        model["dataTable"] = query.buildHTMLFromSQL(dic.L2P_SQL(sql),callback)
        model["appName"] = "Keiyaku Viewer"
        model["req"] = req
        model["SQL"] = dic.L2P_SQL(sql)
        return "shokenShoruiList"
    }

    /**
     * PDFをダウンロード
     * TODO: SpringのInputStreamResourceを返却する方法を検討する
     */
    @GetMapping("/shokenShoruiPDFdownload")
    fun pdfDownload(@RequestParam("dataId") dataId:String, 
                    @RequestParam("filename") filename:String,
                    response:HttpServletResponse) {
        response.setContentType("application/octet-stream");
        response.setHeader("Cache-Control", "private");
        response.setHeader("Pragma", "");
        response.setHeader("Content-Disposition","attachment;filename=\"${filename}.pdf\"");

        val con = db.getConnection(config.getCurrentEnvironment())
        val su = con.createStatement()
        val rs = su.executeQuery("SELECT DATA_FILE FROM PCM.T_PDF_DATA WHERE DATA_ID = $dataId");
        val out =  response.getOutputStream()

        while(rs.next()) {
            //参考：
            //https://github.com/purin-it/java/blob/master/spring-boot-pdf-preview/demo/src/main/java/com/example/demo/DemoController.java
            //https://www.purin-it.com/spring-boot-upload-download-2
            //https://hirauchi-genta.com/kotlin-bytearrayoutputstream/
            val blob = rs.getBlob(1)
            val ins = blob.binaryStream
            val buffer = ByteArray(1024)
            var readSize = ins.read(buffer)
            while ( readSize != -1 ) {
                out.write(buffer,0,readSize)
                readSize = ins.read(buffer)
            }
            blob.free()
            out.flush()
        }
        out.close()
        su.close()
        con.close()
    }
 
}