package Basics

/**
 * Coder : chenshuaiyu
 * Time : 2018/7/30 20:37
 */

//三个结构跳转表达式
//return
//break
//continue

//val s = person.name ?: return

//Break Continue 标签
fun jump(){
    loop@ for (i in 1..10){
        if (i == 2)
            continue@loop
        println(i)
    }
}

//Return 标签
fun foo() { //12
    listOf(1, 2, 3, 4, 5).forEach {
        if (it == 3) return
        print(it)
    }
    println("this point is unreachable")
}

fun foo1() { //1245
    listOf(1, 2, 3, 4, 5).forEach lit@{
        if (it == 3) return@lit
        print(it)
    }
    print(" done with explicit label")
}

fun foo2() { //1245
    listOf(1, 2, 3, 4, 5).forEach {
        if (it == 3) return@forEach
        print(it)
    }
    print(" done with implicit label")
}

fun foo3() { //1245
    listOf(1, 2, 3, 4, 5).forEach(fun(value: Int) {
        if (value == 3) return
        print(value)
    })
    print(" done with anonymous function")
}

fun foo4() {
    run loop@{ //12
        listOf(1, 2, 3, 4, 5).forEach {
            if (it == 3) return@loop
            print(it)
        }
    }
    print(" done with nested loop")
}

//返回值：当返回一个值时，解析器给了一个参考
//return@a 1

fun main(args: Array<String>) {
//    jump()
//    foo()
//    foo1()
//    foo2()
//    foo3()
//    foo4()
}
