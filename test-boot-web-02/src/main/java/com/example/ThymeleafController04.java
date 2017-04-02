package com.example;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.form.KeyValueForm;
import com.example.model.TinyProduct;

@Controller
public class ThymeleafController04 {

	@Autowired JdbcTemplate jdbc;


	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}

	
	@RequestMapping(value = "/04-enquiry-sample", method = RequestMethod.GET)
	public String getAsKeyValue(Model model) {
		List<Map<String, Object>> result = jdbc.queryForList("select * from contract");
		model.addAttribute("contracts", result);
		
		HashMap<String,Object> map = new HashMap<String,Object>();
		KeyValueForm form = new KeyValueForm();
		form.setMyMap(map);
		model.addAttribute("keyValueForm",form);
		return "simple/04-enquiry-sample";
	}
	
	@RequestMapping(value = "/04-enquiry-sample", method = RequestMethod.POST)
	public String postAsKeyValue(@ModelAttribute KeyValueForm keyValueForm, Model model) {
		System.out.println("POST"+keyValueForm.getMyMap());
		List<Map<String, Object>> result = jdbc.queryForList("select * from contract");
		
		if ( keyValueForm.getMyMap().get("policyNo") != null && ! keyValueForm.getMyMap().get("policyNo").toString().isEmpty()){
			result = result.stream().
					filter(s -> s.get("policy_no")
							.toString()
							.contains(keyValueForm.getMyMap().get("policyNo").toString() ))
					.collect(Collectors.toList());
			System.out.println(keyValueForm.getMyMap());

		}
		
		model.addAttribute("contracts", result);
		model.addAttribute("keyValueForm",keyValueForm);
		return "simple/04-enquiry-sample";
	}
}
