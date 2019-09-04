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
    ...
    
    //获取当前线程Looper对象，若线程无Looper对象则抛出异常
    mLooper = Looper.myLooper();
    if (mLooper == null) {
        throw new RuntimeException(
            "Can't create handler inside thread " + Thread.currentThread()
            + " that has not called Looper.prepare()");
    }
    //绑定消息队列MessageQueue对象
    mQueue = mLooper.mQueue;
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
    sThreadLocal.set(new Looper(quitAllowed)); // ---> 分析1：Looper构造函数
}

//分析1：Looper构造函数
private Looper(boolean quitAllowed) {
    //创建一个MessageQueue与当前Looper相关联
    mQueue = new MessageQueue(quitAllowed);
    
    mThread = Thread.currentThread();
}
```

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
        msg.recycleUnchecked();
    }
}
```

```java
//分析3：MessageQueue.next()
Message next() {
    //用于确定消息队列中是否还有消息，决定消息队列应处于出队消息状态或等待状态
    int nextPollTimeoutMillis = 0;

    for (;;) {
        if (nextPollTimeoutMillis != 0) {
            Binder.flushPendingCommands();
        }

        // nativePollOnce方法在native层，若是nextPollTimeoutMillis为-1，此时消息队列处于等待状态　
        nativePollOnce(ptr, nextPollTimeoutMillis);

        synchronized (this) {
            final long now = SystemClock.uptimeMillis();
            Message prevMsg = null;
            Message msg = mMessages;

            if (msg != null) {
                if (now < msg.when) {
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
                    msg.markInUse();
                    return msg;
                }
            } else {
                //消息队列为等待状态
                nextPollTimeoutMillis = -1;
            }
        }
    }
}
```

```java
//分析4：dispatchMessage()
public void dispatchMessage(Message msg) {
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

            //若消息队列里有消息，则根据消息创建的时间插入到队列中
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
    return  sendMessageDelayed(getPostMessage(r), 0); // ---> 分析2：getPostMessage(r)
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
        handleCallback(msg);
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
```

### 6.常见面试题

#### 1.loop为什么不会阻塞，CPU为什么不会忙等？

通过epoll机制监听文件I/O事件，在有message需要处理时，写入数据以唤醒线程，没有message处理时，让线程进入休眠状态。

#### 2.Message缓存池如何实现缓存？

通过链表缓存，需要时从头部取出，回收时插入头部。

#### 3.子线程如何使用Handler机制？

保证在子线程中先执行`Looper.prepare()`，再调用`Looper.loop()`开始消息循环。

