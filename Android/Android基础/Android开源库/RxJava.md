# RxJava

### 一、简介

基于事件流的链式调用，实现异步操作的库。类似于AsyncTask、Handle的作用。

RxJava是基于一种拓展的观察者模式，有四种角色：

|          角色          |            作用            |
| :--------------------: | :------------------------: |
| 被观察者（Observable） |          产生事件          |
|   观察者（Observer）   |  接收事件，并给出相应事件  |
|    订阅（Subscibe）    |    连接被观察者和观察者    |
|     事件（Event）      | 连接被观察者和观察者的纽带 |

被观察者（Observable）订阅（Subscibe）按顺序发送事件给观察者（Observer），观察者（Observer）按顺序接收事件作出相应的响应动作。

```java
Observer<Integer> observer = new Observer<Integer>() {
    @Override
    public void onSubscribe(Disposable d) {
        Log.d(TAG, "onSubscribe: ");
    }

    @Override
    public void onNext(Integer integer) {
        Log.d(TAG, "onNext: " + integer);
    }

    @Override
    public void onError(Throwable e) {
        Log.d(TAG, "onError: ");
    }

    @Override
    public void onComplete() {
        Log.d(TAG, "onComplete: ");
    }
};

Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
    @Override
    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
        emitter.onNext(1);
        emitter.onNext(2);
        emitter.onNext(3);
        emitter.onComplete();
    }
});

observable.subscribe(observer);
```

运行结果：

```
MainActivity: onSubscribe: 
MainActivity: onNext: 1
MainActivity: onNext: 2
MainActivity: onNext: 3
MainActivity: onComplete: 
```

### 二、操作符

#### 1.创建操作符

| 基本创建 | 快速创建 & 发送事件 |    延迟创建     |
| :------: | :-----------------: | :-------------: |
| create() |       just()        |     defer()     |
|          |     fromArray()     |     timer()     |
|          |    fromIterator     |   interval()    |
|          |       never()       | intervalRange() |
|          |       empty()       |     range()     |
|          |       error()       |   rangeLong()   |

##### 1.基本创建

###### create()

```java
Observable.create(new ObservableOnSubscribe<Integer>() {
    @Override
    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
        emitter.onNext(1);
        emitter.onNext(2);
        emitter.onNext(3);
        emitter.onComplete();
    }
}).subscribe(new Observer<Integer>() {
    @Override
    public void onSubscribe(Disposable d) {
        Log.d(TAG, "onSubscribe: ");
    }

    @Override
    public void onNext(Integer integer) {
        Log.d(TAG, "onNext: " + integer);
    }

    @Override
    public void onError(Throwable e) {
        Log.d(TAG, "onError: ");
    }

    @Override
    public void onComplete() {
        Log.d(TAG, "onComplete: ");
    }
});
```

##### 2.快速创建

###### just()

```java
Observable.just(1, 2, 3, 4).subscribe(new Observer<Integer>() {
    @Override
    public void onSubscribe(Disposable d) {
        Log.d(TAG, "onSubscribe: ");
    }

    @Override
    public void onNext(Integer integer) {
        Log.d(TAG, "onNext: " + integer);
    }

    @Override
    public void onError(Throwable e) {
        Log.d(TAG, "onError: ");
    }

    @Override
    public void onComplete() {
        Log.d(TAG, "onComplete: ");
    }
});
```

用于测试的方法：

```java
// 观察者接收后会直接调用onCompleted()
Observable observable1=Observable.empty(); 
// 即观察者接收后会直接调用onError()
Observable observable2=Observable.error(new RuntimeException());
// 观察者接收后什么都不调用
Observable observable3=Observable.never();
```

##### 3.延迟创建

需求场景：

1. 定时操作：经过几秒后执行操作。
2. 周期性操作：每隔几秒后，需要执行操作

###### defer()

```java
private Integer i = 10;

Observable<Integer> observable = Observable.defer(new Callable<ObservableSource<? extends Integer>>() {
    @Override
    public ObservableSource<? extends Integer> call() throws Exception {
        return Observable.just(i);
    }
});

i = 15;

observable.subscribe(new Observer<Integer>() {
    @Override
    public void onSubscribe(Disposable d) {
        Log.d(TAG, "onSubscribe: ");
    }

    @Override
    public void onNext(Integer integer) {
        Log.d(TAG, "onNext: " + integer);
    }

    @Override
    public void onError(Throwable e) {
        Log.d(TAG, "onError: ");
    }

    @Override
    public void onComplete() {
        Log.d(TAG, "onComplete: ");
    }
});
```

