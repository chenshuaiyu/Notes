# BlockingQueue

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

### 2.ArrayBlockingQueue

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

take方法：

```java
public E take() throws InterruptedException {
    final ReentrantLock lock = this.lock;
    //尝试获取锁，如果此时锁被其它线程占用，那么当前线程就处于Waiting状态
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

dequeue方法：

```java
private E dequeue() {
    // assert lock.getHoldCount() == 1;
    // assert items[takeIndex] != null;
    final Object[] items = this.items;
    @SuppressWarnings("unchecked")
    E x = (E) items[takeIndex];
    items[takeIndex] = null;
    if (++takeIndex == items.length)
        takeIndex = 0;
    count--;
    if (itrs != null)
        itrs.elementDequeued();
    notFull.signal();
    return x;
}
```

### 3.LinkedBlockingQueue

基于链表的阻塞队列，作为固定大小线程池（Executors.newFixedThreadPool()）底层所使用的阻塞队列。

```java
static class Node<E> {
    E item;

    Node<E> next;

    Node(E x) { item = x; }
}

//最大容量，可手动指定，默认为Integer.MAX_VALUE
private final int capacity;

//当前阻塞队列的元素数量
//ArrayBlockingQueue：使用int记录元素数量，因为元素的入队和出队使用的是用一个lock对象，而数量的修改都是处于线程获取锁的情况下进行操作，因此不会有线程安全问题。
//LinkedBlockingQueue：入队和出队使用两个不同的lock对象，涉及到元素的并发修改，因此使用原子操作类。
private final AtomicInteger count = new AtomicInteger();

//链表头结点 head.item = null
transient Node<E> head;

//链表尾结点 head.next = null
private transient Node<E> last;

//出队列take、poll线程所获取的锁
private final ReentrantLock takeLock = new ReentrantLock();

//队列为空时，从队列中获取元素的线程处于等待状态
private final Condition notEmpty = takeLock.newCondition();

//入队列add、put、offer线程所获取的锁
private final ReentrantLock putLock = new ReentrantLock();

//当队列达到capacity时，元素入队的线程处于等待状态
private final Condition notFull = putLock.newCondition();
```

入队和出队使用的不是一个Lock，意味着他们之间的操作不会存在互斥操作。在多个CPU的情况下，可以做到真正的在同一时刻既消费又生产，能够并行处理。

```java
public LinkedBlockingQueue() {
    this(Integer.MAX_VALUE);
}

public LinkedBlockingQueue(int capacity) {
    if (capacity <= 0) throw new IllegalArgumentException();
    this.capacity = capacity;
    last = head = new Node<E>(null);
}

public LinkedBlockingQueue(Collection<? extends E> c) {
    this(Integer.MAX_VALUE);
    final ReentrantLock putLock = this.putLock;
    putLock.lock(); // Never contended, but necessary for visibility
    try {
        int n = 0;
        for (E e : c) {
            if (e == null)
                throw new NullPointerException();
            if (n == capacity)
                throw new IllegalStateException("Queue full");
            enqueue(new Node<E>(e));
            ++n;
        }
        count.set(n);
    } finally {
        putLock.unlock();
    }
}
```

```java
private void enqueue(Node<E> node) {
    // assert putLock.isHeldByCurrentThread();
    // assert last.next == null;
    last = last.next = node;
}

public void put(E e) throws InterruptedException {
    if (e == null) throw new NullPointerException();
    int c = -1;
    Node<E> node = new Node<E>(e);
    final ReentrantLock putLock = this.putLock;
    final AtomicInteger count = this.count;
    putLock.lockInterruptibly();
    try {
        while (count.get() == capacity) {
            notFull.await();
        }
        enqueue(node);
        c = count.getAndIncrement();
        if (c + 1 < capacity)
            notFull.signal();
    } finally {
        putLock.unlock();
    }
    if (c == 0)
        signalNotEmpty();
}

private void signalNotEmpty() {
    final ReentrantLock takeLock = this.takeLock;
    takeLock.lock();
    try {
        notEmpty.signal();
    } finally {
        takeLock.unlock();
    }
}
```

```java
public boolean offer(E e) {
    if (e == null) throw new NullPointerException();
    final AtomicInteger count = this.count;
    if (count.get() == capacity)
        return false;
    int c = -1;
    Node<E> node = new Node<E>(e);
    final ReentrantLock putLock = this.putLock;
    putLock.lock();
    try {
        if (count.get() < capacity) {
            enqueue(node);
            c = count.getAndIncrement();
            if (c + 1 < capacity)
                notFull.signal();
        }
    } finally {
        putLock.unlock();
    }
    if (c == 0)
        signalNotEmpty();
    return c >= 0;
}
```

```java
public boolean offer(E e, long timeout, TimeUnit unit)
    throws InterruptedException {

    if (e == null) throw new NullPointerException();
    long nanos = unit.toNanos(timeout);
    int c = -1;
    final ReentrantLock putLock = this.putLock;
    final AtomicInteger count = this.count;
    putLock.lockInterruptibly();
    try {
        while (count.get() == capacity) {
            if (nanos <= 0)
                return false;
            nanos = notFull.awaitNanos(nanos);
        }
        enqueue(new Node<E>(e));
        c = count.getAndIncrement();
        if (c + 1 < capacity)
            notFull.signal();
    } finally {
        putLock.unlock();
    }
    if (c == 0)
        signalNotEmpty();
    return true;
}
```