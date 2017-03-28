package com.example;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
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
@EnableAutoConfiguration
public class ThymeleafController03 {

	final static List<TinyProduct> PRODUCTS = Arrays.asList(
			new TinyProduct("SK0001", "損保太郎", "東京", "2017/4/1", "THEカラダ", "傷害", "100000"),
			new TinyProduct("SK0002", "鈴木一郎", "愛知", "1973/10/22", "THEカラダ", "傷害", "200000"),
			new TinyProduct("SK0003", "田中将大", "北海道", "1988/11/1", "THEカラダ", "傷害", "300000"),
			new TinyProduct("SK0004", "ダルビッシュ", "宮城", "1986/8/16", "THEカラダ", "傷害", "400000"),
			new TinyProduct("SK0005", "松坂大輔", "神奈川", "1980/9/13", "THEカラダ", "傷害", "500000"));

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}

	
	@RequestMapping(value = "/03-enquiry-sample", method = RequestMethod.GET)
	public String getAsKeyValue(Model model) {
		model.addAttribute("allProducts", PRODUCTS);
		HashMap<String,Object> map = new HashMap<String,Object>();
		KeyValueForm form = new KeyValueForm();
		form.setMyMap(map);
		model.addAttribute("keyValueForm",form);
		return "simple/03-enquiry-sample";
	}
	
	@RequestMapping(value = "/03-enquiry-sample", method = RequestMethod.POST)
	public String postAsKeyValue(@ModelAttribute KeyValueForm keyValueForm, Model model) {
		System.out.println("POST");
		Object[] products = PRODUCTS.stream().
				filter(s -> s.getPolicyNo().
						contains(keyValueForm.getMyMap().get("policyNo").toString())).
				toArray();
		System.out.println(keyValueForm.getMyMap());
		model.addAttribute("allProducts", products);
		model.addAttribute("keyValueForm",keyValueForm);
		return "simple/03-enquiry-sample";
	}
}
