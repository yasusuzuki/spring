package com.github.yasusuzuki.spring.testkotlinboot

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import kotlin.test.assertEquals

class TestConfigDef {
    var conf = ConfigDef()

    @BeforeEach
    fun postConstruct(){
        conf.dBConnections = listOf(
            mapOf("ENV" to "dummy env1","DB_SERVER_PRODUCT" to "DB2"),
            mapOf("ENV" to "dummy env2","DB_SERVER_PRODUCT" to "ACCESS_VIA_ODBC"),
            )
        for( env in conf.dBConnections ) {
            println("Env = " + env);
            for ( (k,v) in env ) {
                println( "        " + k + "=" + v );
            }
        }
        conf.setCurrentEnvironment("dummy env1")
    }

    @Test
    public fun testSetCurrentEnvironment(){
        assertEquals("dummy env1",conf.getCurrentEnvironment(),"ENV" )
        assertEquals("DB2",conf.getCurrentDBServerProduct(),"DB_SERVER_PRODUCT" )
        conf.setCurrentEnvironment("dummy env2")
        assertEquals("dummy env2",conf.getCurrentEnvironment(),"ENV" )
        assertEquals("ACCESS_VIA_ODBC",conf.getCurrentDBServerProduct(),"DB_SERVER_PRODUCT" )
    }

}
