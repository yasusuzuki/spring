package com.github.yasusuzuki.spring.testkotlinboot


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
class TestTeianEnquiry {
    val log = LoggerFactory.getLogger(TestTeianEnquiry::class.java)

    fun mockDictionary():Dictionary{
        return mock<Dictionary>{
            // テストでは、テスト結果をわかりやすくするため、L2Pは物理名に変換せず、論理名をそのまま返す
            on {L2P(any())} doAnswer {it.arguments[0] as String}
            on {L2P_SQL(any())} doAnswer {it.arguments[0] as String}
        }
    }

    fun mockQuery(mockDic:Dictionary):DatabaseQuery{
        return mock<DatabaseQuery>{
            on {buildHTMLFromSQL(any(),any())} doAnswer{
                log.info("MOCK call back from buildHtmlFromSQL()")
                    //Mockでは単純に引数に補足をして返すだけ。
                    "RESULT from MOCKed buildHTMLFromSQL() -> %s".format(it.arguments[0] as String)
            }
            on {queryCriteria()} doAnswer{
                    //呼び出し側の処理がテストできないので、ここは本来の実装
                    DatabaseQuery.QueryCriteria(mockDic) 
            }
        }        
    }

    /**
     * Request(ankenNumber="00012345-001-0000-1",false)で実行
     */
    @Test
    fun testBuildTable1(){
        //Set up Mock
        val mockDic = mockDictionary()
        val mockQuery = mockQuery(mockDic)
        var app = TeianEnquiry(mockDic,mockQuery)
        app.tableAndPrimaryKeys = listOf(
            Dictionary.TableDefinition("提案","Table1","提案案件＿番号+提案案件番号枝番＿番号+提案連続＿番号+提案設計データバージョン番号＿数"),
            Dictionary.TableDefinition("提案","Table2","提案案件＿番号+提案案件番号枝番＿番号"),
            Dictionary.TableDefinition("提案","Table3","提案案件＿番号+提案案件番号枝番＿番号+提案連続＿番号+提案設計データバージョン番号＿数"),
        )

        //Set up Input
        var req = TeianEnquiry.Request("00012345-001-0000-1",false)
        log.info("DEBUG app=$app req=$req")

        //Run Target
        var result = app.buildDataTables(req)
        result.forEach{ log.info("Table[%s] html[%s".format(it.logicalTableName,it.html())) }

        //Extract Actual
        var actual : List<String>
        argumentCaptor<String>().apply {
            verify(mockQuery,times(3)).buildHTMLFromSQL(capture(),any())
            actual = allValues
        }

        //Verify
        assertAll{
            assertThat(actual[0]).isEqualTo("SELECT * FROM Table1 WHERE (提案案件＿番号 = '00012345' AND 提案案件番号枝番＿番号 = '001' AND 提案連続＿番号 = '0000' AND 提案設計データバージョン番号＿数 = '1')")
            assertThat(actual[1]).isEqualTo("SELECT * FROM Table2 WHERE (提案案件＿番号 = '00012345' AND 提案案件番号枝番＿番号 = '001')")
            assertThat(actual[2]).isEqualTo("SELECT * FROM Table3 WHERE (提案案件＿番号 = '00012345' AND 提案案件番号枝番＿番号 = '001' AND 提案連続＿番号 = '0000' AND 提案設計データバージョン番号＿数 = '1')")
        }
    }

