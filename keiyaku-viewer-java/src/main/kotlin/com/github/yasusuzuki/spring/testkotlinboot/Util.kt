package com.github.yasusuzuki.spring.testkotlinboot


import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

object Util {
    //DBに設定された値を対象としたrtrim()関数
    fun rtrim(s : Any?): String?{
        return if ( s == null ) {
            null  //DBにNULLが入っていることを想定
        } else if (s is String){ //<-- Smart Castsの例
            s.trimEnd()  //CHARやVARCHARを想定。特にコード値を持つCHARは右側をトリムしないと、コードマスタを検索できない
        } else {
            s.toString().trimEnd() //上記以外を想定。DATE, TIMESTAMP,NUMERICを想定
        }
    }

    fun readFileAsString(filename:String):String {
        var line = Files.lines(Paths.get(filename), Charset.forName("UTF-8"))
        .collect(Collectors.joining(System.getProperty("line.separator")))
        return line
    }
}  