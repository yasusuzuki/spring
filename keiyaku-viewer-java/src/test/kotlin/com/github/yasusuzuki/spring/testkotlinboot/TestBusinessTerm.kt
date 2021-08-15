package com.github.yasusuzuki.spring.testkotlinboot

import org.junit.jupiter.api.Test

import assertk.assertThat
import assertk.assertions.*

class TestBusinessTerm {

    @Test
    fun init(){
        BusinessTerm.AGENT_CODE.logicalName = "代理店＿コード"
        var file = mapOf("AGENT_SUB_CODE" to "ダイリテンサブコード")
        BusinessTerm.values().onEach{ it.logicalName = file.getOrDefault(it.name, it.name) }

        assertThat(BusinessTerm.AGENT_CODE.logicalName).equals("代理店_コード")
        assertThat(BusinessTerm.AGENT_SUB_CODE.logicalName).equals("ダイリテンサブコード")
    }
}