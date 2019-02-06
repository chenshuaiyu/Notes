package Classes_and_Objects

/**
 * Coder : chenshuaiyu
 * Time : 2018/7/27 16:05
 */

//Extensions（拓展）

//拓展函数：要声明扩展函数，我们需要在其名称前加上接收器类型，即扩展类型。
//扩展函数内的this关键字对应于接收者对象。
fun MutableList<Int>.swap(index1: Int, index2: Int) {
    val tmp = this[index1] // this 代表 list
    this[index1] = this[index2]
    this[index2] = tmp
}

val l = mutableListOf(1, 2, 3)
//l.swap(0, 2)

//变为通用
fun <T> MutableList<T>.swap1(index1: Int, index2: Int) {
    val tmp = this[index1] // 'this' 对应于列表
    this[index1] = this[index2]
    this[index2] = tmp
}

//通过静态解决拓展
//扩展实际上并不修改它们扩展的类。通过定义扩展，不会将新成员插入到类中，而只是使用此类型的变量上的点符号使新函数可调用。

//open class C
//class D: C()
//fun C.foo() = "c"
//fun D.foo() = "d"
//fun printFoo(c: C) {
//    println(c.foo())
//}
//printFoo(D()) //结果是 c

//class C {
//    fun foo() { println("member") }
//}
//fun C.foo() { println("extension") }
//
//c.foo()//结果是 member

//class C {
//    fun foo() { println("member") }
//}
//fun C.foo(i: Int) { println("extension") }
//C().foo(1)//结果是 extension

//可空接收者

//可以使用可空接收器类型定义扩展。
//即使其值为null，也可以在对象变量上调用此类扩展，并且可以在正文内检查 this == null。检查发生在扩展函数内部。
fun Any?.toString(): String {
    if (this == null) return "null"
    // after the null check, 'this' is autocast to a non-null type, so the toString() below
    // resolves to the member function of the Any class
    return toString()
}

//拓展属性
val <T> List<T>.lastIndex: Int
    get() = size - 1

//由于扩展并不会给类添加成员属性，因此扩展属性没有方法来获得field。
//这就是 初始化函数不允许有扩展属性 的原因。他们的行为只能通过明确提供getter / setter来定义。
//val Foo.bar = 1 //编译失败,扩展属性不允许设定初始值

//伴随对象拓展

//如果某个类定义了一个伴随Object，还可以为该伴随Object定义扩展函数和属性
class MyClass {
    companion object { }
}
fun MyClass.Companion.foo() {  }

//和普通伴随对象一样，只能用类的名字就调用
//MyClass.foo()

//拓展的域

//大部分在顶级定义拓展
//package foo.bar
//fun Baz.goo() {  }

//在声明包之外使用此类扩展，需要导入
//import foo.bar.goo
//// or
//import foo.bar.*
//
//fun usage(baz: Baz) {
//    baz.goo()
//}

//将拓展声明为成员

//在类中，可以为另一个类声明扩展。在这样的扩展中，有多个隐式接收器 - 可以在没有限定符的情况下访问其成员。
//声明扩展的类的实例称为调度接收器，扩展方法的接收器类型的实例称为扩展接收器。
//class D {
//    fun bar() {  }
//}
//
//class C {
//    fun baz() {  }
//
//    fun D.foo() {
//        bar()
//        baz()
//    }
//
//    fun caller(d: D) {
//        d.foo()   // call the extension function
//    }
//}

////如果调度接收器的成员与扩展接收器之间存在名称冲突，则扩展接收器优先。要引用调度接收器的成员，可以使用限定的语法。
//class C {
//    fun D.foo() {
//        toString()         // calls D.toString()
//        this@C.toString()  // calls C.toString()
//    }
//}


//open class D { }
//
//class D1 : D() { }
//
//open class C {
//    open fun D.foo() {
//        println("D.foo in C")
//    }
//
//    open fun D1.foo() {
//        println("D1.foo in C")
//    }
//
//    fun caller(d: D) {
//        d.foo()   // call the extension function
//    }
//}
//
//class C1 : C() {
//    override fun D.foo() {
//        println("D.foo in C1")
//    }
//
//    override fun D1.foo() {
//        println("D1.foo in C1")
//    }
//}
//
//fun main(args: Array<String>) {
//    C().caller(D())   // prints "D.foo in C"
//    C1().caller(D())  // prints "D.foo in C1" - dispatch receiver is resolved virtually
//    C().caller(D1())  // prints "D.foo in C" - extension receiver is resolved statically
//}
