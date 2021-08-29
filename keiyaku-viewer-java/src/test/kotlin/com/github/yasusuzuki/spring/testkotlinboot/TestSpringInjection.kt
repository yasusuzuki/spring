package com.github.yasusuzuki.spring.testkotlinboot


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

import org.junit.jupiter.api.Test
import assertk.assertThat
import assertk.assertions.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@SpringBootTest
class TestSpringInjection {
    val log = LoggerFactory.getLogger(ConfigDef::class.java)

    @Autowired
    lateinit var conf : ConfigDef
    @Autowired
    lateinit var dic : Dictionary

    @Test
    public fun testSetCurrentEnvironment(){
        log.info("Autowired component ConfigDef $conf")
        log.info("Autowired component Dictionary $dic")
        assertThat(conf).isNotNull()
        assertThat(dic).isNotNull()
        assertThat(dic.config).isNotNull()
    }

}
