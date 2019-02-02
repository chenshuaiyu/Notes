package Basics

/**
 * Coder : chenshuaiyu
 * Time : 2018/7/26 13:45
 */

/*
Numbers:

Type    Bit width

Double  64
Float   32
Long    64
Int     32
Short   16
Byte    8


Literal Constants(字面值常量):
Decimals:      123
Longs:         123L
Hexadecimals:  0x0F
Binaries:      0b00001011

注意：不支持 8 进制字面值常量

支持浮点数的传统表示法：
Double:  123.5,123.5e10
Float:   123.5f 或 123.5F

numeric literals（数字文字）支持下划线
val oneMillion = 1_000_000
val creditCardNumber = 1234_5678_9012_3456L
val socialSecurityNumber = 999_99_9999L
val hexBytes = 0xFF_EC_DE_5E
val bytes = 0b11010010_01101001_10010100_10010010

*/
/**
 * 在Java平台上，numbers(数值)以字节码的形式物理存储在JVM，除了可空数值（Int?）或涉及泛型，后者需要装箱
 */
fun representation() {
    val a: Int = 10000
    println(a === a) // true
    val boxedA: Int? = a
    val anotherBoxedA: Int? = a
    println(boxedA == anotherBoxedA) // true
    println(boxedA === anotherBoxedA) //true
}

/**
 * Explicit Conversions（显式转化）
 */
fun explicitConversions() {
    //不能通过编译，较小类型不能隐式转化为较大类型，需显示转化
//    val Basics.getA: Int? = 1
//    val b: Long? = Basics.getA
//    println(b == Basics.getA)

    //同样不能编译
//    val b: Byte = 1
//    val i: Int = b

    //显式转化
    val b: Byte = 1
    val i: Int = b.toInt()
    println(i)

//    toByte()
//    toShort()
//    toInt()
//    toLong()
//    toFloat()
//    toDouble()
//    toChar()

    val l = 1L + 3 // Long + Int => Long
}

/**
 * 位操作符，以中缀函数调用
 */
fun operations() {
    //支持中缀
    val x = (1 shl 2) and 0x000FF000
    println(x)

//    shl(bits) – 左移 (Java's <<)
//    shr(bits) – 右移 (Java's >>)
//    ushr(bits) – 无符号右移 (Java's >>>)
//    and(bits) – 与 (Java's &)
//    or(bits) – 或 (Java's |)
//    xor(bits) – 异或 (Java's ^)
//    inv() – 取反 (Java's ~)

}

/**
 * 字符，用单括号表示，转义字符加\，
 */
fun characters() {
    //可以将字符转化为数字
    val c: Char = 'c'
    val cInt: Int = c.toInt()//显示转换
    println(cInt)
}

//Boolean(布尔值) 可以使用 && || !

/**
 * Arrays(数组)
 */
//Arrays 源代码
//class Array<T> private constructor() {
//    val size: Int
//    operator fun get(index: Int): T
//    operator fun set(index: Int, value: T): Unit
//
//    operator fun iterator(): Iterator<T>
//    // ...
//}

fun arrays() {
    //可以通过 set() 和 get() 操作元素
    val a1: Array<Int> = arrayOf(1, 2, 3) // [1,2,3]
    val a2 = arrayOfNulls<Int>(3) // [null,null,null]
    val a3 = Array(5, { i -> (i * i).toString() })
    a1.forEach { println(it) }
    a2.forEach { println(it) }
    a3.forEach { println(it) }
}

fun arrays1() {
    //原始类型的数组，没有装箱开销 ByteArray，ShortArray，IntArray
    val x: IntArray = intArrayOf(1, 2, 3)
    x[0] = x[1] + x[2]
}

/**
 * String(字符串)，不可变，通过索引访问，可以使 for 迭代，可以使用 + 操作
 */
fun string() {
    val str = "abcd"
    for (s in str) {
        println(s)
    }

    val s = "abs" + 1
    println(s)
}


/**
 * String Literals: 包括转义字符和原始字符串(""")
 */
fun stringLiterals() {
    val s = "Hello, world!\n"
    val text = """
    for (c in "foo")
        Basics.print(c)
""".trimMargin()
}

/**
 * String Templates
 */
fun stringTemplates() {
    val i = 10
    println("i = $i") // prints "i = 10"
    val s = "abc"
    println("$s.length is ${s.length}") // prints "abc.length is 3"
    val price = """
${'$'}9.99
"""
    println(price)
}


fun main(args: Array<String>) {
//    Basics.representation()
//    Basics.operations()
//    Basics.characters()
//    Basics.arrays()
//    Basics.arrays1()
//    stringTemplates()
}
