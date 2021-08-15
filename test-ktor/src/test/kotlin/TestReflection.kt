import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.Before
import org.mockito.Mockito

import io.ktor.http.parametersOf

class TestReflection  {

    data class Request(
        var id: String="",
        var keiyakushaName: String="",
        var maxFetchRows: Int=100,
        var keiyakuType: String="",
        var environment: String="",
        var myInts: List<Int> = listOf(),
        var myList: List<String> = listOf()
    )

    @Test
    fun test1() {
        var obj = KeiyakuList.Request()
        println("BEFORE: ${obj}")
        var parameters = parametersOf("id" to listOf("I001"),
         "keiyakushaName" to listOf("Yamada"),
         "maxFetchRows" to listOf("999"),
         "keiyakuType" to listOf("TYPE-A"),
          "myList" to listOf("a","b","c"),
          "myInts" to listOf("1","2","3"))
        var map = HTTP.parametersToMap(parameters)
        println("DEBUG: parameter id = %s".format(map.get("id")))
        println("DEBUG: parameter idXX = %s".format(map.get("idXX")))

        //genericPropertiesSetting(obj.javaClass.kotlin,obj,map )
        var req = HTTP.requestMapping<Request>(parameters )
        println("AFTER: ${req}")
    }

    @Test
    fun test2() {
        var obj = KeiyakuList.Request()
        println("BEFORE: ${obj}")
        var parameters = parametersOf("id" to listOf("I001"),
         "keiyakushaName" to listOf("Yamada"),
         "maxFetchRows" to listOf("XXX999"),  //NaN
         "keiyakuType" to listOf(),   //Empty
          "myList" to listOf(),       //Empty List
          "myInts" to listOf("1","XXX2","3"))  //NaN in List
        var req = HTTP.requestMapping<Request>(parameters )
        println("AFTER: ${req}")

    }    


    
}
