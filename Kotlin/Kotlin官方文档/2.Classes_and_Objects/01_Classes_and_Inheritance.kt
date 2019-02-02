package Classes_and_Objects

/**
 * Coder : chenshuaiyu
 * Time : 2018/7/26 16:02
 */

//类和继承

//类由关键字class声明,由类名，类头（指定其类型参数，主构造函数等）和类主体组成，用大括号包裹

// 类主体为空时，大括号可省略
class Empty

//类可以有一个主构造函数以及多个二级构造函数。
//主构造函数是类头的一部分：它位于类名（和可选的类型参数）之后。
class Person constructor(firstName: String) {
//class Person (firstName: String){ //可省略 constructor 关键字

}

//主构造函数不能包含任何代码。
// 初始化代码可以放到以 init 为关键字的初始化块中。

//在实例初始化时，init程序块的执行顺序与它们在类主体中出现的顺序相同，并与属性初始化程序交错：
class InitOrderDemo(name: String) {
    val firstProperty = "First property: $name".also(::println)

    init {
        println("First initializer block that prints ${name}")
    }

    val secondProperty = "Second property: ${name.length}".also(::println)

    init {
        println("Second initializer block that prints ${name.length}")
    }
}

//fun main(args: Array<String>) {
//    InitOrderDemo("chen")
//}

//结果
//First property: chen
//First initializer block that prints chen
//Second property: 4
//Second initializer block that prints 4

//主构造函数的参数可以在初始化程序块中使用。
//也可以在类体中声明的属性初始值设定项中使用：
class Customer(name: String) {
    val customerKey = name.toUpperCase()
}

//声明属性并从主构造函数初始化
class Person1(val firstName: String, val lastName: String, var age: Int) {

}

//如果构造函数具有注释或可见性修饰符，则构造函数关键字是必需的，修饰符位于其前面
//class Customer public @Inject constructor(name: String) {
//
//}

//二级构造函数
//class Person2 {
//    constructor(parent: Person) {
//        parent.children.add(this)
//    }
//}

//如果类有主构造函数，则每个二级构造函数需要直接或间接通过另一个二级构造函数委托给主构造函数。
//使用this关键字完成对同一个类的另一个构造函数的委派
//class Person3(val name: String) {
//    constructor(name: String, parent: Person) : this(name) {
//        parent.children.add(this)
//    }
//}

//init程序块中的代码有效地成为主构造函数的一部分。委托给主构造函数作为次级构造函数的第一个语句发生，因此所有init程序块中的代码在辅助构造函数体之前执行。
//即使该类没有主构造函数，委托仍然会隐式发生，并且仍然执行初始化程序块。
class Constructors {
    init {
        println("Init block")
    }

    constructor(i: Int) {
        println("Constructor")
    }
}

//如果非抽象类没有声明任何构造函数，则将生成一个没有参数的主构造函数。
//此构造函数是public。如果不希望此类具有public构造函数，则需要声明具有非默认可见性的空主构造函数
class DontCreateMe private constructor() {

}

//构造函数参数可以有默认值
//class Customer(val customerName: String = "")

//要创建类的实例，使用构造函数就和使用常规函数一样，没有 new 关键字
val person = Person("Chen")

//类成员：
// 构造函数和init代码块
// 函数
// 属性
// 嵌套内部类
// 对象声明。

class Example // 隐式继承Any

//所有类都有一个共同的超类Any，这是没有声明超类型的类的默认超类
//Any不是Java的Object类，它除了equals，hashCode和toString方法外，没有任何成员。

//继承：通过 : 继承父类
open class Base(p: Int) //open与Java的final相反，允许继承此类。默认所有类都是final

class Derived(p: Int) : Base(p)

//如果子类具有主构造函数，则可以（并且必须）使用主构造函数的参数在那里初始化父类。
//如果子类没有主构造函数，则每个次级构造函数必须使用super关键字初始化基类型，或者委托给另一个构造函数。
//注意，在这种情况下，不同的辅助构造函数可以调用基类型的不同构造函数
//class MyView : View {
//    constructor(ctx: Context) : super(ctx)
//
//    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
//}

//复写父类方法

