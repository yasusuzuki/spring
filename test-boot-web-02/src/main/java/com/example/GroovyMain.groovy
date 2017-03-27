package com.example
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.SpringApplication
 
@SpringBootApplication
class GroovyMain {
	@Autowired
	GroovyController controller
 
	static void main(args) {
		SpringApplication.run(GroovyMain.class, args).withCloseable {
			it.getBean(GroovyMain.class).controller.start()
		}
	}
}
