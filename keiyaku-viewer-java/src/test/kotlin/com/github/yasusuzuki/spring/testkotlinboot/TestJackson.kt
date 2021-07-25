package com.github.yasusuzuki.spring.testkotlinboot


import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class TestJackson {
    fun initJson() : Json {
        var conf = ConfigDef()
		var dic = Dictionary()
		conf.dBConnections = listOf(mapOf("ENV" to "dummy env","DB_SERVER_PRODUCT" to "DB2"))
		conf.dataDictionaryFilePath = ".\\data\\data_dictionary.csv"
		conf.dBTableListFilePath = ".\\data\\db_tables.csv"
		conf.codeMasterFilePath = ".\\data\\codemaster.csv"
		conf.init()
		dic.config = conf
		dic.init()
        var json = Json()
        json.dic = dic
        return json
    }

    @Test
    fun testJackson() {
        var app = initJson()
        var jsonString = Util.readFileAsString("data/test-teian.json")
        var jsonMap = app.readJsonFromString(jsonString)
        var jsonRecords = app.formatRelationalData(jsonMap)
        var dq = DatabaseQuery()
        dq.d = app.dic
        jsonRecords.forEach{ tableName, records ->
            var columnList = records.get(0).keys.toList()
            var sqlResultSet = Database.SQLResultSet(columnList,records)
            println("<h1>$tableName</h1>" + dq.buildHTML(sqlResultSet,"",mapOf()))
        }
    }
}

