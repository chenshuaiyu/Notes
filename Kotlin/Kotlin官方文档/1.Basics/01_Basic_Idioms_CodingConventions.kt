package Basics


/**
 * Coder : chenshuaiyu
 * Time : 2018/7/25 15:48
 */

//在源文件开头定义包名
//package my_demo
//import java.util.*

/**
 * 接受两个Int类型参数，返回值为Int
 */
fun sum(a: Int, b: Int): Int {
    return a + b
}

/**
 * 表示式作为函数体，返回值自动类型推断
 */
fun sum1(a: Int, b: Int) = a + b

/**
 * 返回一个没有意义的值
 */
fun printSum(a: Int, b: Int): Unit {
    println("Basics.sum of $a and $b is ${a + b}")
}

/**
 * Unit 的返回类型可以省略
 */
fun printSum1(a: Int, b: Int) {
    println("Basics.sum of $a and $b is ${a + b}")
}

//定义局部变量

//声明常量，一次赋值
//val Basics.getA: Int = 1//立即初始化
//val b = 2//推断出Int类型

//可变变量
//var t = 5 // 自动推断类型
//t += 1


//单行注释
/*块注释*/


//使用字符串模板
var a = 1
//变量名作为模板
val s1 = "Basics.getA is $a"
//Basics.getA = 2
//表达式作为模板
val s2 = "${s1.replace("is", "was")}, but now is $a"


//条件表达式
fun maxOf(a: Int, b: Int): Int {
    if (a > b) {
        return a
    } else {
        return b
    }
}

//把if当成表达式
fun maxOf1(a: Int, b: Int) = if (a > b) a else b


//使用可空变量以及空值检查
/**
 * 空值可能出现时明确指出该引用可空
 * 当某个值为空的时候，必须在声明类型后加?，来表示该引用可以为null
 */
fun parseInt(str: String): Int? {
    return str.toIntOrNull()
}

//使用可返回空值的函数
fun print(arg1: String, arg2: String) {
    val x = parseInt(arg1)
    val y = parseInt(arg2)
    if (x != null && y != null) {
//        进行空值检查后，可以直接使用
        println(x * y)
    } else {
        println("either '$arg1' or '$arg2' is not Basics.getA number")
    }
}

fun print1(arg1: String, arg2: String) {
    val x = parseInt(arg1)
    val y = parseInt(arg2)
    if (x == null) {
        println("wrong number format: '$arg1'")
        return
    }
    if (y == null) {
        println("wrong number format: '$arg2'")
        return
    }
    println(x * y)
}

//使用类型检查并自动类型转换
/**
 * is 可以检测是否为某一类型的实例，类似Java中的instanceof
 */
fun getStringLength(obj: Any): Int? {
    if (obj is String) {
        //obj这个条件分支已自动转换为String类型
        return obj.length
    }
    return null
}

fun getStringLength1(obj: Any): Int? {
    if (obj !is String)
        return null
    //在这一分支自动转换为String
    return obj.length
}

fun getStringLength2(obj: Any): Int? {
    //obj在判断后，自动转换为String
    if (obj is String && obj.length > 0) {
        return obj.length
    }
    return null
}

/**
 * for循环
 */
fun forTest() {
    //listOf只能返回只读的集合，需要直接传入参数初始化
    val items = listOf("China", "America", "Japan")
    for (item in items) {
        println(item)
    }
    //通过索引
    for (index in items.indices) {
        println("the $index is ${items[index]}")
    }
}

/**
 * while循环，也有do-while循环
 */
fun whileTest() {
    val items = listOf("China", "America", "Japan")
    var index: Int = 0
    while (index < items.size) {
        println("the $index is ${items[index]}")
        index++
    }
}

//循环同样支持break,continue

/**
 * when 表达式
 */
fun whenTest(obj: Any): String =
        when (obj) {
            1 -> "One"
            //可以有多个选择
            2, 3 -> "Two or Three"
            "Hello" -> "Greeting"
            is Long -> "Long"
            !is String -> "Not Basics.getA Basics.string"
            else -> "Unknown"
        }

/**
 * 区间（Range）
 */
fun rangeTest() {
    val x = 10
    val y = 9
    if (x in 1..y + 1) {
        println("fits in the range")
    }
}

fun rangeTest1() {
    val list = listOf("Basics.getA", "b", "c")
    if (-1 !in 0..list.lastIndex) {
        println("-1 is out of range")
    }
    if (list.size !in list.indices) {
        println("list size is out of valid list indices range too")
    }
}

/**
 * 区间迭代
 */
fun rangeTest2() {
    for (x in 1..5)
        println(x)
}

/**
 * 数列迭代
 */
fun rangeTest3() {
    for (x in 1..10 step 2)
        println(x)
    println()
    for (x in 9 downTo 0 step 3)
        println(x)
}


//集合
fun collectionsTest() {
    val items = listOf("apple", "banana", "kiwifruit")
    for (item in items)
        println(item)
}

fun collectionsTest1() {
    val items = listOf("apple", "banana", "kiwifruit")
    //使用 in 来判断集合是否包含某实例
    when {
        "orange" in items -> println("juicy")
        "apple" in items -> println("apple is fine too")
    }
}

/**
 * 使用 Lambda 表达式来 filter 过滤和 map 映射集合
 */
fun collectionsTest2() {
    val fruits = listOf("apple", "banana", "kiwifruit")
    fruits.filter { it.startsWith("Basics.getA") }
            .sortedBy { it }
            .map { it.toUpperCase() }
            .forEach { println(it) }
}

