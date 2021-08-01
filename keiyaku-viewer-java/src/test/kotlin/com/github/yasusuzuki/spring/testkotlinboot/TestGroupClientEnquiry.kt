package com.github.yasusuzuki.spring.testkotlinboot


import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.capture
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.atLeastOnce
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.argThat

import org.mockito.ArgumentCaptor
import org.mockito.Mockito

import org.assertj.core.api.Assertions.assertThat

class TestGroupClientEnquiry {    
    @Test
    fun test() {
        val mockQuery = mock<DatabaseQuery>{
            //on {buildHTMLFromSQL(argThat{e -> true == "FROM 団体基本 WHERE".toRegex().containsMatchIn(e)},any())} doAnswer{
            on {buildHTMLFromSQL(any(),any())} doAnswer{
                    println("Call Mock buildHtmlFromSQL()")
                val callback = it.arguments[1] as Map<String, (String?,String?,List<String>?,Map<String,Any?>?) -> String>
                callback.get("KeyA")?.invoke("KeyA", "A", null, null)
                callback.get("KeyA")?.invoke("KeyA", "B", null, null)
                callback.get("KeyA")?.invoke("KeyA", null, null, null)
                callback.get("KeyA")?.invoke("KeyA", "D", null, null)
                callback.get("KeyB")?.invoke("KeyB", "aiueo", null, null)
                callback.get("KeyB")?.invoke("KeyB", "kakikukeko", null, null)
                callback.get("団体集団集約＿コード")?.invoke("団体集団集約＿コード", "X12", null, null)
                callback.get("団体集団集約＿コード")?.invoke("団体集団集約＿コード", "X12345", null, null)
                it.arguments[0] as String
            } 
        }
        val mockDic = mock<Dictionary>{
            // テストでは、テスト結果をわかりやすくするため、L2Pは物理名に変換せず、論理名をそのまま返す
            on {L2P(any())} doAnswer {it.arguments[0] as String}
        }
        var app = GroupClientEnquiry()
        app.query = mockQuery
        app.dic = mockDic
        app.tableAndPrimaryKeys = listOf(
            Dictionary.TableDefinition("団体","Table1","団体＿コード"),
            Dictionary.TableDefinition("団体","Table2","団体＿コード"),
            Dictionary.TableDefinition("団体","Table3","KeyA"),
            Dictionary.TableDefinition("団体","Table4","KeyB"),
            Dictionary.TableDefinition("団体","Table5","団体集団集約＿コード"),
            Dictionary.TableDefinition("団体","Table6","自動車大口集約＿コード"),
            Dictionary.TableDefinition("団体","Table7","火災大口集約＿コード"),

        )
        var req = GroupClientEnquiry.Request("12345",false)
        var result = app.buildDataTables(req)

        val sqlCaptor: ArgumentCaptor<String> = ArgumentCaptor.forClass(String::class.java)

        var t = result[0]
        println("Table[%s] html[%s]".format(t.logicalTableName,t.html()))
        verify(mockQuery,atLeastOnce()).buildHTMLFromSQL(capture(sqlCaptor),any())
        assertThat(sqlCaptor.getValue()).isEqualTo("SELECT * FROM Table1 WHERE 団体＿コード IN ('12345')")

        t = result[1]
        println("Table[%s] html[%s]".format(t.logicalTableName,t.html()))
        verify(mockQuery,atLeastOnce()).buildHTMLFromSQL(capture(sqlCaptor),any())
        assertThat(sqlCaptor.getValue()).isEqualTo("SELECT * FROM Table2 WHERE 団体＿コード IN ('12345')")

        t = result[2]
        println("Table[%s] html[%s]".format(t.logicalTableName,t.html()))
        verify(mockQuery,atLeastOnce()).buildHTMLFromSQL(capture(sqlCaptor),any())
        assertThat(sqlCaptor.getValue()).isEqualTo("SELECT * FROM Table3 WHERE KeyA IN ('A','B','null','D')")

        t = result[3]
        println("Table[%s] html[%s]".format(t.logicalTableName,t.html()))
        verify(mockQuery,atLeastOnce()).buildHTMLFromSQL(capture(sqlCaptor),any())
        assertThat(sqlCaptor.getValue()).isEqualTo("SELECT * FROM Table4 WHERE KeyB IN ('aiueo','kakikukeko')")

        t = result[4]
        println("Table[%s] html[%s]".format(t.logicalTableName,t.html()))
        verify(mockQuery,atLeastOnce()).buildHTMLFromSQL(capture(sqlCaptor),any())
        assertThat(sqlCaptor.getValue()).isEqualTo("SELECT * FROM Table5 WHERE 団体集団集約＿コード IN ('X12','X12345')")

        t = result[5]
        println("Table[%s] html[%s]".format(t.logicalTableName,t.html()))
        verify(mockQuery,atLeastOnce()).buildHTMLFromSQL(capture(sqlCaptor),any())
        assertThat(sqlCaptor.getValue()).isEqualTo("SELECT * FROM Table6 WHERE 自動車大口集約＿コード IN ('CX12','CX1234')")

        t = result[6]
        println("Table[%s] html[%s]".format(t.logicalTableName,t.html()))
        verify(mockQuery,atLeastOnce()).buildHTMLFromSQL(capture(sqlCaptor),any())
        assertThat(sqlCaptor.getValue()).isEqualTo("SELECT * FROM Table7 WHERE 火災大口集約＿コード IN ('FX12','FX1234')")

    }
}

