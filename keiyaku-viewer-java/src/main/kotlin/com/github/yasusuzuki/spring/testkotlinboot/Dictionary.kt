package com.github.yasusuzuki.spring.testkotlinboot

import java.nio.file.Files
import java.nio.file.Paths
import javax.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Component
class Dictionary (val config: ConfigDef) {
    val log = LoggerFactory.getLogger(Dictionary::class.java)

    @PostConstruct
    fun postConstruct() {
        initCodeMaster()
        initDictionary()
    }
    // コードマスタは、ドメイン名 > (コード値名、コード値名称)のペアのマップ
    // コードマスタは、順番に表示したいので、LinkedHashMap
    var CodeMaster: LinkedHashMap<String, MutableList<CodeValueNamePair>> = linkedMapOf()

    data class CodeValueNamePair(var codeValue: String = "", var codeName: String = "")

    fun initCodeMaster() {
        for ( fileName in config.codeMasterFilePath) {
            var file = Paths.get(fileName)
            if (!Files.exists(file) || Files.isDirectory(file)) {
                throw Error("コードマスタファイル[${file}]が見つかりません")
            } else {
                log.info("コードマスタファイル[${file}]が見つかりました")
            }

            var lines = Files.readAllLines(file)
            log.info("    %s行".format(lines.count()))
            for (line in lines) {
                var words = line.split(",")
                if (words.size < 6) continue // ときどき、2行目に折り返してしまっているデータがある。この2行目は無視する。
                if (CodeMaster.containsKey(words[0])) {
                    CodeMaster.get(words[0])!!.add(CodeValueNamePair(words[1], words[5]))
                } else {
                    CodeMaster[words[0]] = mutableListOf(CodeValueNamePair(words[1], words[5]))
                }
            }
        }
    }

    data class DomainPhysicalNamePair(val domain: String = "", val javaName: String = "", val physicalName: String = "")
    data class DomainLogicalNamePair(val domain: String = "", val logicalName: String = "")
    data class TableDefinition(val dbName: String = "", val logicalTableName: String = "", val primaryKeysString: String = "")
    var L2PDBTables:MutableMap<String,String> = mutableMapOf()
    var L2PDictionary:MutableMap<String,DomainPhysicalNamePair> = mutableMapOf()
    var DBTables:MutableList<TableDefinition> = mutableListOf()
    var P2LDBTables:MutableMap<String,String> = mutableMapOf()
    var P2LDictionary:MutableMap<String,DomainLogicalNamePair> = mutableMapOf()

    
    fun initDictionary() {
        var file = Paths.get(config.dBTableListFilePath)
        if (!Files.exists(file) || Files.isDirectory(file)) {
            throw Error("DBテーブル一覧ファイル[${file}]が見つかりません")
        } else {
            log.info("DBテーブル一覧ファイル[${file}]が見つかりました")
        }
        var lines = Files.readAllLines(file)
        for (line in lines) {
            var words = line.split(",")
            if (words.size < 2) continue    
            // 0:DB名、1:テーブル論理名、2:テーブル物理名、3:検索に用いる主キー
            L2PDBTables[words[1]] = words[2]
            DBTables += TableDefinition(words[0],words[1],words[3])
        }

        // データディクショナリのロード
        for ( fileName in config.dataDictionaryFilePath) {
            file = Paths.get(fileName)
            if (!Files.exists(file) || Files.isDirectory(file)) {
                throw Error("データディクショナリファイル[${file}]が見つかりません")
            } else {
                log.info("データディクショナリファイル[${file}]が見つかりました")
            }
            lines = Files.readAllLines(file)
            for (line in lines) {
                var words = line.split(",")
                if (words.size < 4) continue
                // words[0]がデータ項目論理名、words[1]がドメイン名、words[2]が項目物理名（Java）,words[3]がデータ項目物理名
                L2PDictionary[words[0]] = DomainPhysicalNamePair(words[1], words[2], words[3])
            }
            // 検索の高速化のため、ハッシュ―キーとバリューを入れ替えたマップを作成する
            for ((k, v) in L2PDBTables) {
                P2LDBTables[v] = k
            }
            for ((k, v) in L2PDictionary) {
                P2LDictionary[v.physicalName] = DomainLogicalNamePair(v.domain, k)
                //E0001 Java物理目もディクショナリ登録
                P2LDictionary[v.javaName] = DomainLogicalNamePair(v.domain, k)

            }
        }
    }

