package com.github.yasusuzuki.spring.testkotlinboot

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.ui.set
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import org.thymeleaf.templateresolver.FileTemplateResolver
import java.sql.Connection

@Component
class DatabaseQuery {

    @Autowired
    lateinit var db : Database
    @Autowired
    lateinit var d : Dictionary
    @Autowired
    lateinit var config: ConfigDef
    @Autowired
    lateinit var json: Json

    fun getSQL(templateName: String, req: Any): String {
        val resolver = FileTemplateResolver()
        resolver.prefix = ".\\templates\\"
        resolver.suffix = ".txt"
        resolver.templateMode = TemplateMode.TEXT
        resolver.characterEncoding = "UTF-8"
        val engine = TemplateEngine()
        engine.setTemplateResolver(resolver)
        val context = Context()
        context.setVariable("req",req)
        var sql = engine.process(templateName, context)
        if (  config.getCurrentDBServerProduct() == "ACCESS_VIA_ODBC" ) {
            return sql
        } else {
            return d.L2P_SQL(sql)
        }
    }

    var DBSystemColumns : Map<String,Boolean> = mapOf(
        "証券番号枝番＿番号" to          true,
        "契約管理区分キー＿英数カナ"  to    true,
        "契約管理レコードＩＤ＿英数カナ"  to  true,
        "有効開始年月日枝番＿番号"  to     true,
        "遡及連続＿番号"  to          true,
        "契約計上処理回数＿数"  to       true,
        "取扱年月日＿日付"  to         true,
        "繰越データ抽出キー＿英数カナ"  to   true,
        "データステータス区分＿コード"  to   true,
        "排他制御バージョン番号＿数"  to    true,
        "ビジネスタスクＩＤ＿英数カナ"  to   true,
        "イベント発生タイムスタンプ＿日付"  to true,
        "論理削除＿コード"  to         true,
        "データ登録タイムスタンプ＿日付"  to  true,
        "データ登録ユーザーＩＤ＿英数カナ"  to true,
        "データ登録プログラム＿英数カナ"  to  true,
        "データ更新タイムスタンプ＿日付"  to  true,
        "データ更新ユーザーＩＤ＿英数カナ"  to true,
        "データ更新プログラム＿英数カナ"  to  true,
        "リレーション用契約計上枝番＿番号"  to true,
    )

