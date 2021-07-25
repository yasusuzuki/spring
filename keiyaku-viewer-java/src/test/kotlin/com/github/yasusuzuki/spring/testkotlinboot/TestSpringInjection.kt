package com.github.yasusuzuki.spring.testkotlinboot


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import kotlin.test.assertEquals

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
    }

}
