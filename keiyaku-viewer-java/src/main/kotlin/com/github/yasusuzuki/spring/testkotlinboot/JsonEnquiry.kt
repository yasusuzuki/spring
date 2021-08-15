package com.github.yasusuzuki.spring.testkotlinboot

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.multipart.MultipartFile

@Controller
class JsonEnquiry(var query: DatabaseQuery,var json: Json) {

    data class Request(
        val jsonFile: MultipartFile? = null, 
        val verboseMode: Boolean = false
    )

    data class TableResultPair(
        var logicalTableName: String = "",
        var html: String = ""
    )

    @GetMapping("/jsonEnquiry" )
    fun initialPage(@ModelAttribute req:Request, model:Model ): String {
        var dataTables : MutableList<TableResultPair> = mutableListOf()
        model["dataTables"] =  dataTables
        model["appName"] = "Keiyaku Viewer"
        model["req"] = req
        return "jsonEnquiry"
    }

    @PostMapping("/jsonEnquiry")
    fun execute(@ModelAttribute req:Request, model:Model ): String {
        println("Process jsonEnquiry:  req = $req")
        var jsonString = "{}"
        if ( req.jsonFile != null ) {
            jsonString = req.jsonFile.getInputStream().bufferedReader().use{it.readText()}
        }

        //callback
        var callback = mapOf(
            "VERBOSE_MODE_FLAG" to fun(_:String?,_:String?,_:List<String>?,_:Map<String,Any?>?):String{
                return if (req.verboseMode) {"on"} else {"off"}
            },
        )

        var dataTables : MutableList<TableResultPair> = mutableListOf()
        //var jsonString = Util.readFileAsString("data/test-teian.json")
        try {
            var jsonMap = json.readJsonFromString(jsonString)
            var jsonRecords = json.formatRelationalData(jsonMap)
            
            jsonRecords.forEach{ tableName, records ->
                var columnList = records.get(0).keys.toList()
                var sqlResultSet = Database.SQLResultSet(columnList,records)
                var html = query.buildHTML(sqlResultSet,"",callback)
                dataTables.add(TableResultPair(tableName,html))
            }
        } catch ( e:Exception ) {
            model["error"] = e.toString()
            model["jsonString"] = jsonString
        }
        model["dataTables"] =  dataTables
        model["appName"] = "Keiyaku Viewer"
        model["req"] = req
        return "jsonEnquiry"
    }
}