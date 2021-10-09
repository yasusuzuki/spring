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
import org.slf4j.Logger
import org.slf4j.LoggerFactory
@Component
class DatabaseQuery(var db : Database,var d : Dictionary,var config: ConfigDef) {
    val log = LoggerFactory.getLogger(DatabaseQuery::class.java)

    /**
     * テンプレートファイルもとにSQL文を組み立てる
     * 論理項目名は物理項目名に変換する
     */
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
        //"イベント発生タイムスタンプ＿日付"  to true,
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
        if  ( callback["VERBOSE_MODE_FLAG"]?.let{ it("", "", null,null) } == "on") {
            hideDBSystemColumns = false
            displaySQL = true
            displayPhysicalName = true
        }
        if  ( callback["HIDE_DB_SYSTEM_COLUMNS_FLAG"]?.let{ it("", "", null,null) } == "off") {
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
            val domain = d.lookupDomain(column)
            if (functionColumnHeader!= null) {
                html += "<TH>" + functionColumnHeader(column, "", result.columnList, null) + "</TH>"
            } else if (domain != ""){  
                //column.indexOf("＿コード") != -1 || column.indexOf("_CD") != -1 ){
                html += String.format("<TH><A HREF='/codeMasterEnquiry?domain=%s'>%s</A>%s</TH>",domain,column,physicalColumnName)
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

                val callbackForColumn = callback[column]
                var value1 = Util.rtrim(origVal)
                if (callbackForColumn != null) {
                    value1 = callbackForColumn(column, value1, result.columnList, record) + "</TD>"
                }

                //"＿コード"で終わる列名はコードマスタ対象データ項目とみなし、コード値名称を添えて表示
                if (d.lookupDomain(column) != ""){  
                    //( column.indexOf("＿コード") != -1 )  {
                    val codeName = d.findCodeName(column, value1)
                    if (codeName != "") {
                        value1 = value1 + "<SPAN CLASS='CODE_NAME'>[" + codeName + "]</SPAN>"
                    }
                }
                if (value1 == null) {
                    html += "<TD><SPAN CLASS='NULL'>NULL</SPAN></TD>"
                } else {
                    html += String.format("<TD>%s</TD>",value1)
                }
            }
            html += "</TR>\n"
        }
        html += "</TABLE><BR>\n"

