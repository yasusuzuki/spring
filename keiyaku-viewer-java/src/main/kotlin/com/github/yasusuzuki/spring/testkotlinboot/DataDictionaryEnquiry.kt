package com.github.yasusuzuki.spring.testkotlinboot

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute


@Controller
class DataDictionaryEnquiry(var dic: Dictionary) {
    data class Request(
        var fieldLogicalName: String = "", 
        var fieldPhysicalName: String = "", 
        var maxFetchRows: Int = 500, 
    )

    @GetMapping("/dataDictionaryEnquiry" )
    fun execute(@ModelAttribute req:Request, model:Model ): String {
        println("Process dataDictionaryEnquiry:  req = $req")
        
    	//１．　HTTPリクエストパラメータを解析する
        //TODO: rtrimはNULLも返却する可能性がある。ここではNULLはありえないのでrtrimとは別の関数を用意する
        req.fieldLogicalName = Util.rtrim(req.fieldLogicalName)!!
        req.fieldPhysicalName = Util.rtrim(req.fieldPhysicalName)!!
        var max = req.maxFetchRows
        var dataTable = ""

	    //２．　データから必要な情報を抽出する
        data class FieldLogicalPhysicalPai (
            var fieldLogicalName : String = "",
            var fieldPhysicalName : String = ""
        )
        var list = mutableListOf<FieldLogicalPhysicalPai>()
        var currentRow = 0
        for ( (k, v) in dic.L2PDictionary ) {
            if ( (req.fieldLogicalName == "" || k.indexOf(req.fieldLogicalName) != -1 ) &&
                 (req.fieldPhysicalName == "" || v.physicalName.indexOf(req.fieldPhysicalName) != -1) ){
                list.add( FieldLogicalPhysicalPai(k,v.physicalName))
                currentRow++
            }
            if ( currentRow >= max ) {
                break
            }
        }

        //３．　抽出したデータを表示する
        dataTable += "<font color='grey'>" + list.size + " rows fetched</font>"
        dataTable += "<TABLE CELLSPACING=0 CLASS='DataTable'>\n"
        dataTable += "<TR><TH>データ項目名</TH><TH>項目物理名（データモデリング）</TH></TR>"
        for ( v in list ) {
            dataTable += "<TR><TD>" + Util.rtrim(v.fieldLogicalName) + "</TD><TD>" + Util.rtrim(v.fieldPhysicalName) + "</TD></TR>"
        }
        dataTable += "</TABLE>"

        //４．　入力フォームを組み立てる
        //not required

        //５．　テンプレートエンジンからのコールバックを定義する
        //no callback

        model["appName"] = "Keiyaku Viewer"
        model["req"] = req
        model["dataTable"] = dataTable
        return "dataDictionaryEnquiry"
    }
}