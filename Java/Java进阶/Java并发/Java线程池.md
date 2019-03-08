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
ExecutorService service = new ThreadPoolExecutor(...);
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

线程池中的核心线程数，默认情况下，核心线程一直存活在线程池中，即使他们在线程池中处于闲置状态。除非将ThreadPoolExecutor的allowCoreThreadTimeOut属性设为true的时候，这时候处于闲置的核心线程在等待新任务到来时会有超时策略，这个超时时间由keepAliveTime来指定。一旦超过所设置的超时时间，闲置的核心线程就会被终止。

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

还能够通过实现`BlockingQueue`接口来自定义我们所需要的阻塞队列。

6.threadFactory

线程工厂，为线程池提供的新线程的创建。ThreadFactory是一个接口，里面只有一个newThread方法。默认为DefaultThreadFactory类。

7.handler

是RejectedExecutionHandler对象，而RejectedExecutionHandler是一个接口，里面只有一个rejectedExecution方法。**当任务队列已满并且线程池中的活动线程已经达到所限定的最大值或者是无法成功执行任务，这时候ThreadPoolExecutor会调用RejectedExecutionHandler中的rejectedExecution方法。在ThreadPoolExecutor中有四个内部类实现了RejectedExecutionHandler接口。在线程池中它默认是AbortPolicy，在无法处理新任务时抛出RejectedExecutionException异常**。

TheadPoolExecutor中提供的四个可选值：

| 可选值              | 说明                                     |
| ------------------- | ---------------------------------------- |
| CallerRunsPolicy    | 只用调用者所在线程来与运行任务           |
| AbortPolicy         | 直接抛出RejectedExecutionException异常   |
| DiscardPolicy       | 丢弃掉该任务，不进行处理                 |
| DiscardOldestPolicy | 丢弃队列里最近的一个任务，并执行当前任务 |

也可以通过实现RejectedExecutionHandler接口来自定义自己的Handle，如记录日志或持久化不能处理的任务。

##### ThreadPoolExecutor的使用

```java
//其他参数均采用默认值
ExecutorService service = new ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
```

可以通过execute和submit两种方法向线程池提交一个任务。

- execute()

```java
//没有返回值，无法判断任务是否执行成功
service.execute(new Runnable() {
	public void run() {
		System.out.println("execute方式");
	}
});
```

- submit

```java
//如果子线程任务没有完成，Future的get方法会阻塞住直到任务完成
//get(long timeout, TimeUnit unit)方法则会阻塞一段时间后立即返回，这时候有可能任务并没有执行完。
Future<Integer> future = service.submit(new Callable<Integer>() {

	@Override
	public Integer call() throws Exception {
		System.out.println("submit方式");
		return 2;
	}
});
try {
	Integer number = future.get();
} catch (ExecutionException e) {
	e.printStackTrace();
}
```

##### 线程池关闭

- shutDown():将线程状态设置为SHUTDOWN状态，然后中断所有没有正在执行的进程。
- shutDownNow():将线程状态设置为STOP状态，然后中断所有任务（包括正在执行的进程），并返回等待执行任务的列表。

**中断采用interrupt方法，所有无法响应中断程序的任务可能永远无法终止。**

### 三、线程执行流程

1. 如果在线程池中的线程数量没有达到核心的线程数量，这时候就回启动一个核心线程来执行任务。
2. 如果线程中的线程数量已经超过核心线程数，这时候任务就会被插入到任务队列中排队等待执行。
3. 由于任务队列已满，无法将任务插入到任务队列中，这个时候如果线程中的线程数量没有达到线程池所设定的最大值，那么就会立即启动一个非核心线程来执行任务。
4. 如果线程池中的数量达到了所规定的最大值，那么就会拒绝执行此任务，这时候就会调用RejectedExecutionHandler中的rejectedExecution方法来通知调用者。

### 四、四种线程池类

他们都是直接或间接配置ThreadPoolExecutor来实现各自的功能。这四种线程池分别是：

- newFixedThreadPool
- newCachedThreadPool
- newScheduledThreadPool
- newSingleThreadExecutor

#### 1.newFixedThreadPool

```java
ExecutorService service = Executors.newFixedThreadPool(4);
```

