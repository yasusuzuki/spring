import kotlin.reflect.KMutableProperty
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.createInstance
import kotlin.reflect.typeOf
import kotlin.reflect.KClass
import io.ktor.http.Parameters
import io.ktor.http.parametersOf

class HTTP {
    companion object {
        
        @Deprecated("legacy implementation")
        fun parametersToMap(parameters:Parameters):Map<String,List<String>>{
            var reqMap = mutableMapOf<String,List<String>>()
            parameters.forEach { key,values -> reqMap.put(key,values) }
            return reqMap
        }

        @Deprecated("legacy implementation")
        @OptIn(kotlin.ExperimentalStdlibApi::class)
        fun <T:Any> requestMapping(type: KClass<T>, obj: T,values:Map<String,List<String>>){
           var props = type.declaredMemberProperties //親クラスで宣言されたプロパティは除く。
           props.forEach { e ->
                print("${e.name}")
                if ( e is KMutableProperty<*> && values.containsKey(e.name)){
                    print(" -- KMutableProperty ${e.returnType}")
                    e.isAccessible = true
                    when ( e.returnType ) {
                        typeOf<String>()       -> e.setter.call(obj,values[e.name]!!.first())
                        typeOf<Int>()          -> e.setter.call(obj,values[e.name]!!.first().toInt())
                        typeOf<List<String>>() -> e.setter.call(obj,values[e.name])
                    }
                }
                println("")
            }
            println("AFTER: ${obj}")
        }
    
        @OptIn(kotlin.ExperimentalStdlibApi::class)
        inline fun <reified T:Any>requestMapping(values:Parameters):T {
           var obj = T::class.createInstance()
           var props = T::class.declaredMemberProperties //親クラスで宣言されたプロパティは除く。
           props.forEach { e ->
                var value = values.getAll(e.name)
                if ( e is KMutableProperty<*> && ! value.isNullOrEmpty() ){
                    println("requestMapping() -- Setting ${value} to ${e.name} of ${e.returnType}")
                    e.isAccessible = true
                    when ( e.returnType ) {
                        //String:  同名のパラメータが複数あった場合、コンマ区切りで設定する。(Sprinv MVCの動作を踏襲)
                        typeOf<String>()       -> value.joinToString(",").let{e.setter.call(obj,it)}
                        //Int: 同名のパラメータが複数設定された場合、1番目の値を設定する。
                        typeOf<Int>()          -> value.first().toIntOrNull()?.let{ e.setter.call(obj,it) }
                        //List<int>: 同名のパラメータが複数設定された場合、数値型にキャスト成功したものを設定する。
                        typeOf<List<Int>>()    -> value.map{it.toIntOrNull()}.filterNotNull().let{ e.setter.call(obj,it) }
                        typeOf<List<String>>() -> e.setter.call(obj,value)
                        else                   -> TODO("${e.returnType} -- NOT implemented yet")
                    }
                }
            }
            return obj 
        }
    
    }
}
