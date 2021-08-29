package com.github.yasusuzuki.spring.testkotlinboot

import org.mockito.kotlin.mock

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import assertk.assertThat
import assertk.assertions.*

class TestJackson {
    fun initJson() : Json {
        var conf = ConfigDef()
		var dic = Dictionary(conf)
		conf.dBConnections = listOf(mapOf("ENV" to "dummy env","DB_SERVER_PRODUCT" to "DB2"))
		conf.dataDictionaryFilePath = ".\\data\\data_dictionary.csv"
		conf.dBTableListFilePath = ".\\data\\db_tables.csv"
		conf.codeMasterFilePath = ".\\data\\codemaster.csv"
		conf.postConstruct()
		dic.postConstruct()
        var json = Json(dic)
        return json
    }

    @Test
    fun testJackson() {
        var app = initJson()
        var jsonString = Util.readFileAsString("data/test-teian.json")
        var jsonMap = app.readJsonFromString(jsonString)       
        var jsonRecords = app.formatRelationalData(jsonMap)
        assertThat(jsonMap.size).isEqualTo(6695)
        assertThat(jsonRecords.size).isEqualTo(45)
        
        
        val mockDb = mock<Database>{}
        var dq = DatabaseQuery(mockDb,app.dic,app.dic.config)
        jsonRecords.forEach{ tableName, records ->
            var columnList = records.get(0).keys.toList()
            var sqlResultSet = Database.SQLResultSet(columnList,records)
            //var out = 
            "<h1>$tableName</h1>" + dq.buildHTML(sqlResultSet,"",mapOf())
           
        }
    }
}

