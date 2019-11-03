# AsyncTask使用及源码分析

### 1.使用

一个Android封装好的轻量级异步类，是一个抽象类，使用时需继承父类并实现方法。

```java
class Task extends AsyncTask<Params, Progress, Result> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        publishProgress(objects);
        return null;
    }

    @Override
    protected void onProgressUpdate(Progress... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}

Task task = new Task();

task.execute(Params);
```

注意：

1. AsyncTask不与任何组件绑定生命周期，最好在Activity或Fragment的onDestroy()中调用cancel()。
2. 将AsyncTask声明为静态内部类，防止内存泄露。

### 2.源码分析

原理：线程池（线程调度、线程复用、执行任务） + Handler（异步通信）

- SerialExecutor：线程池用于任务的排队，让需要执行的多个耗时任务按顺序排列。
- THREAD_POOL_EXECUTOR：真正的执行任务。
- InternalHandler：用于从工作线程切换到主线程。

#### 1.创建AsyncTask子类实例

```java
Task task = new Task(); // ---> 分析1：AsyncTask构造函数

//分析1：AsyncTask构造函数
public AsyncTask() {
    //一个可存储参数的Callable对象
    mWorker = new WorkerRunnable<Params, Result>() { // ---> 分析1：WorkerRunnable构造方法
        public Result call() throws Exception {
            mTaskInvoked.set(true);
            Result result = null;
            try {
                //设置线程优先级
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                //执行耗时操作
                result = doInBackground(mParams);
                Binder.flushPendingCommands();
            } catch (Throwable tr) {
                //出现异常时设置取消标志
                mCancelled.set(true);
                throw tr;
            } finally {
                //将执行结果发送至主线程
                postResult(result);
            }
            return result;
        }
    };

    mFuture = new FutureTask<Result>(mWorker) { // ---> 分析2：FutureTask构造方法
        //执行完Callable的call()再执行此方法
        @Override
        protected void done() {
            try {
                postResultIfNotInvoked(get()); // ---> 分析3：postResultIfNotInvoked()
            } catch (InterruptedException e) {
                android.util.Log.w(LOG_TAG, e);
            } catch (ExecutionException e) {
                throw new RuntimeException("An error occurred while executing doInBackground()",
                                           e.getCause());
            } catch (CancellationException e) {
                postResultIfNotInvoked(null);
            }
        }
    };
}

//分析1：WorkerRunnable构造方法
private static abstract class WorkerRunnable<Params, Result> implements Callable<Result> {
    Params[] mParams;
}

//分析2：FutureTask构造方法
public FutureTask(Callable<V> callable) {
    if (callable == null)
        throw new NullPointerException();
    this.callable = callable;
    this.state = NEW;
}

//分析3：postResultIfNotInvoked()
private void postResultIfNotInvoked(Result result) {
    final boolean wasTaskInvoked = mTaskInvoked.get();
    //若任务未被执行，将未被调用的任务的结果通过InternalHandler传递到UI线程
    if (!wasTaskInvoked) {
        postResult(result);
    }
}
```

#### 2.调用execute()

