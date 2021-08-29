package com.github.yasusuzuki.spring.testkotlinboot


import org.junit.jupiter.api.Test

import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.capture
import org.mockito.kotlin.mock
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.argThat
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.times
import org.mockito.kotlin.whenever

import assertk.assertThat
import assertk.assertAll
import assertk.assertions.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TestAgentEnquiry {
    val log = LoggerFactory.getLogger(TestAgentEnquiry::class.java)

    fun extractAgentCodeSubCode(agentCodeSubCode: String):Pair<String,String>{
        var agentCode = agentCodeSubCode.take(5)
        var agentSubCode = ""
        if ( agentCodeSubCode.length > 5 ) {
            agentSubCode = agentCodeSubCode.slice(5..agentCodeSubCode.lastIndex)
        }
        return Pair(agentCode,agentSubCode)
    }

    @Test
    fun test() {
        var pair = extractAgentCodeSubCode("12345")
        assertThat(pair.first).isEqualTo("12345")
        assertThat(pair.second).isEqualTo("")
        
        pair = extractAgentCodeSubCode("123")
        assertThat(pair.first).isEqualTo("123")
        assertThat(pair.second).isEqualTo("") 


        pair = extractAgentCodeSubCode("12345678")
        assertThat(pair.first).isEqualTo("12345")
        assertThat(pair.second).isEqualTo("678")             
    }

    fun mockDictionary():Dictionary{
        return mock<Dictionary>{
            // テストでは、テスト結果をわかりやすくするため、L2Pは物理名に変換せず、論理名をそのまま返す
            on {L2P(any())} doAnswer {it.arguments[0] as String}
            on {L2P_SQL(any())} doAnswer {it.arguments[0] as String}
        }
    }
    fun mockDatabaseQuery(mockDic:Dictionary):DatabaseQuery{
        return mock<DatabaseQuery>{
            on {buildHTMLFromSQL(any(),any())} doAnswer{
                log.info("MOCK call back from buildHtmlFromSQL()")
                val callback = it.arguments[1] as Map<String, (String?,String?,List<String>?,Map<String,Any?>?) -> String>
                callback.get("じゅうぞく＿コード＿０１")?.invoke("じゅうぞく＿コード＿０１", "A", null, null)
                callback.get("じゅうぞく＿コード＿０２")?.invoke("じゅうぞく＿コード＿０２", "B", null, null)
                callback.get("じゅうぞく＿コード＿０３")?.invoke("じゅうぞく＿コード＿０３", "C", null, null)
                //Mockでは単純に引数に補足をして返すだけ。
                "RESULT from MOCKed buildHTMLFromSQL() -> %s".format(it.arguments[0] as String)
            }
            on {queryCriteria()} doAnswer {
                    //呼び出し側の処理がテストできないので、ここは本来の実装
                    DatabaseQuery.QueryCriteria(mockDic) 
                }
        }
    }

    @Test
    fun testBuildTable(){
        val mockDic = mockDictionary()
        val mockQuery = mockDatabaseQuery(mockDic)
        whenever(mockDic.listDBTables(any())) doReturn
            listOf(
                Dictionary.TableDefinition("代理店","Table1","代理店＿コード"),
                Dictionary.TableDefinition("代理店","Table2","代理店＿コード"),
                Dictionary.TableDefinition("代理店","Table3","代理店＿コード+代理店サブ＿コード" ),
                Dictionary.TableDefinition("代理店","Table4","じゅうぞく＿コード＿０１+じゅうぞく＿コード＿０２+じゅうぞく＿コード＿０３"),
            )

        var app = AgentEnquiry(mockDic,mockQuery)
        var req = AgentEnquiry.Request("12345-678",false)
        log.info("DEBUG app=$app req=$req")
        
        //Run Target
        var result = app.buildDataTables(req)
        result.forEach{ it.html() }

        //Extract Actual
        var actual : List<String>
        argumentCaptor<String>().apply {
            verify(mockQuery,times(4)).buildHTMLFromSQL(capture(),any())
            actual = allValues
        }

        //Verify
        assertAll{
            assertThat(actual[0]).isEqualTo("SELECT * FROM Table1 WHERE 代理店＿コード = '12345'")
            assertThat(actual[1]).isEqualTo("SELECT * FROM Table2 WHERE 代理店＿コード = '12345'")
            assertThat(actual[2]).isEqualTo("SELECT * FROM Table3 WHERE (代理店＿コード = '12345' AND 代理店サブ＿コード = '678')")
            assertThat(actual[3]).isEqualTo("SELECT * FROM Table4 WHERE (じゅうぞく＿コード＿０１ = 'A' AND じゅうぞく＿コード＿０２ = 'B' AND じゅうぞく＿コード＿０３ = 'C')")
        }
    }

    @Test
    fun testBuildTable2(){
        val mockDic = mockDictionary()
        val mockQuery = mockDatabaseQuery(mockDic)
        whenever(mockDic.listDBTables(any())) doReturn
            listOf(
                Dictionary.TableDefinition("代理店","Table1","代理店＿コード"),
                Dictionary.TableDefinition("代理店","Table2","代理店＿コード+代理店サブ＿コード" ),
            )

        var app = AgentEnquiry(mockDic,mockQuery)
        var req = AgentEnquiry.Request("12345-678 , ABCDE-FGH",false)
        log.info("DEBUG app=$app req=$req")
        
        //Run Target
        var result = app.buildDataTables(req)
        result.forEach{ it.html() }

        //Extract Actual
        var actual : List<String>
        argumentCaptor<String>().apply {
            verify(mockQuery,times(2)).buildHTMLFromSQL(capture(),any())
            actual = allValues
        }

        //Verify
        assertAll{
            assertThat(actual[0]).isEqualTo("SELECT * FROM Table1 WHERE 代理店＿コード IN ('12345','ABCDE')")
            assertThat(actual[1]).isEqualTo("SELECT * FROM Table2 WHERE (代理店＿コード = '12345' AND 代理店サブ＿コード = '678') OR (代理店＿コード = 'ABCDE' AND 代理店サブ＿コード = 'FGH')")
        }
    }
}

