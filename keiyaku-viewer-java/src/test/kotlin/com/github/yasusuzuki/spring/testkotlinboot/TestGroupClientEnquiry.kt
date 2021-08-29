package com.github.yasusuzuki.spring.testkotlinboot


import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.capture
import org.mockito.kotlin.mock
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.argThat
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.times

import assertk.assertThat
import assertk.assertAll
import assertk.assertions.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
class TestGroupClientEnquiry {
    val log = LoggerFactory.getLogger(TestGroupClientEnquiry::class.java)

    @Test
    fun test() {
        val mockDic = mock<Dictionary>{
            // テストでは、テスト結果をわかりやすくするため、L2Pは物理名に変換せず、論理名をそのまま返す
            on {L2P(any())} doAnswer {it.arguments[0] as String}
            on {P2L(any())} doAnswer {it.arguments[0] as String}
        }
        val mockQuery = mock<DatabaseQuery>{
            on {buildHTMLFromSQL(any(),any())} doAnswer{
                log.info("Call Mock buildHtmlFromSQL()")
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
        //Run Target
        var result = app.buildDataTables(req)
        result.forEach{ it.html() }

        //Extract Actual
        var actual : List<String>
        argumentCaptor<String>().apply {
            verify(mockQuery,times(7)).buildHTMLFromSQL(capture(),any())
            actual = allValues
        }

        //Verify
        assertAll{ 
            assertThat(actual[0]).isEqualTo("SELECT * FROM Table1 WHERE 団体＿コード = '12345'")
            assertThat(actual[1]).isEqualTo("SELECT * FROM Table2 WHERE 団体＿コード = '12345'")
            assertThat(actual[2]).isEqualTo("SELECT * FROM Table3 WHERE KeyA IN ('A','B',NULL,'D')")
            assertThat(actual[3]).isEqualTo("SELECT * FROM Table4 WHERE KeyB IN ('aiueo','kakikukeko')")
            assertThat(actual[4]).isEqualTo("SELECT * FROM Table5 WHERE IGR_SDN_CSD_CD IN ('X12','X12345')")
            assertThat(actual[5]).isEqualTo("SELECT * FROM Table6 WHERE CAR_LGO_CSD_CD IN ('CX12','C12345')")
            assertThat(actual[6]).isEqualTo("SELECT * FROM Table7 WHERE FIR_LGO_CSD_CD IN ('FX12','F12345')")
        }
    }
}