```java
task.execute(); // ---> 分析1：execute()

//分析1：execute()
public final AsyncTask<Params, Progress, Result> execute(Params... params) {
    //sDefaultExecutor = SerialExecutor
    return executeOnExecutor(sDefaultExecutor, params); // ---> 分析2：executeOnExecutor()
}

//分析2：executeOnExecutor()
public final AsyncTask<Params, Progress, Result> executeOnExecutor(Executor exec,
                                                                   Params... params) {
    //判断AsyncTask当前状态
    //不是初始化状态
    if (mStatus != Status.PENDING) {
        switch (mStatus) {
            case RUNNING:
                throw new IllegalStateException("Cannot execute task:"
                                                + " the task is already running.");
            case FINISHED:
                throw new IllegalStateException("Cannot execute task:"
                                                + " the task has already been executed "
                                                + "(a task can be executed only once)");
        }
    }
    //设置为运行状态
    mStatus = Status.RUNNING;
	
    //初始化
    onPreExecute();

    mWorker.mParams = params;
    //SerialExecutor执行execute(mFuture)
    exec.execute(mFuture); // ---> 分析3：SerialExecutor.execute(mFuture)

    return this;
}

//分析3：SerialExecutor.execute(mFuture)
//静态内部类，被所有AsyncTask实例公有
private static class SerialExecutor implements Executor {
    //双向队列，容量根据双向数量调节
    final ArrayDeque<Runnable> mTasks = new ArrayDeque<Runnable>();
    Runnable mActive;

    //通过锁保证该队列中的任务是串行执行
    public synchronized void execute(final Runnable r) {
        //一个新任务入队
        mTasks.offer(new Runnable() {
            public void run() {
                try {
                    r.run();
                } finally {
                    scheduleNext();
                }
            }
        });
        
        //若当前无任务执行，取出队列中一个任务执行
        if (mActive == null) {
            scheduleNext();
        }
    }

    protected synchronized void scheduleNext() {
        //取出队头任务
        if ((mActive = mTasks.poll()) != null) {
            //THREAD_POOL_EXECUTOR执行任务
            THREAD_POOL_EXECUTOR.execute(mActive); // ---> 分析4：THREAD_POOL_EXECUTOR.execute()
        }
    }
}

//分析4：THREAD_POOL_EXECUTOR.execute()
public static final Executor THREAD_POOL_EXECUTOR;

private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
private static final int KEEP_ALIVE_SECONDS = 30;

private static final ThreadFactory sThreadFactory = new ThreadFactory() {
    private final AtomicInteger mCount = new AtomicInteger(1);

    public Thread newThread(Runnable r) {
        return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
    }
};

private static final BlockingQueue<Runnable> sPoolWorkQueue =
    new LinkedBlockingQueue<Runnable>(128);

static {
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
        CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
        sPoolWorkQueue, sThreadFactory);
    threadPoolExecutor.allowCoreThreadTimeOut(true);
    THREAD_POOL_EXECUTOR = threadPoolExecutor;
}
```

返回构造函数中，

```java
//分析1：AsyncTask构造函数
public AsyncTask() {
    //一个可存储参数的Callable对象
    mWorker = new WorkerRunnable<Params, Result>() {
        public Result call() throws Exception {
            mTaskInvoked.set(true);
            Result result = null;
            try {
                //设置线程优先级
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                //执行耗时操作
                result = doInBackground(mParams);
                Binder.flushPendingCommands();
            } catch (Throwable tr) {
                //出现异常时设置取消标志
                mCancelled.set(true);
                throw tr;
            } finally {
                //将执行结果发送至主线程
                postResult(result); // ---> 分析2：postResult()
            }
            return result;
        }
    };
}

//分析2：postResult()
private Result postResult(Result result) {
    @SuppressWarnings("unchecked")
    Message message = getHandler().obtainMessage(MESSAGE_POST_RESULT,
                                                 new AsyncTaskResult<Result>(this, result));
    //将消息发送至Handler
    message.sendToTarget(); // ---> 分析3：InternalHandler
    return result;
}

//分析3：InternalHandler
private static class InternalHandler extends Handler {
    public InternalHandler(Looper looper) {
        super(looper);
        //获取主线程Looper
        //super(Looper.getMainLooper());
    }

    @SuppressWarnings({"unchecked", "RawUseOfParameterizedType"})
    @Override
    public void handleMessage(Message msg) {
        AsyncTaskResult<?> result = (AsyncTaskResult<?>) msg.obj;
        switch (msg.what) {
            //通过finish()将结果通过Handler传递到主线程
            case MESSAGE_POST_RESULT:
                result.mTask.finish(result.mData[0]); // ---> 分析4：finish()
                break;
            //回调onProgressUpdate()通知主线程更新进度的操作
            case MESSAGE_POST_PROGRESS:
                result.mTask.onProgressUpdate(result.mData);
                break;
        }
    }
}

//分析4：finish()
private void finish(Result result) {
    if (isCancelled()) {
        onCancelled(result);
    } else {
        onPostExecute(result);
    }
    //设置为完成状态
    mStatus = Status.FINISHED;
}
```

默认情况下，AsyncTask是串行执行的，如果需要并行中，

```java
new Task().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
```

```java
public final AsyncTask<Params, Progress, Result> executeOnExecutor(Executor exec,
        Params... params) {
    if (mStatus != Status.PENDING) {
        switch (mStatus) {
            case RUNNING:
                throw new IllegalStateException("Cannot execute task:"
                        + " the task is already running.");
            case FINISHED:
                throw new IllegalStateException("Cannot execute task:"
                        + " the task has already been executed "
                        + "(a task can be executed only once)");
        }
    }

    mStatus = Status.RUNNING;

    onPreExecute();

    mWorker.mParams = params;
    //直接通过传入的线程池执行任务，一般可传入AsyncTask.THREAD_POOL_EXECUTOR，也就是同步执行的两个线程池中真正执行任务的线程池
    exec.execute(mFuture);

    return this;
}
```