# ThreadLocal

### 1. 概述

1. 定义：线程的局部变量。
2. 作用：为每个线程提供一个特定空间，以保存该线程所独享的资源。
3. 具体应用：Looper的存储。

### 2. 具体使用

```java
//1.直接创建对象
private ThreadLocal mThreadLocal = new ThreadLocal();

//2.创建泛型对象
private ThreadLocal<String> mThreadLocal = new ThreadLocal<>();

//3.创建泛型对象，并初始化
private ThreadLocal<String> mThreadLocal = new ThreadLocal<String>(){
    @Override
    protected String initialValue() {
        return "initial value";
    }
};
```

注意：

1. `ThreadLocal`实例 = 类中的`private`、`static`字段。
2. 只需实例化一次，不需要知道是被哪个线程实例化。
3. 每个线程都保持对其线程局部变量副本的隐式引用
4. 线程消失后，其线程局部实例的所有副本都会被垃圾回收（除非存在对这些副本的其他引用）。
5. 虽然所有的线程都能访问到这个ThreadLocal实例，但是每个线程只能访问到自己通过`ThreadLocal.set()`设置的值。

```java
//1.设置值
mThreadLocal.set("");

//2，读取值
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

### 3. 实现原理











··