该线程池是一种线程数量固定的线程池，在此线程池中，**所容纳的最大线程数就是我们设置的核心线程数**。如果线程池的线程处于空闲状态，他们并不会回收，除非这个线程池被关闭，如果所有的线程都处于活动状态的话，新任务就会处于等待状态，直到有线程空闲出来。

由于newFixedThreadPool只有核心线程，所有这些线程都不会被回收，就可以更快的响应外界请求。

```java
//只有核心线程，不存在超时机制，采用LinkedBlockingQueue，任务队列的大小是没有限制的。
public static ExecutorService newFixedThreadPool(int nThreads) {
	return new ThreadPoolExecutor(nThreads, nThreads,
		0L, TimeUnit.MILLISECONDS,
		new LinkedBlockingQueue<Runnable>());
}
```

#### 2.newCachedThreadPool

```java
//核心线程数为0，最大线程数为Integer.MAX_VALUE
public static ExecutorService newCachedThreadPool() {
	return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
		60L, TimeUnit.SECONDS,
		new SynchronousQueue<Runnable>());
}
```

**当线程池中的线程都处于活动状态的时候，线程池就会创建一个新的线程来处理任务。该线程池中的线程超时时长为60秒，所以当线程处于闲置状态超过60秒的时候便会被回收。**若是整个线程池的线程都处于闲置状态超过60秒以后，在newCachedThreadPool线程池中是不存在任何线程的，所以这时候它几乎不占用任何的系统资源。

SynchronousQueue内部没有任何容量的阻塞队列。SynchronousQueue内部相当于一个空集合，我们无法将一个任务插入到SynchronousQueue中。所以说在线程池中如果现有线程无法接收任务，将会创建新的线程来执行任务。

#### 3.newScheduledThreadPool

```java
public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize) {
    return new ScheduledThreadPoolExecutor(corePoolSize);
}
public ScheduledThreadPoolExecutor(int corePoolSize) {
    super(corePoolSize, Integer.MAX_VALUE, 0, NANOSECONDS,
          new DelayedWorkQueue());
}
```

核心线程固定，非核心线程数几乎没有限制，当非核心线程池处于限制状态的时候就会立即被召回。

创建一个可定时执行或周期执行任务的线程池：

```java
ScheduledExecutorService service = Executors.newScheduledThreadPool(4);
service.schedule(new Runnable() {
	public void run() {
		System.out.println(Thread.currentThread().getName()+"延迟三秒执行");
	}
}, 3, TimeUnit.SECONDS);
service.scheduleAtFixedRate(new Runnable() {
	public void run() {
		System.out.println(Thread.currentThread().getName()+"延迟三秒后每隔2秒执行");
	}
}, 3, 2, TimeUnit.SECONDS);
```

输出结果：

```
pool-1-thread-2延迟三秒后每隔2秒执行 
pool-1-thread-1延迟三秒执行 
pool-1-thread-1延迟三秒后每隔2秒执行 
pool-1-thread-2延迟三秒后每隔2秒执行 
pool-1-thread-2延迟三秒后每隔2秒执行
```

```java
schedule(Runnable command, long delay, TimeUnit unit)//延迟一定时间后执行Runnable任务；
schedule(Callable callable, long delay, TimeUnit unit)//延迟一定时间后执行Callable任务；

scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)//延迟一定时间后，以间隔period时间的频率周期性地执行任务；

scheduleWithFixedDelay(Runnable command, long initialDelay, long delay,TimeUnit unit)//与scheduleAtFixedRate()方法很类似，但是不同的是scheduleWithFixedDelay()方法的周期时间间隔是以上一个任务执行结束到下一个任务开始执行的间隔，而scheduleAtFixedRate()方法的周期时间间隔是以上一个任务开始执行到下一个任务开始执行的间隔，也就是这一些任务系列的触发时间都是可预知的。
```

#### 4.newSingleThreadExecutor

```java
//只有一个核心线程，对于任务队列没有大小限制，也就意味着这一个任务处于活动状态时，其他任务都会在任务队列中排队等候依次执行
public static ExecutorService newSingleThreadExecutor() {
	return new FinalizableDelegatedExecutorService
	(new ThreadPoolExecutor(1, 1,
		0L, TimeUnit.MILLISECONDS,
		new LinkedBlockingQueue<Runnable>()));
}
```

newSingleThreadExecutor将所有的外界任务统一到一个线程中支持，所以在这个任务执行之间我们不需要处理线程同步的问题。