    fun  buildHTMLFromSQL(sql: String, callback: Map<String,(String?,String?,List<String>?,Map<String,Any?>?)->String >): String {
        val conn = db.getConnection(config.getCurrentEnvironment())
        var result = db.queryForList(conn,sql)
        //E0002　コネクションプールを利用しているため、ここでクローズしても実際のDB接続はクローズされない
        //そのため、buildHTMLFromSQLの呼出しの都度コネクションをクローズしても性能影響はない。
        conn.close()
        return buildHTML(result,sql,callback)
    }

 
    fun  buildHTML(result: Database.SQLResultSet,
                   sql: String, 
                   callback: Map<String,(String?,String?,List<String>?,Map<String,Any?>?)->String >): String {
        var html = ""
        var hideDBSystemColumns = true
        var displaySQL = false
        var displayPhysicalName = false
        val functionVerboseModeFlag = callback["VERBOSE_MODE_FLAG"]
        val functionHideDBSystemColumnsFlag = callback["HIDE_DB_SYSTEM_COLUMNS_FLAG"]
        if  (functionVerboseModeFlag != null && functionVerboseModeFlag("", "", null,null) == "on") {
            hideDBSystemColumns = false
            displaySQL = true
            displayPhysicalName = true
        }
        if  (functionHideDBSystemColumnsFlag != null && functionHideDBSystemColumnsFlag("", "", null,null) == "on") {
            hideDBSystemColumns = false
        }
        if (displaySQL) {
            html += String.format("<PRE>%s</PRE>", sql)
        }
        html += "<font color='grey'>" + result.recordList.size + " rows fetched</font>"
        html += "<TABLE CELLSPACING=0 CLASS='DataTable'>\n"

        html += "<TR>"
        //"H_PREPEND"は、DBrecordに存在していない列を、表の列として追加するためのもの
        //ヘッダーとデータ部で列数がずれるといけないので、"H_PREPEND"が使用されていなくても
        //"PREPEND"が使用されていれば、ヘッダーの列を追加してあげる。

        val functionPrepend = callback["PREPEND"]
        val functionHeaderPrepend = callback["H_PREPEND"]

        if (functionPrepend != null) {
            if (functionHeaderPrepend != null) {
                html += "<TH>" + functionHeaderPrepend("", "", result.columnList, null) + "</TH>"
            } else {
                html += "<TH></TH>"
            }
        }

        //表のヘッダーを出力していく。ヘッダーは、SQL結果のrows.Columns()を源泉とする。
        for (column in result.columnList) {
            if ( hideDBSystemColumns && DBSystemColumns[column] == true ) {
                continue
            }
            var physicalColumnName = ""
            if (displayPhysicalName) {
                physicalColumnName = String.format("<BR><SPAN CLASS='PHYSICAL_COLUMN_NAME'>[%s]</SPAN>", d.L2P(column))
            }
            val functionColumnHeader = callback["H_"+column]
            if (functionColumnHeader!= null) {
                html += "<TH>" + functionColumnHeader(column, "", result.columnList, null) + "</TH>"
            } else if (column.indexOf("＿コード") != -1 || column.indexOf("_CD") != -1 ){
                val domain = d.lookupDomain(column)
                if (domain != "") {
                    html += String.format("<TH><A HREF='/codeMasterEnquiry?domain=%s'>%s</A>%s</TH>",domain,column,physicalColumnName)
                } else {
                    html += String.format("<TH>%s%s</TH>",column,physicalColumnName)
                }
            } else {
                if ( DBSystemColumns[column] == true ) {
                    html += String.format("<TH CLASS='SYSTEM_COLUMN'>%s%s</TH>",column,physicalColumnName)
                } else {
                    html += String.format("<TH>%s%s</TH>",column,physicalColumnName)
                }
            }
        }
        html += "</TR>\n"


        //表のデータ部を出力していく。
        for (record in result.recordList) {
            html += "<TR>"
            //"PREPEND"は、DBrecordに存在していない列を、表の列として追加するためのもの
            if ( functionPrepend!= null) {
                html += "<TD>" + functionPrepend("", "", result.columnList, record) + "</TD>"
            }
            //DBRecordに存在する列を、順番に表のセルとして出力していく
            for ( column in result.columnList ) {
                if ( hideDBSystemColumns && DBSystemColumns[column] == true) {
                    continue
                }
                //logger.debug("record = ${record}, column = ${column}")
                val origVal : Any? = record[column]  //DBから取得した生の値は文字型、数値型、日付型などさまざま
                var value = Util.rtrim(origVal)
                //"＿コード"で終わる列名はコードマスタ対象データ項目とみなし、コード値名称を添えて表示
                if ( column.indexOf("＿コード") != -1 )  {
                    val codeName = d.findCodeName(column, value)
                    if (codeName != "") {
                        value = value + "<SPAN CLASS='CODE_NAME'>[" + codeName + "]</SPAN>"
                    }
                }
                val callbackForColumn = callback[column]
                if (callbackForColumn != null) {
                    html += "<TD>" + callbackForColumn(column, value, result.columnList, record) + "</TD>"
                } else {
                    if (record[column] == null) {
                        html += "<TD><SPAN CLASS='NULL'>NULL</SPAN></TD>"
                    } else {
                        html += String.format("<TD>%s</TD>",value)
                    }
            }
        }
            html += "</TR>\n"
        }
        html += "</TABLE><BR>\n"

        return html
    }
}