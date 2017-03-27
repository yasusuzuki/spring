package com.example;

import org.codehaus.groovy.ant.FileScanner
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import groovy.util.AntBuilder;

class InputField {
	String name = "";
	String type = "";
	String toString(){
		"<input type='text'/>"

	}
} 

class DataTable {
	String sql
	String render(){
		String html = """
		<table cellspacing=0 class='DataTable'>
		"""
		html += "<tr>"
		for ( int i=0; i<10; i++ ) {
			html += "<th>"
			html += "aa"
			html += "</th>"
		}
		html += "</tr>"
		
		
		for ( int i=0; i<10; i++ ) {
			html += "<tr>"
			for ( int j=0; j<10; j++ ) {
				html += "<td>"
				html += "aa"
				html += "</td>"
			}
			html += "</tr>"
		}
		html += "</table>"
		return html
	}
		
	
}

@Component
class GroovyBean {
	String name = "aa";
	BigDecimal price;
}


@RestController
class GroovyController {
	def inputField = new InputField(name:"aa", type:"bb")
	def dataTable = new DataTable();
    @RequestMapping("/groovy-01")
    @ResponseBody
	def top() {
		return frameset
	}
	@RequestMapping("/groovy-01/menu")
	@ResponseBody
	def menu() {
		return menu
	}
	@RequestMapping("/groovy-01/body")
	@ResponseBody
	def body() {
		return body
	}


def frameset ='''
<html>
<body>

<table>
<tr><td width="10%" height="100%">
<iframe src="./groovy-01/menu" width="100%" height="100%" frameborder=0 scrolling=no></iframe>
</td>
<td width="90%" height="100%">
<iframe src="./groovy-01/body" width="100%" height="100px" frameborder=0 scrolling=no/>
</td></tr></table>

</body>
</html>
'''

def menu='''
<p>1</p>
<p>2</p>

'''

def clos = {"abdddddcdee"}

def body = """
<html>
<head>
<link rel="stylesheet" type="text/css" href="/css/Site.css"/>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/semantic-ui/2.2.6/semantic.min.js"></script>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/semantic-ui/2.2.6/semantic.min.css"  />

</head>
<body>

<form>
<p>INPUT:${inputField.toString()}</p>
<input type="submit"/>
		<p>${clos.call()}</a>

</form>
<hr size=1 noshade/>

<h2> Data Table </h2>
${dataTable.render()}

<h2> Test </h2>
<input type="textarea" height="100" width="50"/>

</body>
</html>
"""
}



