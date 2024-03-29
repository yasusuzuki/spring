package com.github.yasusuzuki.spring.testkotlinboot

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute

import org.springframework.boot.jdbc.metadata.HikariDataSourcePoolMetadata
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
@Controller
class Monitor(var db: Database) {
    val log = LoggerFactory.getLogger(Monitor::class.java)

    data class Request(
        var ankenNumber: String = "",
    )
    
    @GetMapping("/monitor" )
    fun execute(@ModelAttribute req:Request, model:Model ): String {
        log.info("Process monitor:  req = ${req}")
        var pool : MutableMap<String, HikariDataSourcePoolMetadata> = mutableMapOf()
        db.dataSourcePool.forEach{ (k,v) -> 
            pool.put(k!!,HikariDataSourcePoolMetadata(v as HikariDataSource))
        }
        
        model["ds"] = pool
        model["appName"] = "Keiyaku Viewer"
        return "monitor"
    }
}