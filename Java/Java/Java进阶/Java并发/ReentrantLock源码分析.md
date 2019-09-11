# ReentrantLock源码分析

### 1. 部分源码

```java
public class ReentrantLock implements Lock, java.io.Serializable {
    private final Sync sync;

    abstract static class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = -5179523762034025860L;

        abstract void lock();

        final boolean nonfairTryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                if (compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
            else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                if (nextc < 0) // overflow
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }

        protected final boolean tryRelease(int releases) {
            int c = getState() - releases;
            if (Thread.currentThread() != getExclusiveOwnerThread())
                throw new IllegalMonitorStateException();
            boolean free = false;
            if (c == 0) {
                free = true;
                setExclusiveOwnerThread(null);
            }
            setState(c);
            return free;
        }
        ...
    }
    
    //默认非公平锁
    public ReentrantLock() {
        sync = new NonfairSync();
    }

    //fair俄日true时，采用公平锁策略
    public ReentrantLock(boolean fair) {
        sync = fair ? new FairSync() : new NonfairSync();
    }

    public void lock() {
        sync.lock();
    }

    public boolean tryLock() {
        return sync.nonfairTryAcquire(1);
    }

    public boolean tryLock(long timeout, TimeUnit unit)
            throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(timeout));
    }

    public void unlock() {
        sync.release(1);
    }
    
    public Condition newCondition() {
        return sync.newCondition();
    }

    public boolean isLocked() {
        return sync.isLocked();
    }

    public final boolean isFair() {
        return sync instanceof FairSync;
    }
}
```

### 2. 使用

```java
Lock lock = new ReentrantLock();
Condition condition = lock.newCondition();
lock.lock();
try {
    while(条件判断式) {
    	condition.wait();
    }
    //处理逻辑
} finally {
    lock.unlock();
}
```

### 3. 非公平锁实现

在非公平锁中，每当线程执行lock方法时，都尝试利用CAS把state从0设置为1，

场景：

1. 持有锁的线程A正在running，队列中有线程BCDEFG被挂起并等待被唤醒。
2. 在某一个时间点，线程A执行unlock，唤醒线程B。
3. 同时线程B执行lock，线程B和G拥有相同的获得锁的优先级，同时执行CAS指令竞争锁。如果恰好线程G成功了，线程B就得重新挂起等待被唤醒。

```java
static final class NonfairSync extends Sync {
    final void lock() {
        if (compareAndSetState(0, 1))
            setExclusiveOwnerThread(Thread.currentThread());
        else
            acquire(1);
    }

    protected final boolean tryAcquire(int acquires) {
        return nonfairTryAcquire(acquires);
    }
}
```

通过线程A和线程B来描述非公平锁的竞争过程：

1. 线程A和B同时执行CAS指令，假设线程A成功，线程B失败，则表明线程A成功获取锁，并把同步器中的`exclusiveOwnerThread`设置为线程A。
2. 竞争失败的线程B，在`nonfairTryAcquire`中，会再次尝试获取锁。

### 4. 公平锁实现

在公平锁中，每当线程执行lock方法时，如果同步器的队列中线程在等待，则直接加入队列中。

场景：

1. 持有锁的线程A正在running，队列中有线程BCDEF被挂起并等待被唤醒。
2. 线程G执行lock，队列中有线程BCDEF在等待，线程G直接加入到队列的队尾。

所以每个线程获取锁的过程是公平的，等待时间最长的会最先被唤醒获取锁。

```java
static final class FairSync extends Sync {

    final void lock() {
        acquire(1);
    }

    protected final boolean tryAcquire(int acquires) {
        final Thread current = Thread.currentThread();
        int c = getState();
        if (c == 0) {
            if (!hasQueuedPredecessors() &&
                compareAndSetState(0, acquires)) {
                setExclusiveOwnerThread(current);
                return true;
            }
        }
        else if (current == getExclusiveOwnerThread()) {
            int nextc = c + acquires;
            if (nextc < 0)
                throw new Error("Maximum lock count exceeded");
            setState(nextc);
            return true;
        }
        return false;
    }
}
```

### 5. 重入锁实现

重入锁，即线程可以重复获取已经持有的锁。在非公平和公平锁中，都对重入锁进行了实现。

```java
if (current == getExclusiveOwnerThread()) {
    int nextc = c + acquires;
    if (nextc < 0) // overflow
        throw new Error("Maximum lock count exceeded");
    setState(nextc);
    return true;
}
```

### 6. 条件变量Condition

条件变量很大程度是为了解决Object.wait/notify/notifyAll难以使用的问题。

1. Synchronized中，所有的线程都在同一个object的条件队列上等待。而ReentrantLock中，每个condition都维护一个条件队列。
2. 每一个Lock可以有任意多的Condition对象，Condition是与Lock绑定的，所以就有Lock的公平性特性：如果是公平锁，线程会按照FIFO的顺序从Condition.await中释放，如果是非公平锁，那么后续的锁竞争就不保证FIFO顺序了。
3. Condition接口定义的方法，await对应于Object.wait，singal对应于Object.notify，signalAll对应于Object.notifyAll。

**await实现逻辑**：

1. 将线程A加入到条件等待队列中，如果最后一个节点是取消状态，则从队列中删除。
2. 线程A释放锁，实质上是线程A修改AQS的状态state为0，并唤醒AQS等待队列中的线程B，线程B被唤醒后，尝试获取锁。
3. 线程A释放锁并唤醒线程B之后，如果线程A不在AQS的同步队列中，线程A将通过LockSupport.park进行挂起操作。
4. 线程A等待被唤醒，当线程A被唤醒后，会通过acquireQueued方法竞争锁，如果失败，继续挂起。如果成功，线程A从await位置恢复。

**signal实现逻辑**：

1. 线程B执行了signal方法，取出条件队列中的第一个非CANCELLED节点线程，即线程A。另外，signalAll就是唤醒条件队列中所有非CANCELLED节点线程。遇到CANCELLED节点线程就需要将其从队列中删除。
2. 通过CAS修改线程A的waitStatus，表示该节点已经不是等待条件状态，并将线程A插入到AQS的等待队列中。
3. 唤醒线程A，线程A和别的线程进行锁的竞争。