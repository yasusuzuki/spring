package com.github.yasusuzuki.spring.testkotlinboot

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct


/**
 * 備忘：SpringBootのCofigurationPropertiesは、application.ymlから読み込んでくれる
 * 　　　Javaの場合は、setterがないとインジェクトしてくれないが、Kotlinだとsetterがなくてもインジェクトしてくれる
 *      prefixは大文字だとエラーになるので注意
 */
@Component
@ConfigurationProperties(prefix = "app")
class ConfigDef {
    //appllication.ymlから読み込むプロパティ
    var dBConnections: List<Map<String, String>> = listOf()
    var dataDictionaryFilePath: String = ""
    var dBTableListFilePath: String = ""
    var codeMasterFilePath: String = ""

    //application.ymlとは関係ないプロパティ
    private var currentDBServerProduct: String = ""
    private var currentEnvironment: String = ""
    public fun setCurrentEnvironment ( value : String ){
        for ( con in dBConnections ) {
            if ( con["ENV"] == null ) { throw Error("config.ymlのapp.DBConnectionsにENVが設定されていません。") }
            if ( con["DB_SERVER_PRODUCT"] == null ) { throw Error("config.ymlのapp.DBConnectionsにDB_SERVER_PRODUCTが設定されていません。") }

            if ( con["ENV"] == value ){
                println("ENVを[$currentEnvironment] から[$value]へスイッチ")
                this.currentEnvironment = con["ENV"]!!
                this.currentDBServerProduct = con["DB_SERVER_PRODUCT"].orEmpty()
                break
            }
        }
        println("ENV = [$currentEnvironment] DB_SERVER_PRODUCT = [$currentDBServerProduct] ")
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
        println("application.ymlに登録されたENV = " + dBConnections.map { t -> t["ENV"]})
        setCurrentEnvironment( dBConnections.get(0).get("ENV").orEmpty() )
    }

    fun listEnvironment(): List<String?> {
        val list = ArrayList<String>()
        for (env in dBConnections) {
            list.add(env["ENV"]!!)
        }
        return list
    }

}