package Classes_and_Objects

/**
 * Coder : chenshuaiyu
 * Time : 2018/7/27 20:09
 */

//Data Classes（数据类）
data class User(val name: String, val age: Int)
//编译器自动从主构造函数中声明的所有属性派生以下成员
//equals()/hashCode()
//toString()
//componentN() 对应于其声明顺序中的属性;
//copy()

//要求：
//主构造函数需要至少有一个参数;
//主构造函数的所有参数都需要标记为val或var;
//数据类不能是abstract，open，sealed，inner;
//（1.1之前）数据类只能实现接口。

//复制：对一些属性做修改，但其他部分不变
//copy函数是这样的
//fun copy(name: String = this.name, age: Int = this.age) = User(name, age)

val jack = User(name = "jack", age = 1)
val olderJack = jack.copy(age = 2)

//数据类和多重声明
//组件函数允许数据类在多重声明中使用（类似Python，但不同）
//val jane = User(name = "jane", age = 35)
//val (name, age) = jane
//println("$name, $age years of age")

//标准数据类：标准数据库提供了 Pair 和 Triple。在大多数情况下，命名数据类更好，增强代码可读性并且提供了有意义的名字和属性。
