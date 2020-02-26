# Handler源码分析

### 1.创建Handler类对象

1. 通过无参构造函数new一个Handler对象
2. 无参构造函数调用有参构造函数
3. 有参构造函数中获取当前线程中的Looper对象，并绑定对应的MessageQueue

```java
//1.通过匿名内部类创建Handler对象
mHandler = new Handler() { // ---> 分析1：Handler无参构造函数
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
    }
};

//分析1：Handler无参构造函数
public Handler() {
    this(null, false);
    // ---> 分析2：Handler有参构造函数
}

//分析2：Handler有参构造函数
public Handler(Callback callback, boolean async) {
    //FIND_POTENTIAL_LEAKS标志用来检测 扩展继承Handler的类 是否存在潜在的内存泄漏
    if (FIND_POTENTIAL_LEAKS) {
        final Class<? extends Handler> klass = getClass();
        //判断是否为 匿名类、成员类、局部类
        if ((klass.isAnonymousClass() || klass.isMemberClass() || klass.isLocalClass()) &&
            (klass.getModifiers() & Modifier.STATIC) == 0) {
            Log.w(TAG, "The following Handler class should be static or leaks might occur: " +
                  klass.getCanonicalName());
        }
    }
    
    //获取当前线程Looper对象，若线程无Looper对象则抛出异常
    mLooper = Looper.myLooper();
    if (mLooper == null) {
        throw new RuntimeException(
            "Can't create handler inside thread " + Thread.currentThread()
            + " that has not called Looper.prepare()");
    }
    //绑定消息队列MessageQueue对象
    mQueue = mLooper.mQueue;
    mCallback = callback;
    //mAsynchronous用来设置消息是否异步，这意味着发送的消息将不受Looper同步设置的影响
    mAsynchronous = async;
}
```

Q：当前线程的Looper和MessageQueue对象何时创建？

### 2.创建Handler前的隐式操作：创建Looper和MessageQueue

几个重要方法：

- Looper.prepareMainLooper()
- Looper.prepare()
- Looper.loop()

```java
//2.创建Looper和MessageQueue
public static void prepare() {
    prepare(true);
}

private static void prepare(boolean quitAllowed) {
    if (sThreadLocal.get() != null) {
        throw new RuntimeException("Only one Looper may be created per thread");
    }
    //quitAllowed参数从Looper传入MessageQueue用于quit方法
    sThreadLocal.set(new Looper(quitAllowed)); // ---> 分析1：Looper构造函数
}

//分析1：Looper构造函数
private Looper(boolean quitAllowed) {
    //创建一个MessageQueue与当前Looper相关联
    mQueue = new MessageQueue(quitAllowed);
    mThread = Thread.currentThread();
}
```

ActivityThread下的main方法：


```java
//Looper.prepareMainLooper()：主线程创建Looper和MessageQueue
//在Android应用进程启动时，会默认创建一个主线程（ActivityThread，UI线程），自动调用ActivityThread的一个静态main方法，其内部会调用Looper.prepareMainLooper()为主线程生成Looper和MessageQueue
public static void main(String[] args) {

    //主线程创建Looper和MessageQueue，类似于Looper.prepare()
    Looper.prepareMainLooper();

    //创建主线程
    ActivityThread thread = new ActivityThread();
	
    //开启消息循环
    Looper.loop();// ---> 分析2：Looper.loop()
}
```

```java
//分析2：Looper.loop()
//开启消息循环：从消息队列中获取消息，分发消息到对应Handler
public static void loop() {
    
    //获取Looper
    final Looper me = myLooper();
    if (me == null) {
        throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
    }
    
    //获取Looper对应的MessageQueue
    final MessageQueue queue = me.mQueue;
    
    for (;;) {
        //取出消息
        //若消息为空，则线程阻塞
        Message msg = queue.next(); // ---> 分析3：MessageQueue.next()
        if (msg == null) {
            return;
        }
        
        //将消息派发至对应Handler处理
        msg.target.dispatchMessage(msg); // ---> 分析4：dispatchMessage()
        
        //释放消息占用的资源
        msg.recycleUnchecked(); // ---> 分析5：recycleUnchecked()
    }
}
```