    /**
     * Request(ankenNumber="00012345-001-0000-1",false)で実行
     */
    @Test
    fun testBuildTable2(){
        //Set up Mock
        val mockDic = mockDictionary()
        val mockQuery = mockQuery(mockDic)
        var app = TeianEnquiry(mockDic,mockQuery)
        app.tableAndPrimaryKeys = listOf(
            Dictionary.TableDefinition("提案","Table1","提案案件＿番号+提案案件番号枝番＿番号+提案連続＿番号+提案設計データバージョン番号＿数"),
            Dictionary.TableDefinition("提案","Table2","提案案件＿番号+提案案件番号枝番＿番号"),
            Dictionary.TableDefinition("提案","Table3","提案案件＿番号+提案案件番号枝番＿番号+提案連続＿番号+提案設計データバージョン番号＿数"),
        )

        //Set up Input
        var req = TeianEnquiry.Request("00012345",false)
        log.info("DEBUG app=$app req=$req")

        //Run Target
        var result = app.buildDataTables(req)
        result.forEach{ log.info("Table[%s] html[%s".format(it.logicalTableName,it.html())) }

        //Extract Actual
        var actual : List<String>
        argumentCaptor<String>().apply {
            verify(mockQuery,times(3)).buildHTMLFromSQL(capture(),any())
            actual = allValues
        }
        //Verify
        assertAll{
            assertThat(actual[0]).isEqualTo("SELECT * FROM Table1 WHERE (提案案件＿番号 = '00012345' AND 1 = 1 AND 1 = 1 AND 1 = 1)")
            assertThat(actual[1]).isEqualTo("SELECT * FROM Table2 WHERE (提案案件＿番号 = '00012345' AND 1 = 1)")
            assertThat(actual[2]).isEqualTo("SELECT * FROM Table3 WHERE (提案案件＿番号 = '00012345' AND 1 = 1 AND 1 = 1 AND 1 = 1)")
        }
    }

    /**
     * コンマ区切りで複数の検索値を指定するケース
     * Request(ankenNumber="00012345-001-0000-1,00098765",false)で実行
     */
    @Test
    fun testBuildTable3(){
        //Set up Mock
        val mockDic = mockDictionary()
        val mockQuery = mockQuery(mockDic)
        var app = TeianEnquiry(mockDic,mockQuery)
        app.tableAndPrimaryKeys = listOf(
            Dictionary.TableDefinition("提案","Table1","提案案件＿番号+提案案件番号枝番＿番号+提案連続＿番号+提案設計データバージョン番号＿数"),
            Dictionary.TableDefinition("提案","Table2","提案案件＿番号+提案案件番号枝番＿番号"),
            Dictionary.TableDefinition("提案","Table3","提案案件＿番号+提案案件番号枝番＿番号+提案連続＿番号+提案設計データバージョン番号＿数"),
        )

        //Set up Input
        var req = TeianEnquiry.Request("00012345-001-0000-1,00098765",false)
        log.info("DEBUG app=$app req=$req")

        //Run Target Logic
        var result = app.buildDataTables(req)
        result.forEach{ log.info("Table[%s] html[%s".format(it.logicalTableName,it.html())) }

        //Extract Actual
        var actual : List<String>
        argumentCaptor<String>().apply {
            verify(mockQuery,times(3)).buildHTMLFromSQL(capture(),any())
            actual = allValues
        }
        //Verify
        assertAll{
            assertThat(actual[0]).isEqualTo("SELECT * FROM Table1 WHERE (提案案件＿番号 = '00012345' AND 提案案件番号枝番＿番号 = '001' AND 提案連続＿番号 = '0000' AND 提案設計データバージョン番号＿数 = '1') OR (提案案件＿番号 = '00098765' AND 1 = 1 AND 1 = 1 AND 1 = 1)")
            assertThat(actual[1]).isEqualTo("SELECT * FROM Table2 WHERE (提案案件＿番号 = '00012345' AND 提案案件番号枝番＿番号 = '001') OR (提案案件＿番号 = '00098765' AND 1 = 1)")
            assertThat(actual[2]).isEqualTo("SELECT * FROM Table3 WHERE (提案案件＿番号 = '00012345' AND 提案案件番号枝番＿番号 = '001' AND 提案連続＿番号 = '0000' AND 提案設計データバージョン番号＿数 = '1') OR (提案案件＿番号 = '00098765' AND 1 = 1 AND 1 = 1 AND 1 = 1)")
        }
    }



}

