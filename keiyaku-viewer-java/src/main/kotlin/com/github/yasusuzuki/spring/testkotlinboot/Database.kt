package com.github.yasusuzuki.spring.testkotlinboot

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import javax.annotation.PostConstruct
import javax.sql.DataSource
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.createDirectory
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
@Component
class Database {
    var logger: Logger = LoggerFactory.getLogger(Database::class.java)

    @Autowired
    lateinit var appConfig: ConfigDef
    @Autowired
    lateinit var dic: Dictionary

    lateinit var hikari: HikariDataSource

    val dataSourcePool = HashMap<String?, DataSource>()

    fun getConnection(envName: String): Connection {
        val ds = dataSourcePool[envName]
        appConfig.setCurrentEnvironment(envName)
        return if (ds != null) {
            try {
                ds.connection
            } catch (e: SQLException) {
                throw Error(e)
            }
        } else {
            throw Error("ENV [$envName] not exists")
        }
    }

    @PostConstruct
    fun init() {
        for (env in appConfig.dBConnections) {
            val envName = env["ENV"]
            var dbProduct = env["DB_SERVER_PRODUCT"]
            if (dbProduct == "DB2") {
                dataSourcePool[envName] = dataSourceDB2(env)
            } else if (dbProduct == "ACCESS_VIA_ODBC") {
                dataSourcePool[envName] = dataSourceAccessDB(env)
            } else {
                throw Error("invalid DB_SERVER_ROOT [" + dbProduct + "]")
            }
        }
    }

    private fun dataSourceDB2(env: Map<String, String>): DataSource {
        val config = initHikariDatasource()
        config.driverClassName = "com.ibm.db2.jcc.DB2Driver"
        val jdbcUrl = String.format(
            "jdbc:db2://%s:%s/%s",
            env["HOSTNAME"],
            env["PORT"],
            env["DATABASE"]
        )
        println("Connecting To $jdbcUrl")
        config.jdbcUrl = jdbcUrl
        config.username = env["UID"]
        config.password = env["PWD"]
        config.addDataSourceProperty("currentSchema",env["SCHEMA"])

        hikari = HikariDataSource(config)
        return hikari
    }

    private fun dataSourceAccessDB(env: Map<String, String>): DataSource {
        val config = initHikariDatasource()
        config.driverClassName = "net.ucanaccess.jdbc.UcanaccessDriver"
        //固定で./accdbCache/というフォルダにキャッシュを作成する 
        var folder = Paths.get(env["DB"]).parent.resolve("accdbCache")
        if ( Files.exists(folder) && ! Files.isDirectory(folder) ){
            throw Error("AccessDBのキャッシュファイルを作成できません [${folder}]")
        } else if ( ! Files.exists(folder) ) {
            Files.createDirectory(folder)
        }
        //accessdb_cache
        val jdbcUrl = String.format(
            "jdbc:ucanaccess://%s;keepMirror=%s;",
            env["DB"],
            folder.resolve("cache")
        )
        println("Env " + env["ENV"] + " Connecting to " + jdbcUrl)
        config.jdbcUrl = jdbcUrl
        hikari = HikariDataSource(config)
        return hikari
    }

    private fun initHikariDatasource(): HikariConfig {
        val config = HikariConfig()
        // キャッシュ系の設定(任意)
        config.addDataSourceProperty("cachePrepStmts", "true")
        config.addDataSourceProperty("prepStmtCacheSize", "250")
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
        // サーバサイドプリペアードステートメントを使用する(任意)
        config.addDataSourceProperty("useServerPrepStmts", "true")
        // アプリがプールから一定時間コネクションを取得できない場合に例外をなげる
        config.connectionTimeout = 10000
        // 接続をテストするためのクエリ - Access DBだと SELECT 1 が有効なSQLではないため、いったんコメントアウト
        // JDBC Type4 では、invalid()メソッドがあるので、DB2はこれがなくても大丈夫
        //config.setConnectionInitSql("SELECT 1 ");
        
        // E0004 プールのなかにあらかじめ溜めておく接続済みのコネクション
        // デフォルトは10だが、万が一を設定を誤ると一発でアカウントがリボークされてしまうので、1にした
        config.maximumPoolSize = 1
        return config
    }

    data class SQLResultSet (
        var columnList: List<String>,
        var recordList: List<Map<String, Any?>>
    )


    fun queryForList(con: Connection, sql: String?): SQLResultSet {
        val recordList = ArrayList<Map<String,Any?>>()
        val columnList = ArrayList<String>()
        lateinit var rt : ResultSet
        lateinit var stmt : Statement
        try {
            logger.info("Connecting to -- $con")
            stmt = con.createStatement()
            logger.info("Executing SQL -- $sql")
            rt = stmt.executeQuery(sql)
            val numOfColumns = rt.metaData.columnCount
            for (i in 1..numOfColumns) { //SQL ResultSetの列は１から始まるので注意
                columnList.add(dic.P2L(rt.metaData.getColumnName(i)))
            }
            while (rt.next()) {
                val record = HashMap<String, Any?>()
                for (i in 0 until numOfColumns) { //SQL ResultSetの列は１から始まるので注意
                    record[columnList[i]] = rt.getObject(i + 1)
                }
                recordList.add(record)
            }
            rt.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            stmt.close()
        }
        return SQLResultSet(columnList,recordList)
    }
}