```java
//分析3：MessageQueue.next()
Message next() {
    int pendingIdleHandlerCount = -1; // -1 only during first iteration
    
    //用于确定消息队列中是否还有消息，决定消息队列应处于出队消息状态或等待状态
    int nextPollTimeoutMillis = 0;

    for (;;) {
        if (nextPollTimeoutMillis != 0) {
            Binder.flushPendingCommands();
        }

        // nativePollOnce方法在native层，若是nextPollTimeoutMillis为-1，此时消息队列处于等待状态，线程阻塞
        nativePollOnce(ptr, nextPollTimeoutMillis);

        synchronized (this) {
            final long now = SystemClock.uptimeMillis();
            Message prevMsg = null;
            Message msg = mMessages;

            //同步屏障就是一个target为null的msg，相当于拦截同步消息的“保安”，一旦开启同步屏障，只有异步消息可以通过，相当于为异步消息提高优先级 
            if (msg != null && msg.target == null) {
                //如果从队列里拿到的msg是个“同步屏障”，那么就寻找其后第一个“异步消息”
                do {
                    prevMsg = msg;
                    msg = msg.next;
                } while (msg != null && !msg.isAsynchronous());
            }
            if (msg != null) {
                if (now < msg.when) {
                    //如果有消息，但是时间还没到，或者没有消息的时候
                    nextPollTimeoutMillis = (int) Math.min(msg.when - now, Integer.MAX_VALUE);
                } else {
                    //取出消息
                    mBlocked = false;
                    if (prevMsg != null) {
                        prevMsg.next = msg.next;
                    } else {
                        mMessages = msg.next;
                    }
                    msg.next = null;
                    if (DEBUG) Log.v(TAG, "Returning message: " + msg);
                    //标记为已使用
                    msg.markInUse();
                    return msg;
                }
            } else {
                //消息队列为等待状态
                nextPollTimeoutMillis = -1;
            }
            
            if (mQuitting) {
                dispose();
                return null;
            }

            if (pendingIdleHandlerCount < 0
                && (mMessages == null || now < mMessages.when)) {
                pendingIdleHandlerCount = mIdleHandlers.size();
            }
            if (pendingIdleHandlerCount <= 0) {
                // No idle handlers to run.  Loop and wait some more.
                mBlocked = true;
                continue;
            }

            if (mPendingIdleHandlers == null) {
                mPendingIdleHandlers = new IdleHandler[Math.max(pendingIdleHandlerCount, 4)];
            }
            mPendingIdleHandlers = mIdleHandlers.toArray(mPendingIdleHandlers);
        }
        
        //IdleHandler的作用就是在队列空闲的时候干点事情，要使用IdleHandler就调用MessageQueue.addIdleHandler(IdleHandler handler)方法
        for (int i = 0; i < pendingIdleHandlerCount; i++) {
            final IdleHandler idler = mPendingIdleHandlers[i];
            mPendingIdleHandlers[i] = null;

            boolean keep = false;
            try {
                keep = idler.queueIdle();
            } catch (Throwable t) {
                Log.wtf(TAG, "IdleHandler threw exception", t);
            }

            if (!keep) {
                synchronized (this) {
                    mIdleHandlers.remove(idler);
                }
            }
        }

        pendingIdleHandlerCount = 0;
        nextPollTimeoutMillis = 0;
    }
}
```

```java
//分析4：dispatchMessage()
public void dispatchMessage(Message msg) {
    //使用post方式发送的消息的执行优先级最高
    //msg.callback不为空，使用post(Runnable r)方式发送消息，
    if (msg.callback != null) {
        //回调Runnable的run方法
        handleCallback(msg);
    } else {
        if (mCallback != null) {
            if (mCallback.handleMessage(msg)) {
                return;
            }
        }
        //callback为空，使用sendMessage(Message msg)方式发送消息，回到Handler的handleMessage方法
        handleMessage(msg);
    }
}
```

```java
//分析5：recycleUnchecked()
void recycleUnchecked() {
    flags = FLAG_IN_USE;
    what = 0;
    arg1 = 0;
    arg2 = 0;
    obj = null;
    replyTo = null;
    sendingUid = -1;
    when = 0;
    target = null;
    callback = null;
    data = null;

    synchronized (sPoolSync) {
        if (sPoolSize < MAX_POOL_SIZE) {
            next = sPool;
            sPool = this;
            sPoolSize++;
        }
    }
}
```

### 3.创建消息对象

