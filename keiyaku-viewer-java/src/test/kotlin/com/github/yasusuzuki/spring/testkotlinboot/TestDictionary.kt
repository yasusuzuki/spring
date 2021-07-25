package com.github.yasusuzuki.spring.testkotlinboot


import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class TestDictionary {

	@Test
	fun testLoadingDictionary() {
		var conf = ConfigDef()
		var dic = Dictionary()
		conf.dBConnections = listOf(mapOf("ENV" to "dummy env","DB_SERVER_PRODUCT" to "DB2"))
		conf.dataDictionaryFilePath = ".\\data\\data_dictionary.csv"
		conf.dBTableListFilePath = ".\\data\\db_tables.csv"
		conf.codeMasterFilePath = ".\\data\\codemaster.csv"
		conf.init()
		dic.config = conf
		dic.init()  //テストクラスからファイルを読見込ませる処理ができていないので、まだ呼ばない
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