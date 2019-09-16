# Handler内存泄露

### 1.基础知识

内存泄露（Memory Leak）：指本该回收的对象不能被回收而停留在堆内存中。

原因：当一个对象已经不再被使用时，本该被回收但却因为有另外一个正在使用的对象有它的引用从而导致它不能被回收，从而导致内存泄露。

### 2.Handler问题

```java
public class MainActivity extends AppCompatActivity {
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                
            }
        };

        new Thread() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = 1;
                msg.obj = "message";
                mHandler.sendMessage(msg);
            }
        }.start();
    }
}
```

`new Handler()`代码处会出现`内存泄漏警告`，警告原因是该Handler类没有设置为静态类，可能导致持有Handler的外部类产生内存泄露。

### 3.原因

#### 3.1 基础知识

1. 主线程的Looper对象的生命周期与该应用程序的生命周期一致。
2. 在Java中，**非静态内部类**和**匿名内部类**都默认持有外部类的引用。

#### 3.2 原因

1. 未处理Message、正在被处理Message持有Handler实例的引用，非静态内部类和匿名内部类的Handler实例持有外部类的引用，此关系会保持到Handler队列中的所有消息被处理完毕。
2. 在消息队列中有未处理Message或正在被处理Message时，此时需要销毁外部类，但由于存在此引用关系，无法回收MainActivity，导致内存泄漏。

### 4.解决方案

#### 4.1 静态内部类 + 弱引用

原理：将Handler设置为静态内部类，使其不持有外部类的引用，打断引用链；并使用WeakReference弱引用持有Activity实例，弱引用对象具有短暂的生命周期，在垃圾收集器扫描时，一旦发现，不管当前内存空间是否足够，都会回收。

```java
public class MainActivity extends AppCompatActivity {

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new SHandle(this);

        new Thread() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = 1;
                msg.obj = "message";
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    private static class SHandle extends Handler {
        private WeakReference<Activity> mReference;

        public SHandle(Activity activity) {
            mReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }
}
```

#### 4.2 当外部类结束生命周期时，清空Handler消息队列

原因：清空消息队列，没有任何对象引用外部类，Handler生命周期与外部类声明周期同步，不会导致内存泄露。

```java
@Override
protected void onDestroy() {
    super.onDestroy();
    mHandler.removeCallbacksAndMessages(null);
}
```

建议：使用静态内部类 + 弱引用方式，以保证Handler的消息队列中的所有消息都被执行。