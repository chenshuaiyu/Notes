# Java异常

### 1.没有捕获到异常原因：

1. 异常所在线程跟捕获的线程不是同一个线程。
2. 程序抛出的不是Exception而是Error。

原因1代码演示：

```java
try {
    new Thread() {
        @Override
        public void run() {
            int i = 1;
            i /= 0;
        }
    }.start();
} catch (Exception e) {
    e.printStackTrace();
    System.out.println("here");
}
```

### 2.链式异常

链式异常用于为异常关联另一个异常，第二个异常用于描述当前异常的产生原因。

例如：某个方法从文件读取数值来作为除数，由于发生了IO异常导致获取到的数值是0，从而导致ArithmeticException异常。如果想知道背后的原因是IO错误，使用链式异常就可以来处理这中情况。

Throwable构造函数：

```java
Throwable(Throwable cause);
Throwable(String message, Throwable cause);
```

cause是用于指定引发当前异常的背后原因。

```java
Throwable initCause(Throwable cause);
Throwable getCause();
```

```java
public static void main(String[] args) {
    try {
        test();
    } catch (Exception e) {
        System.out.println(e.getMessage());
        System.out.println(e.getCause().getMessage());
    }
}

private static void test() {
    NullPointerException n = new NullPointerException("NullPointer");
    n.initCause(new ArithmeticException("Arithmetic"));
    throw n;
}
```

运行结果：

```
NullPointer
Arithmetic
```

 链式异常可以包含所需要的任意深度，但是，过长的异常链可能是一种不良的设计。 