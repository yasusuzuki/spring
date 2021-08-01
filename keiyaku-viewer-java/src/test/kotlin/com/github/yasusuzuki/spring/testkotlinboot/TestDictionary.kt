package com.github.yasusuzuki.spring.testkotlinboot


import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Assertions.assertEquals

import assertk.assertThat
import assertk.assertions.*

class TestDictionary {

	
	fun initDictionaryFromCSVFiles():Dictionary {
		var conf = ConfigDef()
		var dic = Dictionary()
		conf.dBConnections = listOf(mapOf("ENV" to "dummy env","DB_SERVER_PRODUCT" to "DB2"))
		conf.dataDictionaryFilePath = ".\\data\\data_dictionary.csv"
		conf.dBTableListFilePath = ".\\data\\db_tables.csv"
		conf.codeMasterFilePath = ".\\data\\codemaster.csv"
		conf.init()
		dic.config = conf
		dic.init() 
		return dic
	}

	@Test
	fun test_DBテーブル定義がロードできたか(){
		var dic = initDictionaryFromCSVFiles()
		//println("＊　団体テーブル\n %s".format(dic.listDBTables("団体")))
		//println("＊　契約テーブル\n %s".format(dic.listDBTables("契約")))
		//println("＊　提案テーブル\n %s".format(dic.listDBTables("提案")))

		assertThat(dic.listDBTables("契約")).contains(Dictionary.TableDefinition("契約","保険契約","INPUT"))
		assertThat(dic.L2P("保険契約")).isEqualTo("TB000570")
		assertThat(dic.listDBTables("提案")).contains(Dictionary.TableDefinition("提案","提案","INPUT"))
		assertThat(dic.L2P("提案")).isEqualTo("TB001340")
		assertThat(dic.listDBTables("団体")).contains(Dictionary.TableDefinition("団体","集金契約基本","集金契約書集約＿コード"))
		assertThat(dic.L2P("団体基本")).isEqualTo("TB015450")

		println("P2L() insuranceContractAgentFreedomMentionFieldKubunCd=" + dic.P2L("insuranceContractAgentFreedomMentionFieldKubunCd"))
		assert(dic.P2L("insuranceContractAgentFreedomMentionFieldKubunCd") != "")
	}

	fun initDictionaryMock() : Dictionary {
		var conf = ConfigDef()
		var dic = Dictionary()
		conf.dBConnections = listOf(mapOf("ENV" to "dummy env","DB_SERVER_PRODUCT" to "DB2"))
		conf.init()
		dic.CodeMaster = linkedMapOf(
			"domainA" to mutableListOf( 
					Dictionary.CodeValueNamePair("1","name1"), 
					Dictionary.CodeValueNamePair("2","name2"), 
					Dictionary.CodeValueNamePair("3","name3"), 
			),
			"domainB" to mutableListOf( 
				Dictionary.CodeValueNamePair("1","name1"), 
				Dictionary.CodeValueNamePair("2","name2"), 
				Dictionary.CodeValueNamePair("3","name3"), 
			)
		)
		dic.L2PDictionary = linkedMapOf(
			"項目１" to  Dictionary.DomainPhysicalNamePair("domainA","field1","FIELD_1"),
			"テーブル１" to Dictionary.DomainPhysicalNamePair("---","---","TableX"),
		)
		dic.config = conf
		return dic
	}


	@Test
	fun testDictionary() {
		var dic = initDictionaryMock()
		println("findDomain() 項目１=" + dic.lookupDomain("項目１"))
		assertEquals("domainA", dic.lookupDomain("項目１"))

		//未知の項目に対して、findDomain()はブランクを返却する
		println("findDomain() 未知の項目=" + dic.lookupDomain("未知の項目"))
		assertEquals("", dic.lookupDomain("未知の項目"), "error ")

		println("L2P() 項目１=" + dic.L2P("項目１"))
		assertEquals("FIELD_1", dic.L2P("項目１"), "error ")

		//未知の項目に対して、L2Pはそのまま返却する
		println("L2P() 未知の項目=" + dic.L2P("未知の項目"))
		assertEquals("未知の項目", dic.L2P("未知の項目"), "error ")


		val sql = "SELECT * FROM テーブル１ a WHERE a.項目１ LIKE ’%0004'="
		println("\nL2P_SQL() " + sql + " ---> " + dic.L2P_SQL(sql))
		assertEquals("SELECT * FROM TableX a WHERE a.FIELD_1 LIKE ’%0004'=", dic.L2P_SQL(sql), "error ")

	}
}