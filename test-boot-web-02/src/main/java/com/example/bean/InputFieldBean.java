package com.example.bean;

import org.springframework.stereotype.Component;

@Component
public class InputFieldBean {
	
	public String getHtml(){
		return "<div class='ui labeled input'><div class='ui label'>証券番号 </div>"
				+ "<input type='text' th:field='*{myMap[policyNo]}'  placeholder='SK0000001'></input>"
				+ "</div>";
		
	}
	public String html(String id,String name,String placeholder){
		return "<div class='ui labeled input'><div class='ui label'>"+name+"</div>"
				+ "<input type='text' th:field='*{myMap["+id+"]}'  placeholder='" + placeholder + "'></input>"
				+ "</div>";
		
	}

}
