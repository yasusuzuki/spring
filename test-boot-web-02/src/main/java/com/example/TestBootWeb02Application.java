package com.example;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class TestBootWeb02Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(TestBootWeb02Application.class, args);
        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
        	if ( beanName.toLowerCase().contains("bean")){
        		System.out.println(beanName);
        	}
        }
        //GroovyBean bean = (GroovyBean)ctx.getBean("groovyBean");
        //System.out.println(bean.getName());
	}
}
