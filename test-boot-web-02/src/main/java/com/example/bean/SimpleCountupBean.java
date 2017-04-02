package com.example.bean;

import org.springframework.stereotype.Component;

@Component
public class SimpleCountupBean {
	private String name = "default_name";
	private int count = 0;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCount() {
		return count;
	}
	public void increment() {
		this.count++;
	}
	
}