    fun listDBTables(dbName: String):List<TableDefinition>{
        return DBTables.filter{e -> e.dbName == dbName}
    }

    /** データ項目論理名からドメイン名をひく */
    fun lookupDomain(fieldName: String): String {
        val domain = L2PDictionary[fieldName]?.domain ?: ""
        var codeList = CodeMaster[domain]
        //コードマスタに存在しないドメインは、ブランクを返却する
        return if ( codeList != null ){
            domain
        } else {
            ""
        }
    }

    /** データ項目論理名とコード値から、コード値名称をひく */
    fun findCodeName(fieldName: String, codeValue: String?): String {
        var domain = lookupDomain(fieldName)
        if (domain == "") {
            return ""
        }
        var codeList = CodeMaster[domain] ?: return ""

        // TODO: 性能がでないようであれば、コードマスタのCodeとValueのペアはリストではなくマップにする
        for (v in codeList) {
            if (v.codeValue == codeValue?.replace("^0+".toRegex(), "")) {
                return v.codeName
            }
        }
        return "" // 該当するドメインとコード値の組み合わせに一致するものがなかった
    }

    /** データ項目論理名から、データ項目物理名をひく */
    fun L2P(logicalName: String): String {
        /*if (config.getCurrentDBServerProduct() == "ACCESS_VIA_ODBC" ||
                config.getCurrentDBServerProduct() == "SQLITE") {
            return logicalName
        }*/
    
        //  データ項目を優先して検索、なければテーブル一覧を検索
        // 該当がなければ、論理名→物理名変換せずに論理名をそのまま返す
        return L2PDictionary[logicalName]?.physicalName ?: L2PDBTables[logicalName] ?: logicalName
    }

    /** データ項目物理名もしくは項目物理名（Java） からデータ項目論理名をひく*/
    fun P2L(physicalName: String): String {
        //TODO: jsonEnquiryからの呼出しの場合、↓の条件は不要。いったん、コメントアウトしたが、恒久対応が必要
        /*if (config.getCurrentDBServerProduct() == "ACCESS_VIA_ODBC" ||
                config.getCurrentDBServerProduct() == "SQLITE") {
            return physicalName
        }*/
        // データ項目を優先して検索、なければテーブル一覧を検索
        // 該当がなければ、物理名→論理名変換せずに物理名をそのまま返す
        return P2LDictionary[physicalName]?.logicalName ?: P2LDBTables[physicalName] ?: physicalName
    }

    /**
     * 論理項目名で記述されたSQLを物理項目名に変換する
     * 前提：　論理項目名は必ずマルチバイトコードで構成していること
     * 論理項目名かどうかは、日本語（マルチバイトコード）かどうかで判断し、L2P()を呼び出して解決する
     * そのため、アルファベットで構成された論理項目名に対してはうまく動作しない
     */
    @OptIn(ExperimentalStdlibApi::class)
    fun L2P_SQL(sqlLogical: String): String {
        var multiByteCharStart = false
        var multiByteWord = StringBuffer("")
        var sqlPhysical = StringBuffer("")
        // 論理名で表されているSQL文を1文字づつ解析して、
        // マルチバイト文字の羅列を１つの論理名とみなして、論物変換していく
        for (ch in sqlLogical) {
            //Char.code()は文字をUTF-16の文字コードで返す。文字コードで、英数文字か日本語か区別できる
            //https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/code.html
            var chint = ch.code 
            // DEBUG:  print("ch["+ch+"]("+chint+")")
            if (chint >= 128) {
                // マルチバイト文字の場合
                // https://golang.hateblo.jp/entry/golang-string-byte-rune#%E3%82%B7%E3%83%B3%E3%82%B0%E3%83%AB%E3%83%90%E3%82%A4%E3%83%88%E6%96%87%E5%AD%97%E3%81%A8%E3%83%9E%E3%83%AB%E3%83%81%E3%83%90%E3%82%A4%E3%83%88%E6%96%87%E5%AD%97%E3%81%AE%E5%88%A4%E5%88%A5
                multiByteCharStart = true
                multiByteWord.append(ch)
            } else if (multiByteCharStart && chint < 128) {
                // 一つ前はマルチバイトで、この文字はシングルバイトの場合
                multiByteCharStart = false
                sqlPhysical.append(L2P(multiByteWord.toString()) + ch)
                multiByteWord = StringBuffer("")
            } else {
                // 一つ前も、この文字もシングルバイトの場合
                sqlPhysical.append(ch)
            }
        }
        return sqlPhysical.toString()
    }
}
