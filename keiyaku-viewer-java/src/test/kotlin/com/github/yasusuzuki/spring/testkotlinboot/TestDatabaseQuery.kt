package com.github.yasusuzuki.spring.testkotlinboot

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach

import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.capture
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.argThat

import assertk.assertThat
import assertk.assertions.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
class TestDatabaseQuery {
    val log = LoggerFactory.getLogger(TestDatabaseQuery::class.java)

    lateinit var mockDic : Dictionary
    lateinit var query : DatabaseQuery
    @BeforeEach
    fun beforeEach(){
        val mockDb = mock<Database>{}
        val mockConf = mock<ConfigDef>{}
        val mockDic = mock<Dictionary>{
            // テストでは、テスト結果をわかりやすくするため、L2Pは物理名に変換せず、論理名をそのまま返す
            on {L2P(any())} doAnswer {it.arguments[0] as String}
        }
        query = DatabaseQuery(mockDb,mockDic,mockConf)
    }

    @Test
    fun testSimple() {
        var app = query.queryCriteria()
        app.put("PK1", "abc")
        app.put("PK1", null)
        app.put("PK1", "def")
        app.put("PK1", "abc")
        log.info("testSimple: condition=[$app]")
        assertThat(app.getSQLCondition("PK1")).isEqualTo("PK1 IN ('abc',NULL,'def')")
    }

    @Test
    fun testDifferentDataType() {
        var app = query.queryCriteria()
        app.put("PK1", "abc")
        app.put("PK1", null)
        assertThat{ app.put("PK1", 123) }.isFailure()
    }

    @Test
    fun testMultiplePrimaryKeys() {
        var app = query.queryCriteria()
        listOf("PK1" to "abc", "PK1" to null, "PK1" to "def", "PK1" to "abc",
               "PK2" to 123, "PK2" to 456, "PK2" to 789, "PK2" to 12)
        .forEach { app.put(it.first,it.second) }
        log.info("testMultiplePrimaryKeys: condition=[$app]")

        assertThat(app.getSQLCondition("PK1+PK2")).isEqualTo(
        "(PK1 = 'abc' AND PK2 = 123) OR (1 = 1 AND PK2 = 456) OR (PK1 = 'def' AND PK2 = 789) OR (PK1 = 'abc' AND PK2 = 12)"
        )
    }

    @Test
    fun testDifferentSize() {
        var app = query.queryCriteria()
        listOf("PK1" to "abc", "PK1" to null, "PK1" to "def", "PK1" to "abc",
               "PK2" to 123, "PK2" to 456, "PK2" to 789)
        .forEach { app.put(it.first,it.second) }
        log.info("testDifferentSize: condition=[$app]")

        assertThat(app.getSQLCondition("PK1+PK2")).isEqualTo(
        "(PK1 = 'abc' AND PK2 = 123) OR (1 = 1 AND PK2 = 456) OR (PK1 = 'def' AND PK2 = 789) OR (PK1 = 'abc' AND 1 = 1)"
        )
    }

    @Test
    fun testMultipleNullKeys() {
        var app = query.queryCriteria()
        log.info("testMultipleNullKeys: condition=[$app]")

        assertThat(app.getSQLCondition("PK1+PK2")).isEqualTo(
        "1 = 2"
        )
    }
    @Test
    fun testNullKey() {
        var app = query.queryCriteria()
        log.info("testNullKey: condition=[$app]")

        assertThat(app.getSQLCondition("PK1")).isEqualTo(
        "1 = 2"
        )
    }
    @Test
    fun testBlankParameter() {
        var app = query.queryCriteria()
        log.info("testBlankParameter: condition=[$app]")

        assertThat(app.getSQLCondition("")).isEqualTo(
        " 1=1 "
        )
    }

    @Test
    fun testPutMultiple() {
        var app = query.queryCriteria()
        listOf("12345-678", "12345", "12345-000")
        .forEach { app.putMultiple("PK1+PK2",it,"-") }        
        log.info("testBlankParameter: condition=[$app]")

        assertThat(app.getSQLCondition("PK1+PK2")).isEqualTo(
        "(PK1 = '12345' AND PK2 = '678') OR (PK1 = '12345' AND 1 = 1) OR (PK1 = '12345' AND PK2 = '000')"
        )
    }


}
