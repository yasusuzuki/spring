package com.github.yasusuzuki.spring.testkotlinboot

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Component

import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute


@Controller
class CodeMasterEnquiry {
    data class Request(
        var domain: String = "", 
        var codeValue: String = "", 
        var codeName: String = "", 
        var maxFetchRows: Int = 500, 
    )

    @Autowired
    lateinit var dic: Dictionary

    @GetMapping("/codeMasterEnquiry" )
    fun execute(@ModelAttribute req:Request, model:Model ): String {
        println("Process codeMasterEnquiry:  req = $req")
        
    	//１．　HTTPリクエストパラメータを解析する
        //TODO: rtrimはNULLも返却する可能性がある。ここではNULLはありえないのでrtrimとは別の関数を用意する
        req.domain = Util.rtrim(req.domain)!!
        req.codeValue = Util.rtrim(req.codeValue)!!
        req.codeName = Util.rtrim(req.codeName)!!
        var max = req.maxFetchRows
        var dataTable = ""

	    //２．　データから必要な情報を抽出する
        data class DomainValueNameTriple (
            var domain : String = "",
            var codeValue : String = "",
            var codeName : String = ""
        )
        var list = mutableListOf<DomainValueNameTriple>()
        for ( domain in dic.CodeMaster.keys ) {
            //検索条件と部分一致でも抽出できるようにindexOf()を用いる
            if ( domain.indexOf(req.domain) != -1 ) { //req.Domainがブランクの場合は常に真
                for ( m  in dic.CodeMaster[domain]!! ) {
                    //DEBUG: println("codeName ${m.codeName}")
                    if ( (req.codeValue == "" || req.codeValue == m.codeValue) &&
                        (req.codeName == "" || m.codeName.indexOf(req.codeName) != -1 ) ) {
                        list.add(DomainValueNameTriple(domain, m.codeValue, m.codeName))
                    }
                }
            }
        }

        //抽出結果の行数か、要求された最大表示数の、小さいほうをMAXにする
        if ( list.size < max ) {
            max = list.size
        }
    

        //３．　抽出したデータを表示する
        dataTable += "<font color='grey'>" + max.toString() + " row(s) fetched</font>"
        dataTable += "<TABLE CELLSPACING=0 CLASS='DataTable'>\n"
        dataTable += "<TR><TH>ドメイン</TH><TH>コード値</TH><TH>コード名称</TH></TR>"
        var currentRow = 0
        for ( v in list ) {
            dataTable += "<TR><TD>%s</TD><TD>%s</TD><TD>%s</TD></TR>".format(Util.rtrim(v.domain) ,Util.rtrim(v.codeValue),Util.rtrim(v.codeName))
            currentRow++
            if ( currentRow >= max ) {
                break
            }
        }

        //４．　入力フォームを組み立てる
        //not required

        //５．　テンプレートエンジンからのコールバックを定義する
        //no callback
        model["appName"] = "Keiyaku Viewer"
        model["req"] = req
        model["dataTable"] = dataTable
        return "codeMasterEnquiry"
    }
}