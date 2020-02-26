# IntentService源码分析

- 继承自Service，内部使用HandlerThread实现
- 处理异步请求，实现多线程
- 多次启动IntentService时，每个耗时操作则以队列的方式在IntentService的onHandleIntent()中依次执行，执行完依次结束

```java
@Override
public void onCreate() {
    super.onCreate();
    //实例化HandlerThread，HandlerThread继承自Thread，内部持有Looper对象
    HandlerThread thread = new HandlerThread("IntentService[" + mName + "]");
    thread.start();

    //获得工作线程Looper，维护子线程工作队列
    mServiceLooper = thread.getLooper();
    //创建工作线程Handler
    mServiceHandler = new ServiceHandler(mServiceLooper);
}
```

```java
private final class ServiceHandler extends Handler {
    public ServiceHandler(Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
        //IntentService的抽象方法
        onHandleIntent((Intent)msg.obj);
        //结束服务
        stopSelf(msg.arg1);
    }
}

@WorkerThread
protected abstract void onHandleIntent(@Nullable Intent intent);
```

```java
@Override
public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
    onStart(intent, startId);
    return mRedelivery ? START_REDELIVER_INTENT : START_NOT_STICKY;
}

@Override
public void onStart(@Nullable Intent intent, int startId) {
    Message msg = mServiceHandler.obtainMessage();
    msg.arg1 = startId;
    //传入intent
    msg.obj = intent;
    //发送消息
    mServiceHandler.sendMessage(msg);
}
```

```java
@Override
public void onDestroy() {
    mServiceLooper.quit();
}
```

### 细节：ServiceHandler是非静态内部类，是否会存在内存泄漏？

ServiceHandler绑定的是后台线程的Looper，而在Service停止时，Looper退出了，内存很快会被回收，所以不存在内存泄露。