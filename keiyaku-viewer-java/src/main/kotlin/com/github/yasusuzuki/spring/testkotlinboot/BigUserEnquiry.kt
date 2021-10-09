package com.github.yasusuzuki.spring.testkotlinboot

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Controller
class BigUserEnquiry(var config: ConfigDef, var dic: Dictionary,var query: DatabaseQuery) {
    val log = LoggerFactory.getLogger(BigUserEnquiry::class.java)

    data class Request(
        var gyoumuCode: String = "",
        var groupingNumber: String = "",
        var maxFetchRows: Int = 100,
    )
    
    @GetMapping("/bigUserEnquiry")
    fun execute(@ModelAttribute req:Request, model:Model ): String {
        log.info("Process bigUserEnquiry:  req = ${req}")
        
        var sql = "SELECT a.*,b.代理店サブ名称＿漢字 FROM ビッグユーザー業務管理．登録要素．登録内容 a "
        sql += " INNER JOIN 代理店サブ b ON a.組織グルーピング管理＿番号 = CONCAT(b.代理店＿コード,b.代理店サブ＿コード) "
        sql += " WHERE 1 = 1 "
        if ( req.gyoumuCode != "" ){
            sql += " AND ビッグユーザー業務内容＿コード LIKE '%${req.gyoumuCode}%' "
        }
        if ( req.groupingNumber != "" ){
            sql += " AND 組織グルーピング管理＿番号 LIKE '%${req.groupingNumber}%' "
        }
        sql += " FETCH FIRST ${req.maxFetchRows} ROWS ONLY "

        //callback
        var callback = mapOf(
            "HIDE_DB_SYSTEM_COLUMNS_FLAG" to fun(_:String?,_:String?,_:List<String>?,_:Map<String,Any?>?):String{
                return "on"
            },
        )

        model["dataTable"] = query.buildHTMLFromSQL(dic.L2P_SQL(sql),callback)

        model["appName"] = "Keiyaku Viewer"
        model["req"] = req
        model["SQL"] = dic.L2P_SQL(sql)
        return "bigUserEnquiry"
    }
}