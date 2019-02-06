package Classes_and_Objects

/**
 * Coder : chenshuaiyu
 * Time : 2018/7/27 15:32
 */

//类，Object，接口，构造函数，函数，属性及其setter都有可见性修饰符（getter方法作为属性时都是可见的）
//private, protected, internal, public（不显示指定，默认为public）

//包：函数，属性和类，Object和接口可以直接在包内顶层声明
//导入一个包后，才能使用此包中的可见顶级声明
private fun foo1() {} // 在此.kt文件中可见

public var bar: Int = 5 // 都可见
    private set         // 在此.kt文件中可见

internal val baz = 6    // 在此模块中可见

//类和接口：
//private: 在本类中可见
//protected：在本类中可见，在其子类中同样可见
//internal: 此模块具有的实例都可见
//public: 具有的实例都可见

//在Kotlin中，外部类不能访问到其内部类的私有成员。
//如果覆盖protected成员并且未明确指定可见性，则覆盖成员也是protected

open class Outer {
    private val a = 1
    protected open val b = 2
    internal val c = 3
    val d = 4  // 默认是 public

    protected class Nested {
        public val e: Int = 5
    }
}

class Subclass : Outer() {
    // a 不可见
    // b, c, d 可见
    // Nested , e 可见

    override val b = 5   // b 仍为protected
}

class Unrelated(o: Outer) {
    // o.a, o.b 不可见
    // o.c, o.d 在同一模块中可见
    // Outer.Nested 不可见, Nested::e 同样不可见
}

//构造函数（默认为public）：添加可见性时，需要有constructor关键字
//局部变量，函数和类不能具有可见性修饰

//模块（一组编译在一起的Kotlin文件）：internal 表示该成员在同一模块中可见。