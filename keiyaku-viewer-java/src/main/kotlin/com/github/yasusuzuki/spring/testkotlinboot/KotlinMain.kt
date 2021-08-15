package com.github.yasusuzuki.spring.testkotlinboot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KotlinMain

fun main(args: Array<String>) {
	runApplication<KotlinMain>(*args)
}
