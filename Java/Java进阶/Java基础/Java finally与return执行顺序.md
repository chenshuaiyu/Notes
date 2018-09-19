# Java finally与return执行顺序

至少有两种情况下finally语句不会被执行到：

1. try语句没有被执行到。
2. 在try块中`System.exit(0);`这样的语句，`System.exit(0);`是终止Java虚拟机JVM的，连JVM都停止了， 所有都结束了。

### 1.finally语句在return语句执行之后return返回之前执行的

```java
public class Test {
    public static void main(String[] args) {
        System.out.println(test1());
    }

    private static int test1() {
        int b = 20;
        try {
            System.out.println("try bolck");
            return b += 80;
        } catch (Exception e) {
            System.out.println("catch block");
        }finally {
            System.out.println("finally block");
            if (b > 25){
                System.out.println("b > 25, b = " + b);
            }
        }
        return b;
    }
}
```

输出：

```
try bolck
finally block
b > 25, b = 100
100
```

加强证明结论：

```java
public class Test {
    public static void main(String[] args) {
        System.out.println(test11());
    }

    private static String test11() {
        try {
            System.out.println("try bolck");
            return test12();
        } finally {
            System.out.println("finally block");
        }
    }

    private static String test12() {
        System.out.println("return statement");
        return "after return";
    }
}
```

输出：

```
try bolck
return statement
finally block
after return
```

### 2.finally块中return语句会覆盖try块中的return返回

```java
public class Test {
    public static void main(String[] args) {
        System.out.println(test1());
    }

    private static int test1() {
        int b = 20;
        try {
            System.out.println("try bolck");
            return b += 80;
        } catch (Exception e) {
            System.out.println("catch block");
        }finally {
            System.out.println("finally block");
            if (b > 25){
                System.out.println("b > 25, b = " + b);
            }
            return 200;
        }
//        return b;
    }
}
```

输出：

```
try bolck
finally block
b > 25, b = 100
200
```

finally块中的return直接返回了。

**注意**：finally外面的return b就变成不可到达语句了，也就是永远不可能被执行到，所以编译器会报错。

### 3.如果finally语句中没有return语句覆盖返回值，那么原来的返回值可能因为finally里的修改而改变也可能不变

```java
public class Test {
    public static void main(String[] args) {
        System.out.println(test3());
    }

    private static int test3() {
        int b = 20;
        try {
            System.out.println("try bolck");
            return b += 80;
        } catch (Exception e) {
            System.out.println("catch block");
        }finally {
            System.out.println("finally block");
            if (b > 25){
                System.out.println("b > 25, b = " + b);
            }
            b = 150;
        }
        return 2000;
    }
}
```

输出：

```
try bolck
finally block
b > 25, b = 100
100
```

```java
public class Test {
    public static void main(String[] args) {
        System.out.println(getMap().get("KEY").toString());
    }

    private static Map<String, String> getMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("KEY", "INIT");
        try {
            map.put("KEY", "TRY");
            return map;
        } catch (Exception e) {
            map.put("KEY", "CATCH");
        } finally {
            map.put("KEY", "FINALLY");
            map = null;
        }
        return map;
    }
}
```

输出：

```
FINALLY
```







### 4.try块里的return语句在异常的情况下不是被执行，这样具体返回哪个看情况



### 5.当异常发生后，catch中的return执行情况与未发生异常时try中return的执行情况完全一样