输出结果：

```
MainActivity: onSubscribe: 
MainActivity: onNext: 15
MainActivity: onComplete: 
```

###### timer()

```java
Observable.timer(2, TimeUnit.SECONDS).subscribe(new Observer<Long>() {
    @Override
    public void onSubscribe(Disposable d) {
        Log.d(TAG, "onSubscribe: ");
    }

    @Override
    public void onNext(Long aLong) {
        Log.d(TAG, "onNext: " + aLong);
    }

    @Override
    public void onError(Throwable e) {
        Log.d(TAG, "onError: ");
    }

    @Override
    public void onComplete() {
        Log.d(TAG, "onComplete: ");
    }
});
```

输出结果：

```
MainActivity: onSubscribe: 
MainActivity: onNext: 0
MainActivity: onComplete: 
```

###### intervalRange()

```java
Observable.interval(3, 1, TimeUnit.SECONDS).subscribe(new Observer<Long>() {
    @Override
    public void onSubscribe(Disposable d) {
        Log.d(TAG, "onSubscribe: ");
    }

    @Override
    public void onNext(Long aLong) {
        Log.d(TAG, "onNext: " + aLong);
    }

    @Override
    public void onError(Throwable e) {
        Log.d(TAG, "onError: ");
    }

    @Override
    public void onComplete() {
        Log.d(TAG, "onComplete: ");
    }
});
```

```
onSubscribe: 
onNext: 0
onNext: 1
onNext: 2
```

#### 2.功能性操作符

辅助被观察者在发送事件是实现一些功能性需求（如错误处理、线程调度）。

##### 1.连接被观察者和观察者 subscribe()

```java
Observable.create(new ObservableOnSubscribe<Integer>() {
    @Override
    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
        emitter.onNext(1);
        emitter.onNext(2);
        emitter.onNext(3);
        emitter.onComplete();
    }
}).subscribe(new Observer<Integer>() {
    @Override
    public void onSubscribe(Disposable d) {
        Log.d(TAG, "onSubscribe: ");
    }

    @Override
    public void onNext(Integer integer) {
        Log.d(TAG, "onNext: " + integer);
    }

    @Override
    public void onError(Throwable e) {
        Log.d(TAG, "onError: ");
    }

    @Override
    public void onComplete() {
        Log.d(TAG, "onComplete: ");
    }
});
```

##### 2.线程操作



##### 3.延迟操作 delay()

```java
Observable.just(1, 2, 3)
        .delay(3, TimeUnit.SECONDS)
        .subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "onNext: " + integer);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: ");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
            }
        });
```

##### 4.在事件的声明周期中操作 do()

在事件的声明周期中调用。

```java
Observable.create(new ObservableOnSubscribe<Integer>() {
    @Override
    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
        emitter.onNext(1);
        emitter.onNext(2);
        emitter.onNext(3);
        emitter.onError(new Throwable("发生错误"));
    }
}).doOnEach(new Consumer<Notification<Integer>>() {
    @Override
    public void accept(Notification<Integer> integerNotification) throws Exception {
        Log.d(TAG, "doOnEach: " + integerNotification.getValue());
    }
}).doOnNext(new Consumer<Integer>() {
    @Override
    public void accept(Integer integer) throws Exception {
        Log.d(TAG, "doOnNext: " + integer);
    }
}).doAfterNext(new Consumer<Integer>() {
    @Override
    public void accept(Integer integer) throws Exception {
        Log.d(TAG, "doAfterNext: " + integer);
    }
}).doOnComplete(new Action() {
    @Override
    public void run() throws Exception {
        Log.d(TAG, "doOnComplete: ");
    }
}).doOnError(new Consumer<Throwable>() {
    @Override
    public void accept(Throwable throwable) throws Exception {
        Log.d(TAG, "doOnError: " + throwable);
    }
}).doOnSubscribe(new Consumer<Disposable>() {
    @Override
    public void accept(Disposable disposable) throws Exception {
        Log.d(TAG, "doOnSubscribe: ");
    }
}).doAfterTerminate(new Action() {
    @Override
    public void run() throws Exception {
        Log.d(TAG, "doAfterTerminate: ");
    }
}).doFinally(new Action() {
    @Override
    public void run() throws Exception {
        Log.d(TAG, "doFinally: ");
    }
}).subscribe(new Observer<Integer>() {
    @Override
    public void onSubscribe(Disposable d) {
        Log.d(TAG, "onSubscribe: ");
    }

    @Override
    public void onNext(Integer integer) {
        Log.d(TAG, "onNext: " + integer);
    }

    @Override
    public void onError(Throwable e) {
        Log.d(TAG, "onError: " + e);
    }

    @Override
    public void onComplete() {
        Log.d(TAG, "onComplete: ");
    }
});
```

