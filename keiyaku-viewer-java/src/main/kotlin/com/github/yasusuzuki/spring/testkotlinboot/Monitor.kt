package com.github.yasusuzuki.spring.testkotlinboot

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute

import org.springframework.boot.jdbc.metadata.HikariDataSourcePoolMetadata
import com.zaxxer.hikari.HikariDataSource

@Controller
class Monitor {
    data class Request(
        var ankenNumber: String = "",
    )
    @Autowired
    lateinit var db: Database
    
    @GetMapping("/monitor" )
    fun execute(@ModelAttribute req:Request, model:Model ): String {
        println("Process monitor:  req = ${req}")
        var pool : MutableMap<String, HikariDataSourcePoolMetadata> = mutableMapOf()
        db.dataSourcePool.forEach{ (k,v) -> 
            pool.put(k!!,HikariDataSourcePoolMetadata(v as HikariDataSource))
        }
        
        model["ds"] = pool
        model["appName"] = "Keiyaku Viewer"
        return "monitor"
    }
}