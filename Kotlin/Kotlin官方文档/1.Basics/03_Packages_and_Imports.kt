package Basics

/**
 * Coder : chenshuaiyu
 * Time : 2018/7/26 15:37
 */

//包的声明和Java是一样的

// package foo.bar
//fun baz() {}
//class Goo {}

//baz函数的全名是 foo.bar.baz

//导包

//import foo.Bar
//import foo.*
//import bar.Bar as bBar // 防止与本地出现冲突

//import关键字不限于导入类，还可以使用它来导入其他声明：顶级功能和属性，在对象声明中声明的函数和属性; 枚举常量。
//与Java不同，Kotlin没有单独的“import static”语法;所有这些声明都使用常规 import 关键字导入。

//可见性和包嵌套
//如果最顶层的声明标注为private，那么它是自己对应包私有。对子包是可见的。