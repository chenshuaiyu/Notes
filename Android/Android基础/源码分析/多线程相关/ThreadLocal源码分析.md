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
        
        private static final int INITIAL_CAPACITY = 16;//初始容量为16，必须为2^n
        private int size = 0;//table中Entry的数量
        private int threshold;//扩容阈值, 当size >= threshold时就会触发扩容逻辑
        private Entry[] table;//table数组
        
        private void setThreshold(int len) {
            //长度的2/3
            threshold = len * 2 / 3;
        }
        
        //threadLocalHashCode用于计算index
        private final int threadLocalHashCode = nextHashCode();//threadLocalHashCode的值等于nextHashCode方法的返回值
        private static AtomicInteger nextHashCode = new AtomicInteger();
        private static final int HASH_INCREMENT = 0x61c88647;
        private static int nextHashCode() {
            //每次调用nextHashCode方法都会在原本的int值加上0x61c88647后再返回
            return nextHashCode.getAndAdd(HASH_INCREMENT);
        }
    }
}
```

计算index：

```java
int i = key.threadLocalHashCode & (len-1);
//当出现冲突时，ThreadLocalMap是使用线性探测法来解决冲突的，即如果i位置已经有了key-value映射，就会在i + 1位置找(到达len返回0)，直到找到一个合适的位置。
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

### 5.脏槽

Entry 继承至 **WeakReference**，并且它的**key是弱引用**，但是**value是强引用**，所以如果**key关联的ThreadLocal实例**没有强引用，只有弱引用时，在gc发生时，ThreadLocal实例就会被gc回收，当ThreadLocal实例被gc回收后，由于value是强引用，导致table数组中存在着**null - value**这样的映射，称之为**脏槽**，这种脏槽会浪费table数组的空间，所以需要及时清除，所以ThreadLocalMap 中提供了**expungeStaleEntry**方法和**expungeStaleEntries**方法去清理这些脏槽，每次ThreadLocalMap 运行getEntry、set、remove等方法时，都会主动的间接使用这些方法去清理脏槽，从而释放更多的空间，避免无谓的扩容操作。