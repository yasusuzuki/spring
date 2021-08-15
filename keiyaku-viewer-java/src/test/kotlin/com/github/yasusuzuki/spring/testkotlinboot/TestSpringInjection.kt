package com.github.yasusuzuki.spring.testkotlinboot


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

import org.junit.jupiter.api.Test
import assertk.assertThat
import assertk.assertions.*

@SpringBootTest
class TestSpringInjection {
    @Autowired
    lateinit var conf : ConfigDef
    @Autowired
    lateinit var dic : Dictionary

    @Test
    public fun testSetCurrentEnvironment(){
        println("Autowired component ConfigDef $conf")
        println("Autowired component Dictionary $dic")
        assertThat(conf).isNotNull()
        assertThat(dic).isNotNull()
        assertThat(dic.config).isNotNull()
    }

}