```java
//3.创建Message对象
Message msg = Message.obtain(); // ---> 分析1：Message.obtain()
msg.what = 1;
msg.obj = "message";

//分析1：Message.obtain()
public static Message obtain() {
    //Message池用于Message对象的复用
    synchronized (sPoolSync) {
        if (sPool != null) {
            Message m = sPool;
            sPool = m.next;
            m.next = null;
            m.flags = 0;
            sPoolSize--;
            return m;
        }
    }
    return new Message();
}
```

建议：尽量使用`Message.obtain()`获取消息对象，若Message池中无可复用消息对象，则使用new创建。

### 4.使用sendMessage()发送消息到消息队列

```java
//4.使用sendMessage()发送消息到消息队列
mHandler.sendMessage(msg); // ---> 分析1：sendMessage(msg)

//分析1：sendMessage(msg)
public final boolean sendMessage(Message msg)
{
    return sendMessageDelayed(msg, 0);
}

public final boolean sendMessageDelayed(Message msg, long delayMillis)
{
    if (delayMillis < 0) {
        delayMillis = 0;
    }
    return sendMessageAtTime(msg, SystemClock.uptimeMillis() + delayMillis);
}

public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
    //获取消息队列
    MessageQueue queue = mQueue;
    return enqueueMessage(queue, msg, uptimeMillis);
}

private boolean enqueueMessage(MessageQueue queue, Message msg, long uptimeMillis) {
    //将msg.target属性设为当前Handler
    msg.target = this;
    
    if (mAsynchronous) {
        msg.setAsynchronous(true);
    }
    return queue.enqueueMessage(msg, uptimeMillis); // ---> 分析2：queue.enqueueMessage()
}

//分析2：queue.enqueueMessage()
//使用单链表管理消息队列
boolean enqueueMessage(Message msg, long when) {
    if (msg.target == null) {
        throw new IllegalArgumentException("Message must have a target.");
    }
    if (msg.isInUse()) {
        throw new IllegalStateException(msg + " This message is already in use.");
    }
    
    synchronized (this) {
        msg.markInUse();
        msg.when = when;
        Message p = mMessages;
        boolean needWake;

        //若队列中无消息，则将当前插入的消息作为队头，若此时消息队列处于等待状态，则唤醒
        if (p == null || when == 0 || when < p.when) {
            msg.next = p;
            mMessages = msg;
            needWake = mBlocked;
        } else {
            needWake = mBlocked && p.target == null && msg.isAsynchronous();
            Message prev;

            //若消息队列里有消息，消息队列中的Message以when字段升序排列，则根据消息创建的时间插入到队列中
            for (;;) {
                prev = p;
                p = p.next;
                if (p == null || when < p.when) {
                    break;
                }
                if (needWake && p.isAsynchronous()) {
                    needWake = false;
                }
            }

            msg.next = p; 
            prev.next = msg;
        }

        if (needWake) {
            nativeWake(mPtr);
        }
    }
    return true;
}
```

- SystemClock.uptimeMillis()：表示系统**开机到当前的时间总数**，单位是毫秒，但是，当系统进入深度睡眠（CPU休眠、屏幕休眠、设备等待外部输入）时间就会停止，但是不会受到时钟缩放、空闲或者其他节能机制的影响。
- System.currentTimeMillis()：获取的是系统的时间，可以使用SystemClock.setCurrentTimeMillis(long millis)进行设置。如果使用System.currentTimeMillis()来获取当前时间进行计时，应该考虑监听ACTION_TIME_TICK, ACTION_TIME_CHANGED 和 ACTION_TIMEZONE_CHANGED这些广播ACTION，如果系统时间发生了改变，可以通过监听广播来获取。

### 5.使用post()发送消息到消息队列

```java
//5.使用post()发送消息到消息队列
mHandler.post(new Runnable() { // ---> 分析1：post()
    @Override
    public void run() {

    }
});

//分析1：post()
public final boolean post(Runnable r)
{
    return sendMessageDelayed(getPostMessage(r), 0); // ---> 分析2：getPostMessage(r)
}

//分析2：getPostMessage(r)
private static Message getPostMessage(Runnable r) {
    //新建消息
    Message m = Message.obtain();
    //将Runnable对象设置为callback属性
    m.callback = r;
    
    return m;
}

// ---> 步骤2的分析4
//msg.target.dispatchMessage(msg)
public void dispatchMessage(Message msg) {
    //msg.callback不为空，使用post(Runnable r)方式发送消息，
    if (msg.callback != null) {
        //回调Runnable的run方法
        handleCallback(msg); // ---> 分析3：handleCallback(msg)
    } else {
        //通过Handler handler = new Handler(callback)方式传入的mCallback
        if (mCallback != null) {
            if (mCallback.handleMessage(msg)) {
                return;
            }
        }
        //callback为空，使用sendMessage(Message msg)方式发送消息，回到Handler的handleMessage方法
        handleMessage(msg);
    }
}

// ---> 分析3：handleCallback(msg)
private static void handleCallback(Message message) {
    message.callback.run();
}
```