///需要显示标注可覆盖成员和覆盖后的成员
open class Base1 {
    //加上open的函数，才能被覆盖
    open fun v() {}

    fun nv() {}
}

open class Derived1() : Base1() {
    //需要覆盖注释
    override fun v() {}

    //只能覆盖open
//    override fun nv(){ }
}

//在final类中，open成员是不允许的

open class Derived2() : Derived1() {
    //已覆盖的方法与覆盖前的方法都是open
    //要想禁止open，将其声明为final
    final override fun v() {}
}

class Test {
    //    属性是val，那么会自动生成getter方法。
//    属性是var，那么会自动生成getter和setter方法。
//    如果想在get,set中进行其他操作时，可以进行重写
    val a = 0
    var b = 0
        get() {
            println()
            return field
        }
        set(value) {
            field = value
        }
}

//复写属性

//与复写方法类似，可以使用var覆盖val，但是反之不可以。
//open class Foo {
//    open val x: Int
//        get() {
//            return x
//        }
//}

interface Foo {
    val count: Int
}

//可以在主构造函数中将override关键字用作属性声明的一部分。
class Bar1(override val count: Int) : Foo

class Bar2 : Foo {
    override val count: Int = 0
}


//派生类初始化顺序

open class Base3(val name: String) {

    init {
        println("Initializing Base")
    }

    open val size: Int =
            name.length.also { println("Initializing size in Base: $it") }
}

class Derived3(
        name: String,
        val lastName: String
) : Base3(name.capitalize().also { println("Argument for Base: $it") }) {

    init {
        println("Initializing Derived")
    }

    override val size: Int =
            (super.size + lastName.length).also { println("Initializing size in Derived: $it") }
}

//在基类构造函数执行时，尚未初始化在派生类中声明或覆盖的属性

//fun main(args: Array<String>) {
//    val d = Derived3("123", "456")
//}
//运行结果
//    Argument for Base: 123
//    Initializing Base
//    Initializing size in Base : 3
//    Initializing Derived
//    Initializing size in Derived : 6


//调用超类实现

//使用super关键字调用其超类函数和属性

open class Foo1 {
    open fun f() {
        println("Foo.f()")
    }

    open val x: Int get() = 1
}

class Bar : Foo1() {
    override fun f() {
        super.f()
        println("Bar.f()")
    }

    override val x: Int get() = super.x + 1
}

//fun main(args: Array<String>) {
//    val b = Bar()
//    b.f()
//    println(b.x)
//}
//运行结果
//    Foo.f()
//    Bar.f()
//    2


//在内部类中，使用（super@Outer）关键字来访问外部类的超类
class Bar3 : Foo1() {
    override fun f() {
    }

    override val x: Int get() = 0

    inner class Baz() {
        fun g() {
            f() //调用外部类的f()
            super@Bar3.f() //调用外部类的超类的f()
            println(super@Bar3.x)
        }
    }
}

//复写规则

//如果一个类从其直接父类继承同一成员的多个实现，则它必须覆盖此成员并提供其自己的实现（或许只是直接使用了继承来的实现）。
//为了表示使用父类中提供的方法，使用super<Base>表示
// super<A> super<B>

open class A {
    open fun f() {
        print("A")
    }

    fun a() {
        print("a")
    }
}

interface B {
    // 接口成员默认是 open
    fun f() {
        print("B")
    }

    fun b() {
        print("b")
    }
}

class C() : A(), B {
    //重写f方法，消除歧义
    override fun f() {
        super<A>.f() // call to A.f()
        super<B>.f() // call to B.f()
    }
}

//抽象类

open class Base4 {
    open fun f() {}
}

//可以使用抽象成员覆盖非抽象的open成员
abstract class Derived4 : Base4() {
    //抽象类或函数默认是open
    override abstract fun f()
}

//Companion Objects(伴随对象): 类似Java中的 static 方法
//如果需要编写一个可以在没有类实例但需要访问类的内部（例如，工厂方法）的情况下调用的函数，则可以将其编写为该类中的对象声明的成员。
//如果在类中声明一个伴随对象，将能够使用与在Java / C＃中调用静态方法相同的语法来调用其成员，只使用类名作为限定符。