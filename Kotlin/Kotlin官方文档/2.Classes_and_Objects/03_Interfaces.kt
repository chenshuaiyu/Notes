package Classes_and_Objects

/**
 * Coder : chenshuaiyu
 * Time : 2018/7/26 21:18
 */

//Kotlin的接口和Java8非常相似。
//可以包含抽象方法的声明以及实现。与抽象类的不同是接口无法存储状态。
//可以具有属性，但这些属性是抽象的或需要提供访问器实现。

//用关键字 interface 声明
interface MyInterface {

    fun bar()
    fun foo() {
        // optional body
    }
}

//实现接口
class Child : MyInterface {
    override fun bar() {
        // body
    }
}

interface MyInterface1 {
    //属性要么抽象，要么提供访问器实现
    val prop: Int // 抽象

    //接口中的属性没有field，所以在接口中声明的访问器不能引用field
    val propertyWithImplementation: String
        get() = "foo"

    fun foo() {
        print(prop)
    }
}

class Child1 : MyInterface1 {
    override val prop: Int = 29
}

//接口继承

interface Named {
    val name: String
}

interface Person2 : Named {
    val firstName: String
    val lastName: String

    override val name: String get() = "$firstName $lastName"
}

data class Employee(
        // implementing 'name' is not required
        override val firstName: String,
        override val lastName: String
) : Person2


//解决复写矛盾
interface A1 {
    fun foo() {
        print("A")
    }

    fun bar()
}

interface B1 {
    fun foo() {
        print("B")
    }

    fun bar() {
        print("bar")
    }
}

class C1 : A1 {
    //必须实现bar方法
    override fun bar() {
        print("bar")
    }
}

class D1 : A1, B1 {
    //必须手动复写foo和bar方法
    override fun foo() {
        super<A1>.foo()
        super<B1>.foo()
    }

    override fun bar() {
        super<B1>.bar()
    }
}