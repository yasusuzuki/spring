package com.github.yasusuzuki.spring.testkotlinboot

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import org.slf4j.Logger
import org.slf4j.LoggerFactory
/**
 * 備忘：SpringBootのCofigurationPropertiesは、application.ymlから読み込んでくれる
 * 　　　Javaの場合は、setterがないとインジェクトしてくれないが、Kotlinだとsetterがなくてもインジェクトしてくれる
 *      prefixは大文字だとエラーになるので注意
 */
@Component
@ConfigurationProperties(prefix = "app")
class ConfigDef {
    val log = LoggerFactory.getLogger(ConfigDef::class.java)

    //appllication.ymlから読み込むプロパティ
    var dBConnections: List<Map<String, String>> = listOf()
    var dataDictionaryFilePath: List<String> = listOf()
    var dBTableListFilePath: String = ""
    var codeMasterFilePath: List<String> = listOf()

    //application.ymlとは関係ないプロパティ
    private var currentDBServerProduct: String = ""
    private var currentEnvironment: String = ""
    public fun setCurrentEnvironment ( value : String ){
        for ( con in dBConnections ) {
            if ( con["ENV"] == null ) { throw Error("config.ymlのapp.DBConnectionsにENVが設定されていません。") }
            if ( con["DB_SERVER_PRODUCT"] == null ) { throw Error("config.ymlのapp.DBConnectionsにDB_SERVER_PRODUCTが設定されていません。") }

            if ( con["ENV"] == value ){
                log.info("ENVを[$currentEnvironment] から[$value]へスイッチ")
                this.currentEnvironment = con["ENV"]!!
                this.currentDBServerProduct = con["DB_SERVER_PRODUCT"].orEmpty()
                break
            }
        }
        log.info("ENV = [$currentEnvironment] DB_SERVER_PRODUCT = [$currentDBServerProduct] ")
        if ( this.currentEnvironment == "" ) { throw Error("[$value]はapplication.ymlに登録されたENVではありません") }
    }
    public fun getCurrentEnvironment ():String {
        return currentEnvironment
    }
    public fun getCurrentDBServerProduct():String {
        return currentDBServerProduct
    }


    @PostConstruct
    fun postConstruct() {
        if ( dBConnections.size == 0 ) {
            throw Error("application.ymlにENVが登録されていません")
        }
        log.info("application.ymlに登録されたENV = " + dBConnections.map { t -> t["ENV"]})
        setCurrentEnvironment( dBConnections.get(0).get("ENV").orEmpty() )
    }

    fun listEnvironment(): List<String?> {
        val list = ArrayList<String>()
        for (env in dBConnections) {
            list.add(env["ENV"]!!)
        }
        return list
    }

    fun currentDateTime(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY/MM/dd (E) HH:mm:ss"))
    }

}