# HandlerThread使用及源码分析

### 1.使用

```java
//创建HandleThread实例对象
mHandlerThread = new HandlerThread("handleThread");

//启动线程
mHandlerThread.start();

//创建工作线程Handler
mHandler = new Handler(mHandlerThread.getLooper()) {
    @Override
    public void handleMessage(Message msg) {

    }
};

//创建消息
Message msg = Message.obtain();
msg.what = 1;
msg.obj = "msg";

//发送消息
mHandler.sendMessage(msg);

//结束线程，停止消息循环
mHandlerThread.quit();
```

**使用实例：**

```java
public class MainActivity extends AppCompatActivity {
    private Handler mMainHandler, mWorkHandler;
    private HandlerThread mHandlerThread;

    TextView mTextView;
    Button mButton1, mButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mTextView = findViewById(R.id.text);
        mButton1 = findViewById(R.id.button1);
        mButton2 = findViewById(R.id.button2);

        mMainHandler = new Handler();

        mHandlerThread = new HandlerThread("handleThread");
        mHandlerThread.start();

        mWorkHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mTextView.setText("HandlerThread");
                    }
                });
            }
        

        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = Message.obtain();
                msg.what = 1;
                msg.obj = "msg";

                mWorkHandler.sendMessage(msg);
            }
        });

        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandlerThread.quit();
            }
        });
    }
}
```

### 2.源码分析

#### 1.创建HandlerThread对象

```java
//1.创建HandlerThread对象
mHandlerThread = new HandlerThread("handleThread"); // ---> 分析1：HandlerThread构造函数

//分析1：HandlerThread构造函数
public class HandlerThread extends Thread {
    int mPriority; //线程优先级
    int mTid = -1; //线程id
    Looper mLooper; //当前线程的Looper

    public HandlerThread(String name) {
        super(name);
        mPriority = Process.THREAD_PRIORITY_DEFAULT;
    }

    public HandlerThread(String name, int priority) {
        super(name);
        mPriority = priority;
    }
    
    ...
}
```

#### 2.启动线程

1. 在当前线程中创建Looper对象、MessageQueue对象
2. 通过锁机制获取当前线程的Looper对象
3. 发出通知：当前线程已经创建好Looper对象
4. 打开消息循环，不断获取消息和分发消息

```java
//2.启动线程
mHandlerThread.start(); // ---> 分析1：HandlerThread.run()

//分析1：HandlerThread.run()
public void run() {
    //获取当前线程id
    mTid = Process.myTid();
    //创建Looper、MessageQueue
    Looper.prepare();
    
    synchronized (this) {
        //获得当前线程的Looper对象
        mLooper = Looper.myLooper();
        
        //发出通知，当前线程已经创建mLooper对象成功，这里主要是通知getLooper()方法中的wait()
        notifyAll();
    }
    //使用持有锁机制 + notifyAll()，是为了保证后面获得Looper对象前就已创建好Looper对象
    
    //设置当前线程优先级
    Process.setThreadPriority(mPriority);
    
    //这里默认是空方法的实现，我们可以重写这个方法来做一些线程开始之前的准备，方便扩展
    onLooperPrepared(); // ---> 分析2：onLooperPrepared()
    
    //开启消息循环，调用next()、dispatchMessage()
    Looper.loop();
    mTid = -1;
}

//分析2：onLooperPrepared()
protected void onLooperPrepared() {
}
```

#### 3.创建工作线程Handler

```java
//3.创建工作线程Handler
mWorkHandler = new Handler(mHandlerThread.getLooper()) { // ---> 分析1：mHandlerThread.getLooper()
    @Override
    public void handleMessage(Message msg) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                
            }
        });
    }
};

//分析1：mHandlerThread.getLooper()
public Looper getLooper() {
    if (!isAlive()) {
        return null
    }
    
	//直到线程创建完Looper之后才能获得Looper对象，Looper未创建成功，阻塞
    synchronized (this) {
        while (isAlive() && mLooper == null) {
            try {
                //直到创建好Looper后，调用notifyAll()，结束wait()
                wait();
            } catch (InterruptedException e) {
            }
        }
    }
    return mLooper;
}
```

Q：getLooper()获得HandlerThread工作线程对应Looper的前提是：1.当线程创建成功，2.对应Looper对象创建成功。

A：`同步锁` + `wait()` + `notifyAll()`，在run()方法中创建Looper对象后，调用`notifyAll()`通知getLooper()的`wait()`结束等待，并且返回Looper对象，使Handler和Looper绑定。

#### 4.使用工作线程Handler向工作线程的消息队列发送消息

```java
//创建消息
Message msg = Message.obtain();
msg.what = 1;
msg.obj = "msg";

//发送消息
mWorkHandler.sendMessage(msg);
```

#### 5.结束线程，停止线程消息循环

```java
//5.结束线程
mHandlerThread.quit(); // ---> 分析1：quit()

//分析1：quit()
//效率高，但线程不安全
public boolean quit() {
    Looper looper = getLooper();
    if (looper != null) {
        looper.quit();
        return true;
    }
    return false;
}

//效率低，但线程安全
public boolean quitSafely() {
    Looper looper = getLooper();
    if (looper != null) {
        looper.quitSafely();
        return true;
    }
    return false;
}

//最终调用MessageQueue.quit()
void quit(boolean safe) {
    if (!mQuitAllowed) {
        throw new IllegalStateException("Main thread not allowed to quit.");
    }

    synchronized (this) {
        if (mQuitting) {
            return;
        }
        mQuitting = true;

        if (safe) {
            removeAllFutureMessagesLocked(); // ---> 分析1：removeAllFutureMessagesLocked()
        } else {
            removeAllMessagesLocked(); // ---> 分析2：removeAllMessagesLocked()
        }
        
        nativeWake(mPtr);
    }
}

//分析1：removeAllFutureMessagesLocked()
private void removeAllFutureMessagesLocked() {
    final long now = SystemClock.uptimeMillis();
    Message p = mMessages;
    //判断是否正在处理消息
    if (p != null) {
        if (p.when > now) {
            //不在处理消息，重置消息
            removeAllMessagesLocked();
        } else {
            //正在处理消息，等待该消息处理完毕后退出循环
            Message n;
            for (;;) {
                n = p.next;
                if (n == null) {
                    return;
                }
                if (n.when > now) {
                    break;
                }
                p = n;
            }
            p.next = null;
            do {
                p = n;
                n = p.next;
                p.recycleUnchecked();
            } while (n != null);
        }
    }
}

//分析2：removeAllMessagesLocked()
private void removeAllMessagesLocked() {
    Message p = mMessages;
    //遍历链表，重置所有回调，并将链表重置为null
    while (p != null) {
        Message n = p.next;
        p.recycleUnchecked();
        p = n;
    }
    mMessages = null;
}
```