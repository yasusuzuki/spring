package com.example

import java.util.Map

import groovy.transform.TupleConstructor

class InputField {
	
}

@TupleConstructor 
class InputDropdownField extends InputField{
	String name,id
	Map selection

	String toString(){
		def html = """\
		<div>
		<div class="ui selection dropdown">
		<input type="hidden" id="${id}"></input>
		<i class="dropdown icon"></i>

		<div class="default text"></div>
		<div class="menu">
		"""
		for (item in selection) {
			html += "<div class='item' value='${item.key}'>${item.value}</div>"
		}

		html += """\
		</div>
		</div>
		</div>
		"""
		return html.stripIndent()
	}
}

@TupleConstructor
class InputTextField extends InputField{
	String name,id,placeholder

	String toString(){
		"""
<div class="ui labeled input"><div class="ui label">${name}</div>
  <input type="text" name="${id}" id="${id}" placeholder="${placeholder}"></input>
</div>
"""
	}
}
