package Classes_and_Objects

/**
 * Coder : chenshuaiyu
 * Time : 2018/7/26 20:55
 */

//声明属性
class Declare {
    //可声明为只读或可变
    val a = 0
    var b = 0
}

//类似Java可通过实例调用属性

//声明属性的完整语法，initializer，getter和setter是可选的。
//var <propertyName>[: <PropertyType>] [= <property_initializer>]
//[<getter>]
//[<setter>]

//var allByDefault: Int? // 不能编译，需要 initializer, 默认有getter和setter
var initialized = 1 // 通过编译，有类型，默认get和set

//val simple: Int? // 不能编译，有类型，有默认getter，必须被构造函数初始化
val inferredType = 1 // 通过编译，有类型，默认getter

//自定义get,可以从get这种推断出参数类型，可省略类型
//val isEmpty: Boolean
//    get() = this.size == 0
//自定义set,参数名称默认是value,可自定义
//var stringRepresentation: String
//    get() = this.toString()
//    set(value) {
//        setDataFromString(value)
//    }

//需要更改访问者的可见性或对其进行注解，但不需要更改默认实现，则可以定义一个不带函数体的访问器：
var setterVisibility: String = "abc"
    private set // setter是私有的，具有默认实现
//var settWithAnnotation: Any? = null
//    @Inject set // 使用Inject注解setter

//备用字段不能直接在Kotlin类中声明。但是，当属性需要Fields时，Kotlin会自动提供它。
//可以使用字段标识符在访问者中引用此支持字段
var counter = 0 //初始化时会写入支持字段
    set(value) {
        //field只能在属性的访问者中使用
        if (value >= 0) field = value
    }

//field只能在属性的访问者中使用。 如果属性使用至少一个访问器的默认实现，或者自定义访问者通过field引用它，则将为该属性生成后备字段。

//将不生成支持字段
//val isEmpty: Boolean
//    get() = this.size == 0


private var _table: Map<String, Int>? = null
public val table: Map<String, Int>
    get() {
        if (_table == null) {
            _table = HashMap() // 参数类型推断
        }
        return _table ?: throw AssertionError("Set to null by another thread")
    }

//编译时常量，使用const将编译时已知的属性标记为编译时常量。
//使用const满足的要求：
//顶级或Object成员
//用String类型或基本类型初始化
//没有自定义的getter

//这些属性可以被当作注解使用
const val SUBSYSTEM_DEPRECATED: String = "This subsystem is deprecated"
@Deprecated(SUBSYSTEM_DEPRECATED) fun foo() {  }

//延迟初始化的属性和变量
//lateinit关键字和by lazy
class Test1{
    //只用于在类体内声明的var属性（不能用在构造方法中，并且仅在属性不能有自定义getter或setter），并且自Kotlin 1.2起，用于顶级属性和局部变量。
    //属性或变量的类型必须为非null，并且它不能是基本类型。

    //对于var变量可以使用lateinit关键字
    lateinit var a: String
    fun setUp(){
        a = "a"
    }

    val b: String by lazy {
        //对于val变量，可以使用by lazy方式
        "abc"
    }
}

//可通过.isInitialized判断是否初始化
//if (foo::bar.isInitialized) {
//    println(foo.bar)
//}