### 6.同步屏障机制

#### 1.发送异步消息

```java
//1.新建一个专门发送异步消息的Handler
public Handler(boolean async) {
    this(null, async);
}

Handler mHandler = new Handler(true);
Message msg = mHandler.obtainMessage(...);
mHandler.sendMessageAtTime(msg, dueTime);

//2.设置Message的异步属性为true
Message msg = mHandler.obtainMessage(...);
msg.setAsynchronous(true);
```
#### 2.开启同步屏障

如果想让异步消息起作用，就得开启同步障碍，同步障碍会阻碍同步消息，只允许通过异步消息，如果队列中没有异步消息，此时的loop()方法将被Linux epoll机制所阻塞。

```java
mHandler.getLooper().getQueue().postSyncBarrier();

public int postSyncBarrier() {
	return postSyncBarrier(SystemClock.uptimeMillis());
}

private int postSyncBarrier(long when) {
    //返回值token
	final int token = mNextBarrierToken++;
    final Message msg = Message.obtain();
    msg.markInUse();
    msg.when = when;
    msg.arg1 = token;
    Message prev = null;
    Message p = mMessages;
    if (when != 0) {
        while (p != null && p.when <= when) {
            prev = p;
            p = p.next;
        }
    }
    if (prev != null) {
        msg.next = p;
        prev.next = msg;
    } else {
        msg.next = p;
        mMessages = msg;
    }
    return token;
}
```

在实例化Message对象的时候并没有设置它的target成员变量的值，然后随即就根据执行时间把它放到链表的某个位置了。也就是说，当在消息队列中放入一个target为空的Message的时候，当前Handler的这一套消息机制就开启了同步阻断。

```java
//分析3：MessageQueue.next()
Message next() {
    int pendingIdleHandlerCount = -1; // -1 only during first iteration
    
    //用于确定消息队列中是否还有消息，决定消息队列应处于出队消息状态或等待状态
    int nextPollTimeoutMillis = 0;

    for (;;) {
        if (nextPollTimeoutMillis != 0) {
            Binder.flushPendingCommands();
        }

        // nativePollOnce方法在native层，若是nextPollTimeoutMillis为-1，此时消息队列处于等待状态，线程阻塞
        nativePollOnce(ptr, nextPollTimeoutMillis);

        synchronized (this) {
            final long now = SystemClock.uptimeMillis();
            Message prevMsg = null;
            Message msg = mMessages;

            //同步屏障就是一个target为null的msg，相当于拦截同步消息的“保安”，一旦开启同步屏障，只有异步消息可以通过，相当于为异步消息提高优先级 
            if (msg != null && msg.target == null) {
                //如果从队列里拿到的msg是个“同步屏障”，那么就寻找其后第一个“异步消息”
                do {
                    prevMsg = msg;
                    msg = msg.next;
                } while (msg != null && !msg.isAsynchronous());
            }
            if (msg != null) {
                if (now < msg.when) {
                    //如果有消息，但是时间还没到，或者没有消息的时候
                    nextPollTimeoutMillis = (int) Math.min(msg.when - now, Integer.MAX_VALUE);
                } else {
                    //取出消息
                    mBlocked = false;
                    if (prevMsg != null) {
                        prevMsg.next = msg.next;
                    } else {
                        mMessages = msg.next;
                    }
                    msg.next = null;
                    if (DEBUG) Log.v(TAG, "Returning message: " + msg);
                    //标记为已使用
                    msg.markInUse();
                    return msg;
                }
            } else {
                //消息队列为等待状态
                nextPollTimeoutMillis = -1;
            }
            
            if (mQuitting) {
                dispose();
                return null;
            }

            if (pendingIdleHandlerCount < 0
                && (mMessages == null || now < mMessages.when)) {
                pendingIdleHandlerCount = mIdleHandlers.size();
            }
            if (pendingIdleHandlerCount <= 0) {
                // No idle handlers to run.  Loop and wait some more.
                mBlocked = true;
                continue;
            }

            if (mPendingIdleHandlers == null) {
                mPendingIdleHandlers = new IdleHandler[Math.max(pendingIdleHandlerCount, 4)];
            }
            mPendingIdleHandlers = mIdleHandlers.toArray(mPendingIdleHandlers);
        }
        
        //IdleHandler的作用就是在队列空闲的时候干点事情，要使用IdleHandler就调用MessageQueue.addIdleHandler(IdleHandler handler)方法
        for (int i = 0; i < pendingIdleHandlerCount; i++) {
            final IdleHandler idler = mPendingIdleHandlers[i];
            mPendingIdleHandlers[i] = null;

            boolean keep = false;
            try {
                keep = idler.queueIdle();
            } catch (Throwable t) {
                Log.wtf(TAG, "IdleHandler threw exception", t);
            }

            if (!keep) {
                synchronized (this) {
                    mIdleHandlers.remove(idler);
                }
            }
        }

        pendingIdleHandlerCount = 0;
        nextPollTimeoutMillis = 0;
    }
}
```

