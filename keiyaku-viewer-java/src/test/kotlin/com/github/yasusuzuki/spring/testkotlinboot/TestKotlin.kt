package com.github.yasusuzuki.spring.testkotlinboot

import org.junit.jupiter.api.Test

import assertk.assertThat
import assertk.assertions.*
import kotlin.assert

class TestKotlin {
    data class MVD (
        var x:Int=1,
        var y:Int=2,
        var z:Int=3
    )

    fun destructingReturn():Triple<Int,Int,String>{
        return Triple( 100,200,"ABC")
    }

    @Test
    fun testLambda() {
        var num = 2
        var a = {println("OK")}  //Lambda with no parameters
        //var b = {x -> x*x}     //Labmda with 1 parameter. Compile Error: because type cannot be inferred       
        var b = num.let{ x -> x*x }  //Lambda with 1 parameter. type can be inferred
        var c = {x:Int -> x*x}
        //var d = { (x,y,z) -> listOf(x*x,y*y,z*z)}
        //var e ={  (x:Int,y:Int,z:Int)  ->  listOf(x*x,y*y,z*z)}  // () is not required
        var f ={ x:Int,y:Int,z:Int  -> listOf(x*x,y*y,z*z)}
        var g ={ (x,y,z):MVD  -> listOf(x,y,z)}
        //var h ={ x,y,z -> listOf<Int>(x,y,z)}
        var h: (Int,Int,Int)->List<Int> = { x,y,z -> listOf<Int>(x,y,z)}

        
        assertThat(b).isEqualTo(4)
        assertThat(c(2)).isEqualTo(4)
        assertThat(f(1,2,3)).containsExactly(1,4,9)
        assertThat(g(MVD(2,3,4))).containsExactly(2,3,4)
        assertThat(h(2,3,4)).containsExactly(2,3,4)
    }

    fun testDestructingDeclaration(){
        var (a,b,c) = listOf(1,2,3)
        println("$a $b $c")
        var (aa,bb,cc) = destructingReturn()
        println("$aa $bb $cc")
    }
}