输出结果：

```
MainActivity: doOnSubscribe: 
MainActivity: onSubscribe: 
MainActivity: doOnEach: 1
MainActivity: doOnNext: 1
MainActivity: onNext: 1
MainActivity: doAfterNext: 1
MainActivity: doOnEach: 2
MainActivity: doOnNext: 2
MainActivity: onNext: 2
MainActivity: doAfterNext: 2
MainActivity: doOnEach: 3
MainActivity: doOnNext: 3
MainActivity: onNext: 3
MainActivity: doAfterNext: 3
MainActivity: doOnEach: null
MainActivity: doOnError: java.lang.Throwable: 发生错误
MainActivity: onError: java.lang.Throwable: 发生错误
MainActivity: doFinally: 
MainActivity: doAfterTerminate: 
```

##### 5.错误处理

- onErrorReturn()
- onErrorResumeNext()
- onExceptionResumeNext()
- retry()
- retryUntil()
- retryWhen

##### 6.重复发送

- repeat()
- repeatWhen()

#### 3.过滤操作符

过滤事件

|        指定条件        | 指定事件数量 |        指定时间        | 指定事件位置       |
| :--------------------: | :----------: | :--------------------: | ------------------ |
|        filter()        |    take()    |    throttleFirst()     | firstElement()     |
|        ofType()        |  takeLast()  |     throttleLast()     | lastElement()      |
|         skip()         |              |        Sample()        | elementAt()        |
|       skipLast()       |              | throttleWithTimeout () | elementAtOrError() |
|       distinct()       |              |       debounce()       |                    |
| distinctUntilChanged() |              |                        |                    |

#### 4.组合/合并操作符

- 组合多个被观察者（Observable
- 合并需要发送的事件

|  组合多个被观察者  |       合并多个事件        | 发送事件前追加发送事件 | 统计发送事件数量 |
| :----------------: | :-----------------------: | :--------------------: | :--------------: |
|      concat()      |           zip()           |      startWith()       |     count()      |
|   concatArray()    |      combineLatest()      |    startWithArray()    |                  |
|      merge()       | combineLatestDelayError() |                        |                  |
|    mergeArray()    |         reduce()          |                        |                  |
| concatDelayError() |         collect()         |                        |                  |
| mergeDelayError()  |                           |                        |                  |

#### 5.变换操作符



#### 6.条件/布尔操作符



#### 7.线程操作

作用：指定被观察者和观察者的工作线程类型。

由于被观察者/观察者工作线程 = 创建自身的线程，所以发送接收相应事件全部发生在主线程中。

目标：

- 被观察者：在子线程中生产事件（耗时操作）。
- 观察者：主线程接收，响应事件（UI操作）。

实现方式：

RxJava内置线程调度器：`Scheduler` ，通过功能性操作符`subscribeOn()`和`observeOn()`实现。

- subscribeOn：指定被观察者（Observable）的工作线程类型。
- observeOn：指定观察者（Observer） 的工作线程类型。

RxJava中内置的线程类型（内部使用线程池来实现）：

| 类型                           | 含义                  | 应用场景                         |
| ------------------------------ | --------------------- | -------------------------------- |
| Schedulers.immediate()         | 当前线程 = 不指定线程 | 默认                             |
| AndroidSchedulers.mainThread() | Android主线程         | 操作UI                           |
| Schedulers.newThread()         | 常规新线程            | 耗时等操作                       |
| Schedulers.io()                | io操作线程            | 网络请求、读写文件等io密集型操作 |
| Schedulers.computation()       | CPU计算操作线程       | 大量计算操作                     |

```java
observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
```

注意：

1. Observable.subscribeOn()多次指定被观察者 生产事件的线程，则只有第一次指定有效，其余的指定线程无效。
2. Observable.observeOn()多次指定观察者接收，响应事件的线程，则每次指定均有效，即每指定一次，就会进行一次线程的切换