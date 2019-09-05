# Java创建线程的三种方式

### 1. 继承Thead创建线程类。

1. 定义Thread类的子类，并重写该类的run方法，该run方法的方法体就代表了线程要完成的任务，因此把run()方法称为执行体。
2. 创建Thread子类的实例，即创建了线程对象。
3. 调用线程对象的start()方法来启动该线程。

```java
public class FirstThreadTest extends Thread {
    int i = 0;
    
    @Override
    public void run() {
        for (; i < 100; i++) {
            System.out.println(getName() + "  " + i);
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            System.out.println(Thread.currentThread().getName() + "  : " + i);
            if (i == 20) {
                new FirstThreadTest().start();
                new FirstThreadTest().start();
            }
        }
    }
}
```

### 2. 通过Runnable接口创建线程类。

1. 定义Runnable接口的实现类，并重写该接口的run方法，该run方法的方法体同样是该线程的线程执行体。
2. 创建Runnable实现类的实例，并以此实例作为Thread的target来创建Thread对象，该Thread对象才是真正的线程对象。
3. 调用线程对象的start方法来启动该线程。

```java
public class RunnableThreadTest implements Runnable {
    private int i;

    @Override
    public void run() {
        for (i = 0; i < 100; i++) {
            System.out.println(Thread.currentThread().getName() + " " + i);
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            System.out.println(Thread.currentThread().getName() + " " + i);
            if (i == 20) {
                RunnableThreadTest rtt = new RunnableThreadTest();
                new Thread(rtt, "新线程1").start();
                new Thread(rtt, "新线程2").start();
            }
        }
    }
}
```

### 3. 通过Callable和Future创建线程。

1. 创建Callable接口的实现类，并实现call()方法，该call()方法将作为线程执行体，并且有返回值。
2. 创建Callable实现类的实例，使用FutureTask类来包装Callable对象，该FutureTask对象封装了该Callable对象的call()方法的返回值。
3. 使用FutureTask对象作为Thread对象的target创建并启动新线程。
4. 调用FutureTask对象的get()方法来获得子线程执行结束后的返回值，调用get()方法会阻塞线程。

```java
public class CallableThreadTest implements Callable<Integer> {

    public static void main(String[] args) {
        CallableThreadTest ctt = new CallableThreadTest();
        FutureTask<Integer> ft = new FutureTask<>(ctt);
        for (int i = 0; i < 100; i++) {
            System.out.println(Thread.currentThread().getName() + " 的循环变量i的值" + i);
            if (i == 20) {
                new Thread(ft, "有返回值的线程").start();
            }
        }
        try {
            System.out.println("子线程的返回值：" + ft.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Integer call() throws Exception {
        int i = 0;
        for (; i < 100; i++) {
            System.out.println(Thread.currentThread().getName() + " " + i);
        }
        return i;
    }
}
```

### 4. 创建线程的三种方式的对比

| 方式              | 优点                                                         | 缺点                                                         |
| ----------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| Thread            | 编写简单，如果需要访问当前线程，则无需使用Thread.currentThread()方法，直接使用this即可获得当前线程 | 线程类已经继承了Thread类，不能再继承其他父类                 |
| Runnable/Callable |                                                              | 编写复杂，如果需要访问当前线程，则必须使用Thread.currentThread()方法 |

