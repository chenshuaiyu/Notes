# LinkedBlockingQueue

基于链表的阻塞队列，作为固定大小线程池（Executors.newFixedThreadPool()）底层所使用的阻塞队列。

### 1. 源码分析

```java
static class Node<E> {
    E item;

    Node<E> next;

    Node(E x) { item = x; }
}

//最大容量，可手动指定，默认为Integer.MAX_VALUE
private final int capacity;

//当前阻塞队列的元素数量
//ArrayBlockingQueue:使用int记录元素数量，因为元素的入队和出队使用的是用一个lock对象，而数量的修改都是处于线程获取锁的情况下进行操作，因此不会有线程安全问题。
//LinkedBlockingQueue:入队和出队使用两个不同的lock对象，涉及到元素的并发修改，因此使用原子操作类。
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





