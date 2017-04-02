package com.example;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.form.SimpleForm;
import com.example.model.TinyProduct;

@Controller
public class ThymeleafController02 {

	final static List<TinyProduct> CONTRACTS = Arrays.asList(
			new TinyProduct("SK0001", "損保太郎", "東京", "2017/4/1", "THEカラダ", "傷害", "100000"),
			new TinyProduct("SK0002", "鈴木一郎", "愛知", "1973/10/22", "THEカラダ", "傷害", "200000"),
			new TinyProduct("SK0003", "田中将大", "北海道", "1988/11/1", "THEカラダ", "傷害", "300000"),
			new TinyProduct("SK0004", "ダルビッシュ", "宮城", "1986/8/16", "THEカラダ", "傷害", "400000"),
			new TinyProduct("SK0005", "松坂大輔", "神奈川", "1980/9/13", "THEカラダ", "傷害", "500000"));

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}

	@RequestMapping(value = "/02-enquiry-sample", method = RequestMethod.GET)
	public String get(Model model) {
		model.addAttribute("contracts", CONTRACTS);
		model.addAttribute("simpleForm",new SimpleForm());
		return "simple/02-enquiry-sample";
	}

	@RequestMapping(value = "/02-enquiry-sample", method = RequestMethod.POST)
	public String post(@ModelAttribute SimpleForm simpleForm, Model model) {
		System.out.println("POST");
		System.out.println(simpleForm);
		List<TinyProduct> contracts = CONTRACTS;
		if ( simpleForm.getPolicyNo() != null && ! simpleForm.getPolicyNo().isEmpty()){
			contracts = CONTRACTS.stream().
					filter(s -> s.getPolicyNo().
							contains(simpleForm.getPolicyNo()))
					.collect(Collectors.toList());
		}
		model.addAttribute("contracts", contracts);
		model.addAttribute("simpleForm",simpleForm);
		return "simple/02-enquiry-sample";
	}

}
