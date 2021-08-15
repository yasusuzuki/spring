
package com.github.yasusuzuki.spring.testkotlinboot

import javax.annotation.PostConstruct
import org.springframework.stereotype.Component

import com.fasterxml.jackson.core.JsonGenerationException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ValueNode

@Component
class Json(var dic : Dictionary) {


    fun readJsonFromString(jsonString:String) : MutableMap<MutableList<String>, String> {
        var mapper = ObjectMapper();
        var rootNode = mapper.readTree(jsonString)

        var jsonMap:MutableMap<MutableList<String>, String> = mutableMapOf()
        readJsonNodes(mutableListOf(), rootNode,jsonMap)
        return jsonMap
    }

    /**
     * jsonNodeをMap型に変更する。
     * https://stackoverflow.com/questions/48642450/how-to-iterate-all-subnodes-of-a-json-object/48645692
     */
    private fun readJsonNodes(currentPath:MutableList<String>, jsonNode:JsonNode,jsonMap : MutableMap<MutableList<String>, String>  ) {
        if (jsonNode.isObject()) {
            var objectNode = jsonNode as ObjectNode
            var iter = objectNode.fields()
            while (iter.hasNext()) {
                var entry = iter.next()
                var copiedPath = currentPath.toMutableList()
                copiedPath.add(dic.P2L(entry.key))
                readJsonNodes(copiedPath, entry.value,jsonMap)   
            }
        } else if (jsonNode.isArray()) {
            var arrayNode =  jsonNode as ArrayNode
            var iter = arrayNode.elements()
            var i = 0;
            while (iter.hasNext()) {
                var copiedPath = currentPath.toMutableList()
                copiedPath.add((i+1).toString())
                readJsonNodes(copiedPath, iter.next(),jsonMap)
                i++
            }
        } else if (jsonNode.isValueNode()) {
            var valueNode = jsonNode as ValueNode;
            jsonMap.put(currentPath, valueNode.asText());
        }
    }

    /**
     * DatabaseQuery.buildHtml()で描画できるように、Map型のデータをリスト型に変換する
     *  
     */    
    fun formatRelationalData(jsonMap:MutableMap<MutableList<String>, String> )
    :MutableMap<String,MutableList<MutableMap<String,String>>>{
        var intmediMap:MutableMap<String,MutableMap<String,String>> = mutableMapOf()
        jsonMap.forEach{ path,value -> 
            var fieldName = path.removeAt(path.lastIndex)
            var subMap = intmediMap.get(path.joinToString(">"))
            if ( subMap == null ){
                subMap = mutableMapOf()
                intmediMap.put(path.joinToString(">"),subMap)
            }
            subMap.put(fieldName,value)
        }
        var relationalMap : MutableMap<String,MutableList<MutableMap<String,String>>> = mutableMapOf()
        intmediMap.forEach{ path,subMap ->
            var tableName = path.split(">").filter{! """^\s*\d+$""".toRegex().matches(it)}.joinToString(">")
            //var tableName = path.replace(""",\s*\d+[,\]]""".toRegex(),">")
            var records = relationalMap.get(tableName)
            if ( records == null ){
                records = mutableListOf()
                relationalMap.put(tableName,records)
            }
            records.add(subMap)
        }
        return relationalMap
    }
}
