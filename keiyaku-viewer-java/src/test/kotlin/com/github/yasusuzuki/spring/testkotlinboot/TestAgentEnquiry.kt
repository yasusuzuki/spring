package com.github.yasusuzuki.spring.testkotlinboot


import org.junit.jupiter.api.Test

import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.capture
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.argThat
import org.mockito.kotlin.spy
import org.mockito.kotlin.KArgumentCaptor
import org.mockito.kotlin.argumentCaptor

import assertk.assertThat
import assertk.assertions.*

class TestAgentEnquiry {
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

    @Test
    fun testBuildTable(){
 
        val mockDic = mock<Dictionary>{
            // テストでは、テスト結果をわかりやすくするため、L2Pは物理名に変換せず、論理名をそのまま返す
            on {L2P(any())} doAnswer {it.arguments[0] as String}
            on {L2P_SQL(any())} doAnswer {it.arguments[0] as String}
        }
        val mockQuery = mock<DatabaseQuery>{
            on {buildHTMLFromSQL(any(),any())} doAnswer{
                println("MOCK call back from buildHtmlFromSQL()")
                val callback = it.arguments[1] as Map<String, (String?,String?,List<String>?,Map<String,Any?>?) -> String>
                callback.get("じゅうぞく＿コード＿０１")?.invoke("じゅうぞく＿コード＿０１", "A", null, null)
                callback.get("じゅうぞく＿コード＿０２")?.invoke("じゅうぞく＿コード＿０２", "B", null, null)
                callback.get("じゅうぞく＿コード＿０３")?.invoke("じゅうぞく＿コード＿０３", "C", null, null)
                //Mockでは単純に引数に補足をして返すだけ。
                "RESULT from MOCKed buildHTMLFromSQL() -> %s".format(it.arguments[0] as String)
            }
            on {queryCriteria()} doAnswer{
                    //呼び出し側の処理がテストできないので、ここは本来の実装
                    DatabaseQuery.QueryCriteria(mockDic) 
                }
        }
        var app = AgentEnquiry(mockDic,mockQuery)
        app.tableAndPrimaryKeys = listOf(
            Dictionary.TableDefinition("代理店","Table1","代理店＿コード"),
            Dictionary.TableDefinition("代理店","Table2","代理店＿コード"),
            Dictionary.TableDefinition("代理店","Table3","代理店＿コード+代理店サブ＿コード" ),
            Dictionary.TableDefinition("代理店","Table4","じゅうぞく＿コード＿０１+じゅうぞく＿コード＿０２+じゅうぞく＿コード＿０３"),
        )
        var req = AgentEnquiry.Request("12345",false)
        println("DEBUG app=$app req=$req")
        var result = app.buildDataTables(req)

        //以降は、org.mockito.kotlin.KArgumentCaptorを用いた技
        //https://qiita.com/wrongwrong/items/bf2fc29a63a848c041d0
        var t = result[0]
        println("Table[%s] html[%s".format(t.logicalTableName,t.html()))
        argumentCaptor<String>().apply {
            verify(mockQuery,atLeastOnce()).buildHTMLFromSQL(capture(),any())
            assertThat(lastValue).isEqualTo("SELECT * FROM Table1 WHERE 代理店＿コード = '12345'")
        }
        t = result[1]
        println("Table[%s] html[%s]".format(t.logicalTableName,t.html()))
        argumentCaptor<String>().apply {
            verify(mockQuery,atLeastOnce()).buildHTMLFromSQL(capture(),any())
            assertThat(lastValue).isEqualTo("SELECT * FROM Table2 WHERE 代理店＿コード = '12345'")
        }
        t = result[2]
        println("Table[%s] html[%s]".format(t.logicalTableName,t.html()))
        argumentCaptor<String>().apply {
                verify(mockQuery,atLeastOnce()).buildHTMLFromSQL(capture(),any())
            assertThat(lastValue).isEqualTo("SELECT * FROM Table3 WHERE (代理店＿コード = '12345' AND 代理店サブ＿コード LIKE '%')")
        }
        t = result[3]
        println("Table[%s] html[%s]".format(t.logicalTableName,t.html()))
        argumentCaptor<String>().apply {
                verify(mockQuery,atLeastOnce()).buildHTMLFromSQL(capture(),any())
            assertThat(lastValue).isEqualTo("SELECT * FROM Table4 WHERE (じゅうぞく＿コード＿０１ = 'A' AND じゅうぞく＿コード＿０２ = 'B' AND じゅうぞく＿コード＿０３ = 'C')")
        }
    }
}

