package com.github.yasusuzuki.spring.testkotlinboot


import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith

import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.capture
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.argThat
import org.mockito.kotlin.KArgumentCaptor
import org.mockito.kotlin.argumentCaptor


import assertk.assertThat
import assertk.assertions.*

class TestGroupClientEnquiry {    
    @Test
    fun test() {
        val mockDic = mock<Dictionary>{
            // テストでは、テスト結果をわかりやすくするため、L2Pは物理名に変換せず、論理名をそのまま返す
            on {L2P(any())} doAnswer {it.arguments[0] as String}
            on {P2L(any())} doAnswer {it.arguments[0] as String}
        }
        val mockQuery = mock<DatabaseQuery>{
            on {buildHTMLFromSQL(any(),any())} doAnswer{
                println("Call Mock buildHtmlFromSQL()")
                val callback = it.arguments[1] as Map<String, (String?,String?,List<String>?,Map<String,Any?>?) -> String>
                callback.get("KeyA")?.invoke("KeyA", "A", null, null)
                callback.get("KeyA")?.invoke("KeyA", "B", null, null)
                callback.get("KeyA")?.invoke("KeyA", null, null, null)
                callback.get("KeyA")?.invoke("KeyA", "D", null, null)
                callback.get("KeyB")?.invoke("KeyB", "aiueo", null, null)
                callback.get("KeyB")?.invoke("KeyB", "kakikukeko", null, null)
                callback.get("IGR_SDN_CSD_CD")?.invoke("IGR_SDN_CSD_CD", "X12", null, null)
                callback.get("IGR_SDN_CSD_CD")?.invoke("IGR_SDN_CSD_CD", "X12345", null, null)
                it.arguments[0] as String
            }
            on {queryCriteria()} doAnswer{ 
                DatabaseQuery.QueryCriteria(mockDic) 
            }
        }

        var app = GroupClientEnquiry(mockQuery,mockDic)
        app.tableAndPrimaryKeys = listOf(
            Dictionary.TableDefinition("団体","Table1","団体＿コード"),
            Dictionary.TableDefinition("団体","Table2","団体＿コード"),
            Dictionary.TableDefinition("団体","Table3","KeyA"),
            Dictionary.TableDefinition("団体","Table4","KeyB"),
            Dictionary.TableDefinition("団体","Table5","IGR_SDN_CSD_CD"),
            Dictionary.TableDefinition("団体","Table6","CAR_LGO_CSD_CD"),
            Dictionary.TableDefinition("団体","Table7","FIR_LGO_CSD_CD"),

        )
        var req = GroupClientEnquiry.Request("12345",false)
        var result = app.buildDataTables(req)

        var t = result[0]
        println("Table[%s] html[%s]".format(t.logicalTableName,t.html()))
        argumentCaptor<String>().apply {
            verify(mockQuery,atLeastOnce()).buildHTMLFromSQL(capture(),any())
            assertThat(lastValue).isEqualTo("SELECT * FROM Table1 WHERE 団体＿コード = '12345'")
        }
        t = result[1]
        println("Table[%s] html[%s]".format(t.logicalTableName,t.html()))
        argumentCaptor<String>().apply {
            verify(mockQuery,atLeastOnce()).buildHTMLFromSQL(capture(),any())
            assertThat(lastValue).isEqualTo("SELECT * FROM Table2 WHERE 団体＿コード = '12345'")
        }
        t = result[2]
        println("Table[%s] html[%s]".format(t.logicalTableName,t.html()))
        argumentCaptor<String>().apply {
            verify(mockQuery,atLeastOnce()).buildHTMLFromSQL(capture(),any())
            assertThat(lastValue).isEqualTo("SELECT * FROM Table3 WHERE KeyA IN ('A','B',NULL,'D')")
        }
        t = result[3]
        println("Table[%s] html[%s]".format(t.logicalTableName,t.html()))
        argumentCaptor<String>().apply {
            verify(mockQuery,atLeastOnce()).buildHTMLFromSQL(capture(),any())
            assertThat(lastValue).isEqualTo("SELECT * FROM Table4 WHERE KeyB IN ('aiueo','kakikukeko')")
        }
        t = result[4]
        println("Table[%s] html[%s]".format(t.logicalTableName,t.html()))
        argumentCaptor<String>().apply {
            verify(mockQuery,atLeastOnce()).buildHTMLFromSQL(capture(),any())
            assertThat(lastValue).isEqualTo("SELECT * FROM Table5 WHERE IGR_SDN_CSD_CD IN ('X12','X12345')")
        }
        t = result[5]
        println("Table[%s] html[%s]".format(t.logicalTableName,t.html()))
        argumentCaptor<String>().apply {
            verify(mockQuery,atLeastOnce()).buildHTMLFromSQL(capture(),any())
            assertThat(lastValue).isEqualTo("SELECT * FROM Table6 WHERE CAR_LGO_CSD_CD IN ('CX12','C12345')")
        }
        t = result[6]
        println("Table[%s] html[%s]".format(t.logicalTableName,t.html()))
        argumentCaptor<String>().apply {
            verify(mockQuery,atLeastOnce()).buildHTMLFromSQL(capture(),any())
            assertThat(lastValue).isEqualTo("SELECT * FROM Table7 WHERE FIR_LGO_CSD_CD IN ('FX12','F12345')")
        }
    }
}

