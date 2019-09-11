# ArrayBlockingQueue

### 1. Queue和BlockingQueue接口回顾

#### 1.Queue

在Queue接口中，除了继承Collection接口中定义的方法外，还分别定义了插入、删除、查询这3个操作，其中每一种操作都以两种不同的形式存在。

|         | Throws exception | Returns special value |
| ------- | ---------------- | --------------------- |
| Insert  | add(e)           | offer(e)              |
| Remove  | remove(e)        | poll()                |
| Examine | element()        | peek()                |

1. add方法将一个元素插入到队列的尾部时，如果队列已经满了，那么就抛出IllegalStateException，而使用offer方法时，如果队列已经满了，则添加失败，返回false，但不会引发异常。
2. remove是获取队列的头部元素并且删除，如果当队列为空时，那么就会抛出NoSuchElementException。而poll在队列为空时，则返回一个null。
3. element方法是从队列中获取到队列的第一个元素，但不会删除，但是如果队列为空时，那么它就会抛出NoSuchElementException。peek方法与之类似，只是不会抛出异常，而是返回false。

#### 2.BlockingQueue

当获取队列中的头部元素时，如果队列为空，那么它将会使执行线程处于等待状态，当添加一个元素到队列的尾部时，如果队列已经满了，那么它同样会使执行的线程处于等待状态。

BlockingQueue提供了四种不同的形式：

1. 抛出异常
2. 返回一个特殊值（null或false，取决于具体操作）
3. 阻塞当前执行直到可以继续
4. 当线程被挂起后，等待最大的时间，如果一旦超时，即使该操作依旧无法继续执行，线程也不会再继续等待下去。

|         | Throws exception | Returns special value | Blocks | Times out            |
| ------- | ---------------- | --------------------- | ------ | -------------------- |
| Insert  | add(e)           | offer(e)              | put(e) | offer(e, time, unit) |
| Remove  | remove()         | poll()                | take() | poll(time, unit)     |
| Examine | element          | peek()                | 无     | 无                   |

**注意**：

1. BlockingQueue不允许添加null。
2. 线程安全，因此所有和队列相关的方法都具有原子性。而从Collection中继承来的批量操作方法，例如addAll()，通常不保证其具有原子性。
3. 主要用于生产着消费者模型中，元素的添加和获取都具有规律性，但是对于remove这样的方法，虽然可以保证元素正确删除，但会影响性能，因此在没有特殊的情况下，也应该避免使用这类方法。

### 2. 源码分析

- 大小固定，底层是由一个数组维护，队列的元素顺序按照FIFO规则。
- 新元素插入队尾，从队头获取元素。
- 一旦创建，大小不能再改变，队列满的时候，执行put操作，线程阻塞，队列空的时候，执行take操作，线程阻塞。
- 支持公平策略（底层有ReentrantLock的公平锁实现），默认情况下使用非公平锁，使用公平锁会造成吞吐的下降但是可以避免线程饥饿。

```java
//底层维护队列元素的数组
final Object[] items;

//读取元素时数组的下标
int takeIndex;

//添加元素时数组的下标
int putIndex;

//队列中的元素个数
int count;

//控制并发的锁
final ReentrantLock lock;

//控制take操作时是否让想线程等待
private final Condition notEmpty;

//控制put操作时是否让想线程等待
private final Condition notFull;
```

```java
public E take() throws InterruptedException {
    final ReentrantLock lock = this.lock;
    lock.lockInterruptibly();
    try {
        while (count == 0)
            notEmpty.await();
        return dequeue();
    } finally {
        lock.unlock();
    }
}
```













