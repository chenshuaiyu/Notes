# Synchronized & ReentrantLock

### 1. Synchronized

#### 1.synchronized简介

1. Synchronized实现同步的基础：Java每个对象都可以作为锁。当线程视图访问同步代码时，必须先获得对象锁，退出或抛出异常时必须使用锁。
2. Synchronized实现同步的表现形式分为：代码块同步和方法同步。

#### 2.synchronized原理

JVM基于进入和退出`Monitor`对象来实现代码块同步和方法同步，两者的实现细节不同。

- 代码块同步：在编译后通过将`monitorenter`指令插入到同步代码块的开始处，将`monitorexit`指令插入到方法结束处和异常处，通过反编译字节码可以观察到，任何一个对象都有一个monitor与之关联，线程执行`monitorenter`指令时，会尝试获取对象对应的monitor的所有权，即尝试获得对象的锁。
- 方法同步：synchronized方法在method_info结构有ACC_synchronized标记，线程执行时会识别该标记，获得对应的锁，实现方法同步。

虽然实现细节不同，但本质上都是对一个对象的监视器（monitor）的获取。任意一个对象都拥有自己的监视器，当同步代码块或同步方法执行时，执行方法的线程必须先获得该线程的监视器才能进入同步代码块或同步方法，没有获取到监视器的线程将会被阻塞，并进入同步队列，状态变为`BLOCKED`，当成功获取监视器的线程释放了锁之后，会唤醒阻塞在同步队列中的线程，使其重新尝试对监视器的获取。

### 2. ReentrantLock锁

ReentrantLock，一个可重入的互斥锁，它具有与使用synchronized方法和语句所访问的隐式监视器锁相同的一些基本行为和语义，但功能更强大。

#### 1.Lock接口

Lock，锁对象。在Java中锁是用来控制多个线程访问共享资源的方式，一般来说，一个锁能够控制多个线程同时访问共享资源。

```java
void lock(); //执行此方法时，如果锁处于空闲状态，当前线程将获取到锁。相反，如果锁已经被其他线程持有，将禁用当前线程，直到当前线程获取到锁。

boolean tryLock(); //如果锁可用，则获取锁，并立即返回true，否则返回false。该方法和lock()的区别在于，tryLock()只是试图获取锁，如果锁不可用，不会导致当前线程被禁用，当前线程仍然继续往下执行代码。而lock()方法则是一定要获取到锁，如果锁不可用，就一直等待，在未获得锁之前，当前线程并不继续向下执行。

void unlock(); //执行此方法时，当前线程将释放持有的锁，只能由持有者释放，如果线程不持有锁，却执行该方法，可能导致异常的发生。

Condition newCondition(); //条件对象，获取等待通知组件。该组件和当前的锁绑定，当前线程只有获取了锁，才能调用该组件的await()方法，而调用后，当前线程线程将释放锁。
```

#### 2.ReentrantLock的使用

```java
ReentrantLock lock = new ReentrantLock();//参数默认false，不公平锁
lock.lock();

try {
    
} finally {
    lock.unlock();
}
```

### 3. 重入锁

当一个线程得到一个对象后，再次请求该对象锁时是可以再次得到该对象的锁的。

具体概念就是：自己可以再次获取自己的内部锁。

Java内置锁synchronized和ReentrantLock都是可重入的。

```java
public class SynchronizedTest {
    public void method1 () {
        synchronized (SynchronizedTest.class) {
            System.out.println("方法1获得ReentrantTest的锁运行了");
            method2();
        }
    }
    
    public void method2 () {
        synchronized (SynchronizedTest.class) {
            System.out.println("方法1里面调用的方法2重入锁，也正常运行了");
            method2();
        }
    }
    
    public static void main(String[] args) {
        new SynchronizedTest().method1();
    }
}
```

```java
class ReentrantLockTest {
    private Lock lock = new ReentrantLock();
    
    public void method1() {
        lock.lock();
        try {
            System.out.println("方法1获得ReentrantLock锁运行了");
            method2();
        } finally {
            lock.unlock();
        }
    }
    
    public void method2() {
        lock.lock();
        try {
            System.out.println("方法1里面调用的方法2重入ReentrantLock锁，也正常运行了");
        } finally {
            lock.unlock();
        }
    }
    
    public static void main(String[] args) {
        new ReentrantLockTest().method1();
    }
}
```

### 4. 公平锁

CPU在调度线程的时候是在等待队列里随机挑选一个线程，由于这种随机性所以是无法保证线程先到先得的（synchronized控制的锁就是非公平锁）。但这样就会产生饥饿现象，即有些线程（优先级较低的线程）可能永远无法获取CPU的执行权。

公平锁可以保证线程按照时间的先后顺序执行，避免饥饿现象的产生。但是公平锁的效率较低，因为要实现顺序执行，需要维护一个有序队列。ReentrantLock是一种公平锁，通过在构造方法中传入true就是公平锁，传入false，就是非公平锁。

```java
public ReentrantLock(boolean fair) {
    sync = fair ? new FairSync() : new NonfairSync();
}
```

使用公平锁实现的效果：

```java
class LockFairTest implements Runnable {
    private static ReentrantLock lock = new ReentrantLock(true);
    
    @Override
    public void run() {
        while(true) {
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + "获得锁");
            } finally {
                lock.unlock();
            }
        }
    }
    
    public static void main(String[] args) {
        LockFairTest lft = new LockFairTest();
        Thread th1 = new Thread(lft);
        Thread th2 = new Thread(lft);
        th1.start();
        th2.start();
    }
}
```

```
Thread-0获得锁
Thread-1获得锁
Thread-0获得锁
Thread-1获得锁
...
```

### 5. synchronized和ReentrantLock的比较











