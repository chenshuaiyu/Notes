package Basics

/**
 * Coder : chenshuaiyu
 * Time : 2018/7/30 20:37
 */

//if表达式，即它返回一个值，没有 ?: 这种三元表达式
//传统用法
//var max = a
//if (a < b) max = b

// 使用 else
//var max: Int
//if (a > b) {
//    max = a
//} else {
//    max = b
//}

//作为表达式
//val max = if (a > b) a else b

//最后一个表达式是该块的值
//val max = if (a > b) {
//    print("Choose a")
//    a
//} else {
//    print("Choose b")
//    b
//}

//如果if表达式只有一个分支，或者分支的结果是Unit，它的值就是Unit。

//when

//when (x) {
//    0, 1 -> print("x == 0 or x == 1")
//    else -> print("otherwise")
//}

//when (x) {
//    parseInt(s) -> print("s encodes x")
//    else -> print("s does not encode x")
//}

//for while

//break continue