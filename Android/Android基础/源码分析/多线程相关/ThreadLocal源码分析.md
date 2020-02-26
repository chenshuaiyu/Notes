# ThreadLocal源码分析

### 1.概述

1. 定义：线程的局部变量。
2. 作用：为每个线程提供一个特定空间，以保存该线程所独享的资源。
3. 具体应用：Looper的存储。

### 2. 使用

```java
//1.创建ThreadLocal实例
//线程消失后，其线程局部实例的所有副本都会被垃圾回收
ThreadLocal mThreadLocal = new ThreadLocal();
ThreadLocal<String> mThreadLocal1 = new ThreadLocal<>();
ThreadLocal<String> mThreadLocal2 = new ThreadLocal<String>(){
    @Override
    protected String initialValue() {
        return "init value";
    }
};

//2.设置值
mThreadLocal.set("string");
        
//3.获取值
String s = (String) mThreadLocal.get();
```

```java
ThreadLocal<Boolean> mBooleanThreadLocal = new ThreadLocal<Boolean>();
mBooleanThreadLocal.set(true);
Log.d(TAG, "Main: " + mBooleanThreadLocal.get());
new Thread("Thread#1") {
    @Override
    public void run() {
        mBooleanThreadLocal.set(false);
        Log.d(TAG, "Thread#1: " + mBooleanThreadLocal.get());
    }
};
new Thread("Thread#2") {
    @Override
    public void run() {
        Log.d(TAG, "Thread#2: " + mBooleanThreadLocal.get());
    }
};
```

输出结果：

```
Main: true
Thread#1: false
Thread#2: null
```

### 3. 源码分析

ThreadLocal中存在`ThreadLocalMap`类，用于存储每个线程的变量。

- key：当前ThreadLocal实例
- value：需存储的值

```java
public class ThreadLocal<T> {
    
    ...
    
    public void set(T value) {
        Thread t = Thread.currentThread();
        ThreadLocalMap map = getMap(t);
        if (map != null)
            map.set(this, value);
        else
            createMap(t, value);
    }
    
    public T get() {
        Thread t = Thread.currentThread();
        ThreadLocalMap map = getMap(t);
        if (map != null) {
            ThreadLocalMap.Entry e = map.getEntry(this);
            if (e != null) {
                @SuppressWarnings("unchecked")
                T result = (T)e.value;
                return result;
            }
        }
        return setInitialValue();
    }
    
    private T setInitialValue() {
        T value = initialValue();
        Thread t = Thread.currentThread();
        ThreadLocalMap map = getMap(t);
        if (map != null)
            map.set(this, value);
        else
            createMap(t, value);
        return value;
    }
    
    void createMap(Thread t, T firstValue) {
        t.threadLocals = new ThreadLocalMap(this, firstValue);
    }

    ThreadLocalMap getMap(Thread t) {
        return t.threadLocals;
    }
    
    
    static class ThreadLocalMap {

        static class Entry extends WeakReference<ThreadLocal<?>> {
            Object value;

            Entry(ThreadLocal<?> k, Object v) {
                super(k);
                value = v;
            }
        }
        
        private static final int INITIAL_CAPACITY = 16;
        private Entry[] table;
        private int size = 0;
        private int threshold;
    }
}
```

```java
public class Thread implements Runnable {
    ...
    ThreadLocal.ThreadLocalMap threadLocals = null;
}
```

### 4. ThreadLocal如何做到线程安全

1. 每个Thread都有自己独立的ThreadLocalMap实例。
2. 访问ThreadLocal变量时，访问的都是各自线程的ThreadLocalMap。
3. ThreadLocalMap = 当前ThreadLocal实例
