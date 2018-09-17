# Java并发

- 创建线程的方式
- Synchronized/ReentrantLock
- 生产者/消费者模式
- volatile关键字
- 乐观锁/悲观锁
- 死锁
- 了解的并发集合

# Java创建线程的三种方式

1. 继承Thead创建线程类。

   ```java
   public class FirstThreadTest extends Thread {
       int i = 0;
       
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

2. 通过Runnable接口创建线程类。

   ```java
   public class RunnableThreadTest implements Runnable {
       private int i;
   
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

3. 通过Callable和Future创建线程。

   ```java
   public class CallableThreadTest implements Callable<Integer>
   {
   
       public static void main(String[] args)
       {
           CallableThreadTest ctt = new CallableThreadTest();
           FutureTask<Integer> ft = new FutureTask<>(ctt);
           for(int i = 0;i < 100;i++)
           {
               System.out.println(Thread.currentThread().getName()+" 的循环变量i的值"+i);
               if(i==20)
               {
                   new Thread(ft,"有返回值的线程").start();
               }
           }
           try
           {
           	//通过get方法获得子线程执行结束的返回值
               System.out.println("子线程的返回值："+ft.get());
           } catch (InterruptedException e)
           {
               e.printStackTrace();
           } catch (ExecutionException e)
           {
               e.printStackTrace();
           }
   
       }
   
       @Override
       public Integer call() throws Exception
       {
           int i = 0;
           for(;i<100;i++)
           {
               System.out.println(Thread.currentThread().getName()+" "+i);
           }
           return i;
       }
   }
   ```

# Java线程池

### 一、概述

线程池的优势：

1. 降低系统资源消耗，通过重用已存在的线程，降低线程创建和销毁造成的消耗。
2. 提高系统响应速度，当有任务到达时，无需等待新线程的创建便能立即执行。
3. 方便线程并发数的管控，线程若是无限制的创建，不仅会额外消耗大量系统资源，更是占用过多资源而阻塞系统或OOM等状况，从而降低系统的稳定性。线程池能有效管控线程，统一分配、调优，提供资源利用率。
4. 更强大的功能，线程池提供了定时、定期以及可控线程数等功能的线程池，使用方便简单。

### 二、ThreadPoolExecutor

创建一个线程池，

```java
ThreadPoolExecutor service = new ThreadPoolExecutor();
```

构造方法，

```java
public ThreadPoolExecutor(int corePoolSize,
                          int maximumPoolSize,
                          long keepAliveTime,
                          TimeUnit unit,
                          BlockingQueue<Runnable> workQueue,
                          ThreadFactory threadFactory,
                          RejectedExecutionHandler handler) {
    if (corePoolSize < 0 ||
        maximumPoolSize <= 0 ||
        maximumPoolSize < corePoolSize ||
        keepAliveTime < 0)
        throw new IllegalArgumentException();
    if (workQueue == null || threadFactory == null || handler == null)
        throw new NullPointerException();
    this.corePoolSize = corePoolSize;
    this.maximumPoolSize = maximumPoolSize;
    this.workQueue = workQueue;
    this.keepAliveTime = unit.toNanos(keepAliveTime);
    this.threadFactory = threadFactory;
    this.handler = handler;
}
```

1.corePoolSize

线程池中的核心线程数，默认情况下，核心线程一种存活在线程池中，即使他们在线程池中处于闲置状态。除非将ThreadPoolExecutor的allowCoreThreadTimeOut属性设为true的时候，这时候处于闲置的核心线程在等待新任务到来时会有超时策略，这个超时时间由keepAliveTime来指定。一旦超过所设置的超时时间，闲置的核心线程就会被终止。

2.maximumPoolSize

线程池中所容纳的最大线程数，如果活动的线程达到这个数值以后，后续的新任务将会被阻塞。包含核心线程数 + 非核心线程数。

3.keepAliveTime

非核心线程闲置时的超时时长，对于非核心线程，闲置时间超过这个时间，非核心线程就会被回收。只有对ThreadPoolExecutor的allowCoreThreadTimeOut属性设为true的时候，这个超时时间才会对核心线程产生效果。

4.unit

用于指定keepAliveTime参数的时间单位。它是一个枚举，可以使用的单位有天（TimeUnit.DAYS），小时（TimeUnit.HOURS），分钟（TimeUnit.MINUTES），毫秒（TimeUnit.MILLISECONDS），微秒（TimeUnit.MICROSECONDS），毫微秒（TimeUnit.NANOSECONDS）。

5.workQueue

线程池中保存执行的任务的阻塞队列。通过线程池中的execute方法提交的Runnable对象都会保存在该队列中。可以选择下面的几个阻塞队列。

| 阻塞队列              | 说明                                                         |
| --------------------- | ------------------------------------------------------------ |
| ArrayBlockingQueue    | 基于数组实现的有界的阻塞队列，该队列按照FIFO（先进先出）原则对队列中的元素进行排序 |
| LinkedBlockingQueue   | 基于链表实现的阻塞队列，该队列按照FIFO（先进先出）原则对队列中的元素进行排序 |
| SynchronousQueue      | 内部没有任何容量的阻塞队列，在它内部没有任何的缓存空间。对于SynchronousQueue的数据元素只有当我们试着取走的时候才可能存在 |
| PriorityBlockingQueue | 具有优先级的无限阻塞队列                                     |

还能够通过实现BlockingQueue接口来自定义我们所需要的阻塞队列。

6.threadFactory

线程工厂，为线程池提供的新线程的创建。ThreadFactory是一个接口，里面只有一个newThread方法。默认为DefaultThreadFactory类。

7.handler