        return html
    }

    /**
     * 検索キー項目と値を集約し、SQLのWHERE句を組み立てるクラスQueryCriteriaのインスタンスを生成
     */
    fun queryCriteria():QueryCriteria {
        return QueryCriteria(d)
    }

    /**
     * 検索キー項目と値を集約し、SQLのWHERE句を組み立てるクラス
     */
    class QueryCriteria (val d:Dictionary ) {
        val log = LoggerFactory.getLogger(QueryCriteria::class.java)

        /* ※当初inner classとして定義し、アウタークラスのDictionaryインスタンスを
        * 参照させていたが、それだとテスト時にDatabaseQueryをモック化すると
        * QueryCriteriaのアウタークラスインスタンスを参照できなくなってしまい、
        * 結局inner classとせず、ネストクラスとしてコンストラクタにDictionaryインスタンスを渡す方式とした
        * inner classではなくネストクラスとすると、外側クラスとの関係性が薄いので外に切り出してもいいのではないか
        */  
        private var pkv = mutableMapOf<String,MutableList<Any?>>()

        /**
         * * 検索キー項目と検索値をセットする
         */
        fun put ( primaryKey:String, value:Any? ){
            //主キーがNULLの可能性がある前提で、SetにNULLも許容しSet<Any?>型とする。
            var valueList = pkv.get(primaryKey)
            if ( valueList == null) {
                valueList = mutableListOf(value)
                pkv.put(primaryKey,valueList)
            } else if ( value == null ) {
                valueList.add(null)            
            } else if ( valueList.first()?.let{ it::class } == value.let{ it::class } ) {
                valueList.add(value)
            } else {
                throw Exception("検索キーとして利用する主キーの型が一貫していません。既に設定済みの型[%s] 追加する値の型[%s] ".
                format(valueList.first()?.let{ it::class }, value.let{ it::class }))
            }
        }

        /**
         * 検索キー項目と検索値をセットする
         * value１つで、ハイフンで区切られた複数の別々のキーを表している場合
         */
        
        fun putMultiple ( primaryKeysString:String, valueString:String, delimiter:String ){
            var pks = primaryKeysString.split("+")
            var values = valueString.split(delimiter)
            require( pks.size >= values.size) {"the count of primaryKeysString should be greater than that of valueString,but was $pks and $values"}

            for ( (i,pk) in pks.withIndex()) {
                put(pk, values.getOrNull(i)) 
            }
        }

        override fun toString():String{
            return pkv.toString()
        }
        
        /**
         * 検索キーとして設定された値をもとにWHERE句を組み立てる
         * 論理項目名は物理項目名に変換する
         */
        fun getSQLCondition(primaryKeysString:String) : String{
            log.info("DEBUG getSQLCondition() pks=$primaryKeysString pkv=$pkv")
            if ( primaryKeysString.isNullOrBlank()  ) { return " 1=1 "}
            var pks = primaryKeysString.split("+")
            //検索キーが１つ指定された場合(pks.size==1)も、複数指定された場合も後述の
            //同じ汎用処理で可能だが、デバッグしやすいので検索キーが1つの場合の単純な処理も残しておく
            if ( pks.size == 1 ) {
                val values = pkv.get(pks.first())?.distinct() ?: return "1 = 2"
                return "%s".format(formatCondition(d.L2P(pks.first()),values))
            } else {
                var conditions = mutableListOf<List<Pair<String,Any?>>>()
                //後でdistinct()できるようにいったんPairクラスのリストを作成する
                val maxLastIndex = pks.mapNotNull { pkv.get(it)?.lastIndex }.maxOrNull()
                log.info("maxLastIndex = $maxLastIndex")
                if ( maxLastIndex == null ) { return "1 = 2"}
                for ( index in 0..maxLastIndex ) {
                    //検索キー１に保持された値の数　＞　検索キー２に保持された値の数　の場合
                    //検索キー１の条件はそのまま生成し、検索キー２は検索条件を"1=1"にする
                    conditions += pks.map { it to pkv.get(it)?.elementAtOrNull(index) }
                }
                //SQLが簡潔になるように条件句の重複を排除しながら、ANDやORでつなぐ
                val conditionsString = conditions.distinct().joinToString(
                    separator = " OR ",
                    transform = { it.joinToString(
                        separator = " AND ",
                        prefix = "(",
                        postfix = ")",
                        transform = { "%s".format(formatCondition(d.L2P(it.first),it.second)) })}    
                    ) 
                return conditionsString
            }
        }


        private fun formatCondition(field:String, value:Any?):String{
            if ( value == null ) {
                return "1 = 1"
            } else if ( value is String && value.contains("%") ) {
                return "%s LIKE %s".format(field,formatSQLLiteral(value))
            } else {
                return "%s = %s".format(field,formatSQLLiteral(value))
            }
        }
        
        private fun formatCondition(field:String, values:List<Any?>):String{
            if ( values.size == 1 ) {
                return formatCondition(field,values[0])
            } else {
                var condition = "$field IN ("
                for ( (i,v) in values.withIndex() ) {
                    condition +=  formatSQLLiteral(v)
                    if ( i != values.lastIndex ){ condition += "," }
                }
                condition += ")"
                return condition
            }
        }

        
        private fun formatSQLLiteral(value:Any?):String{
            //TODO: Date型やタイムスタンプ型もこれでうまくいくか不明。後程調査する
            if ( value == null ) {
                return "NULL"
            } else if ( value is String) {
                return "'$value'"
            } else {
                return value.toString()
            }
        }        

    }
}