package com.example;

import java.util.Arrays
import java.util.List

import org.codehaus.groovy.ant.FileScanner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.*;
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest
import org.thymeleaf.standard.expression.Each

import com.example.model.TinyProduct
import com.fasterxml.jackson.core.sym.Name

import groovy.transform.TupleConstructor
import groovy.util.AntBuilder;

import org.codehaus.groovy.runtime.StringGroovyMethods


class DataTable {
	JdbcTemplate jdbc;
	Closure cl;

	String render(String sql){
		List<Map<String,Object>> result = jdbc.queryForList(sql)
		println "DATATABLE: ${sql}"
		
		if ( result.size() == 0 ) return "" 
	
		String html = """<table class="ui celled table compact collapsing">"""
		html += "<tr>"
		result[0].each { html += "<th>" + it.key + "</th>" }  
		html += "</tr>"

		for ( row in result ) {
			if ( cl != null && ! cl(row.POLICY_NO, "POLICY_NO") ) continue;
			html += "<tr>"
			row.each{html += "<td>" + it.value + "</td>"} 
			html += "</tr>"
		}
		html += "</table>"
		return html
	}
}


@Controller
class GroovyController {
	@Autowired
	JdbcTemplate jdbc;
	DataTable dataTable
	def menu = new GroovyMenu();
	def header = new GroovyHeader();
	def request = null;

	@RequestMapping(value = "/groovy-01", method = RequestMethod.GET)
	@ResponseBody
	def html(WebRequest request) {
//		dataTable = new DataTable(jdbc,null)
		dataTable = new DataTable(jdbc:jdbc)
		this.request = request
		return body
	}
	
	@RequestMapping(value = "/groovy-01", method = RequestMethod.POST)
	@ResponseBody
	def post(WebRequest request, ModelMap map) {
		println "============================"
		
		request.getParameterMap().each{println it.value + "=" + request.getParameter(it.key)}
		
		Closure cl = { targetValue,searchKey -> targetValue =~ request.getParameter(searchKey) }
//		dataTable = new DataTable(jdbc, cl)
		this.request = request
		dataTable = new DataTable(jdbc:jdbc, cl:cl)
		return body
	}				
	
	def inputFieldMap = [
		POLICY_NO: new InputTextField('証券番号','POLICY_NO','SK001'),
		CONTRACTOR_NAME: new InputTextField('契約者氏名ｶﾅ ','CONTRACTOR_NAME','損保太郎'),
		CONTRACTOR_ADDRESS: new InputTextField('契約者住所ｶﾅ ','CONTRACTOR_ADDRESS','東京都'),
		CONTRACT_START_DATE: new InputTextField('保険始期日','CONTRACT_START_DATE','2017/4/1'),
		PRODUCT_CODE: new InputTextField('商品コード','PRODUCT_CODE','THEカラダ'),
		PRODUCT_CATEGORY: new InputDropdownField('種目','PRODUCT_CATEGORY',[1:'傷害',2:'自動車',3:'火災'])
	]
	
def body = """
<html>
${header.render()}
<body>
${menu.render()}

<!--*********************************************
**             上部ナビゲーションメニュー                                     
************************************************* -->

<div class="ui fixed menu">
  <div class="menu">
    <div id="js-sidebar" class="item"><i class="icon large grey content"></i></div>
    <h1>照会系画面のサンプル</h1>
  </div>
</div>


<!--*********************************************
**            メインのコンテンツ                                                           
************************************************* -->
<div class="pusher">
<div class=" ui fluid container">

<form method="post">
<div class="ui three column grid">
  <div class="column">
    ${inputFieldMap.POLICY_NO}
	${inputFieldMap.CONTRACTOR_NAME}
	${inputFieldMap.CONTRACTOR_ADDRESS}
  </div>
  <div class="column">
    ${inputFieldMap.CONTRACT_START_DATE}
	${inputFieldMap.PRODUCT_CODE}
	${inputFieldMap.PRODUCT_CATEGORY}
  </div>
  
  <div class="column"></div>
</div>

<p></p>
<!-- ***************************************** 
** ボタン
********************************************** -->
<button class="ui primary button">検索</button>
<button class="ui button">リセット</button>
</form>

<hr></hr>

<h2> Data Table </h2>
${-> dataTable.render("select * from contract where 1=1")}
</div>
</div>
</body>
</html>
"""
}