开启了同步障碍时，Looper在获取下一个要执行的消息时，会在链表中寻找第一个要执行的异步消息，如果没有找到异步消息，就让当前线程沉睡。

消息机制中也很巧妙的融入了优先级特点，这个同步障碍机制，实质上是一个对消息队列的优先级显示。

#### 3.移除同步屏障

```java
public void removeSyncBarrier(int token) {
    // Remove a sync barrier token from the queue.
    // If the queue is no longer stalled by a barrier then wake it.
    synchronized (this) {
        Message prev = null;
        Message p = mMessages;
        //找到token对应的屏障
        while (p != null && (p.target != null || p.arg1 != token)) {
            prev = p;
            p = p.next;
        }
        if (p == null) {
            throw new IllegalStateException("The specified message queue synchronization "
                                            + " barrier token has not been posted or has already been removed.");
        }
        final boolean needWake;
        //从消息链表中移除
        if (prev != null) {
            prev.next = p.next;
            needWake = false;
        } else {
            mMessages = p.next;
            needWake = mMessages == null || mMessages.target != null;
        }
        //回收这个Message到对象池中。
        p.recycleUnchecked();

        // If the loop is quitting then it is already awake.
        // We can assume mPtr != 0 when mQuitting is false.
        if (needWake && !mQuitting) {
            nativeWake(mPtr);//唤醒消息队列
        }
    }
}
```

### 7.IdleHandler

```java
public static interface IdleHandler {
    boolean queueIdle();
}

//使用方法
MessageQueue.addIdleHandler();
```

MessageQueue中：

- 存放IdleHandler的ArrayList(mIdleHandlers)，
- 还有一个IdleHandler数组(mPendingIdleHandlers)。

数组里面放的IdleHandler实例都是临时的，也就是每次使用完（调用了queueIdle方法）之后，都会置空（mPendingIdleHandlers[i] = null），

1. 如果本次循环拿到的Message为空，或者这个Message是一个延时的消息而且还没到指定的触发时间，那么，就认定当前的队列为空闲状态，
2. 接着就会遍历mPendingIdleHandlers数组(这个数组里面的元素每次都会到mIdleHandlers中去拿)来调用每一个IdleHandler实例的queueIdle方法，
3. 如果这个方法返回false的话，那么这个实例就会从mIdleHandlers中移除，也就是当下次队列空闲的时候，不会继续回调它的queueIdle方法了。

### 8.常见面试题

#### 1.loop为什么不会阻塞，CPU为什么不会忙等？

nativeWake()方法和nativePollOnce()方法采用了Linux的epoll机制，其中nativePollOnce()的第二个值，当它是-1时会一直沉睡，直到被主动唤醒为止，当它是0时不会沉睡，当它是大于0的值时会沉睡传入的值那么多的毫秒时间。epoll机制实质上是让CPU沉睡，来保障当前线程一直在运行而不中断或者卡死，这也是Looper#loop()死循环为什么不会导致ANR的根本原因。

[Gityuan的回答](https://www.zhihu.com/question/34652589)

#### 2.Message缓存池如何实现缓存？

通过链表缓存，需要时从头部取出，回收时插入头部。

#### 3.子线程如何使用Handler机制？

保证在子线程中先执行`Looper.prepare()`，再调用`Looper.loop()`开始消息循环。