//创建基本类的实例，不需要 new 关键字
//val rectangle = Rectangle(5.0, 2.0)
//val triangle = Triangle(3.0, 4.0, 5.0)


//习惯用语

//创建数据类
data class Customer(val name: String, val email: String)
//给Customer类提供方法：所有属性的getters，
// 如果为var，同时添加setters
// equals(),hashcode(),toString(),
// copy(),component1(),component2()

//函数默认值
fun foo(a: Int = 0, b: String = "") {}

//过滤list
//val positives = list.filter { x -> x > 0 }
//或者更短
//val positives = list.filter { it > 0 }

//字符串插值
//println("name $name")

//实例检查
//when (x){
//    is Foo -> ...
//    is Bar -> ...
//    else -> ...
//}

//遍历map/list
//for ((k,v) in map){ // k,v可以随便命名
//    println("$k -> $v")
//}

//使用ranges
//for (i in 1..100) { ... }  // 左右闭
//for (i in 1 until 100) { ... } // 左闭右开
//for (x in 2..10 step 2) { ... }
//for (x in 10 downTo 1) { ... }
//if (x in 1..10) { ... }

//只读list
val list = listOf("a", "b", "c")

//只读map
val map = mapOf("a" to 1, "b" to 2, "c" to 3)

//访问map
//println(map["key"])
//map["key"] = value

//懒属性（延迟加载）
//val p: String by lazy {
//
//}

//扩展函数
fun String.spcaceToCamelCase() {

}

//创建单例模式
object Resource {
    val name = "Name"
}

//如果不为空则...的简写
//val files = File("Test").listFiles()
//println(files?.size)

//如果不为空...否则...的简写
//val files = File("test").listFiles()
//println(files?.size ?: "empty")

//如果声明为空执行某操作
//val data = ...
//val email = data["email"] ?:throw IllegalStateException("Email is missing")

//如果不为空执行某操作
//val data = ...
//data?.let{
//    //如果不为空执行该语句块
//}

//返回when判断
fun transform(color: String): Int {
    return when (color) {
        "Red" -> 0
        "Green" -> 1
        "Blue" -> 2
        else -> throw IllegalArgumentException("Invalid color param value")
    }
}

//try-catch表达式
//fun test() {
//    val result = try {
//        count()
//    } catch (e: ArithmeticException) {
//        throw IllegalStateException(e)
//    }
//    //处理result
//}

//if表达式
fun foo(param: Int) {
    val result = if (param == 1) {
        "one"
    } else if (param == 2) {
        "two"
    } else {
        "three"
    }
}

//方法使用生成器模式返回Unit
fun arrOfMinusOnes(size: Int): IntArray {
    return IntArray(size).apply { fill(-1) }
}

//只有一个表达式的函数
fun theAnswer() = 42
//等效于
//fun theAnswer(): Int {
//    return 42
//}

//利用with调用一个对象实例的多个方法
//class Turtle {
//    fun penDown() {}
//    fun penUp() {}
//    fun turn(degrees: Double) {}
//    fun forward(pixels: Double) {}
//}
//val myTurtle = Turtle()
//with(myTurtle) { //draw a 100 pix square
//    penDown()
//    for(i in 1..4) {
//        forward(100.0)
//        turn(90.0)
//    }
//    penUp()
//}

//Java 7's try with resources
//val stream = Files.newInputStream(Paths.get("/some/file.txt"))
//stream.buffered().reader().use { reader ->
//    println(reader.readText())
//}

//方便的通用函数形式，需要泛型类型信息
//  public final class Gson {
//     ...
//     public <T> T fromJson(JsonElement json, Class<T> classOfT) throws JsonSyntaxException {
//     ...
//inline fun <reified T : Any> Gson.fromJson(json: JsonElement): T = this.fromJson(json, T::class.java)

//使用可以为空的布尔值
//val b: Boolean? = ...
//if (b == true) {
//    ...
//} else {
//    // `b` is false or null
//}

//代码风格

//命名风格
//1.使用骆驼命名法（在命名中避免下划线）
//2.类型名称首字母大写
//3.方法和属性首字母大写
//4.缩进用四个空格
//5.public 方法要写说明文档，这样它就可以出现在Kotlin Doc中

//冒号：在冒号区分类型和父类型中要有空格，在实例和类型之间是没有空格的

//Lambda：大括号和表达式之间要有空格，箭头和参数之间要有空格。尽可能的把lambda放在外面传入
//建议使用it，而不是申明参数。

//Unit：返回时应该省略

fun main(args: Array<String>) {
//    println(Basics.sum(1, 2))
//    println("Basics.sum of 1 and 2 is ${Basics.sum(1, 2)}")
//    Basics.printSum(1, 2)
//    Basics.printSum1(1, 2)
////    println("Basics.getA = $Basics.getA, b = $b")
//    println(Basics.getS1 + Basics.getS2)
//    println(Basics.maxOf(1, 2))
//    println(Basics.parseInt("Basics.getA"))
//    Basics.forTest()
//    Basics.whileTest()
//    println(Basics.whenTest(1))
//    Basics.rangeTest()
//    Basics.rangeTest1()
//    Basics.rangeTest2()
//    Basics.rangeTest3()
//    Basics.collectionsTest()
//    Basics.collectionsTest1()
//    Basics.collectionsTest2()
}