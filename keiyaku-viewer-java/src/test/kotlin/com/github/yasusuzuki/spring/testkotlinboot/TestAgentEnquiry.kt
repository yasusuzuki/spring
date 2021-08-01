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
}

