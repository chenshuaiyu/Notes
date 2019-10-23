# RxJava

### 一、简介

基于事件流的链式调用，实现异步操作的库。类似于AsyncTask、Handler的作用。

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
|          |   fromIterator()    |   interval()    |
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

###### just()：发布10个以下事件

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

###### fromArray()

```java
Integer[] integers = new Integer[]{1, 2, 3, 4, 5, 6};
Observable.fromArray(integers)
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

###### fromIterator()

```java
List<Integer> list = new ArrayList<>();
list.addAll(Arrays.asList(1, 2, 3));
Observable.fromIterable(list)
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

用于测试的方法：

```java
// 观察者接收后会直接调用onCompleted()
Observable observable1 = Observable.empty(); 
// 即观察者接收后会直接调用onError()
Observable observable2 = Observable.error(new RuntimeException());
// 观察者接收后什么都不调用
Observable observable3 = Observable.never();
```

##### 3.延迟创建

需求场景：

1. 定时操作：经过几秒后执行操作。
2. 周期性操作：每隔几秒后，需要执行操作

###### defer()：直到订阅者（Observable）订阅时，才动态创建被观察者对象（Observable）并且发送事件。

```java
Integer i = 10;

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

###### timer()：延迟指定时间后发送一个Long类型数值0

```java
Observable.timer(2, TimeUnit.SECONDS)
        .subscribe(new Observer<Long>() {
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

###### intervalRange()：每隔指定事件就发送事件

```java
//参数1：第一次延迟时间
//参数2：间隔时间
//参数3：时间单位
Observable.interval(3, 1, TimeUnit.SECONDS)
        .subscribe(new Observer<Long>() {
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

###### intervalRange()：每隔指定时间就发送事件，可指定发送的数据数量

```java
//参数1：事件起始点
//参数2：事件数量
//参数3：第一次延迟时间
//参数4：间隔时间
//参数5：时间单位
Observable.intervalRange(3, 10, 3, 1, TimeUnit.SECONDS)
        .subscribe(new Observer<Long>() {
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

###### range()：连续发送一个事件序列，可指定范围

```java
Observable.range(3, 10)
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

###### rangeLong()：与range不同的是，数据类型为Long

此处不再赘述。

#### 2.功能性操作符

辅助被观察者在发送事件时实现一些功能性需求（如错误处理、线程调度）。

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

RxJava中内置的线程类型（内部使用线程池来实现）：

| 类型                           | 含义                  | 应用场景                         |
| ------------------------------ | --------------------- | -------------------------------- |
| Schedulers.immediate()         | 当前线程 = 不指定线程 | 默认                             |
| AndroidSchedulers.mainThread() | Android主线程         | 操作UI                           |
| Schedulers.newThread()         | 常规新线程            | 耗时等操作                       |
| Schedulers.io()                | IO操作线程            | 网络请求、读写文件等io密集型操作 |
| Schedulers.computation()       | CPU计算操作线程       | 大量计算操作                     |

- subscribeOn()：指定被观察者发送事件的线程，多次指定时，只有第一次有效。
- observeOn()：指定观察者接收、响应事件的线程。多次指定时，每调用一次，就切换一次。

```java
observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
```

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

在事件的生命周期中调用。

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

###### onErrorReturn()：发生错误时，发送一个特殊事件，正常终止

```java
Observable.create(new ObservableOnSubscribe<Integer>() {
    @Override
    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
        e.onNext(1);
        e.onNext(2);
        e.onError(new Throwable("发生错误"));
    }
})
        .onErrorReturn(new Function<Throwable, Integer>() {
            @Override
            public Integer apply(Throwable throwable) throws Exception {
                Log.d(TAG, "onErrorReturn: " + throwable.toString());
                return -1;
            }
        })
        .subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(Integer value) {
                Log.d(TAG, "onNext: " + value);
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
onNext: 1
onNext: 2
onErrorReturn: 发生错误
onNext: -1
onComplete: 
```

###### onErrorResumeNext()：拦截Throwable，发送一个Observable

```java
Observable.create(new ObservableOnSubscribe<Integer>() {
    @Override
    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
        e.onNext(1);
        e.onNext(2);
        e.onError(new Throwable("发生错误"));
    }
})
        .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends Integer>>() {
            @Override
            public ObservableSource<? extends Integer> apply(Throwable throwable) throws Exception {
                Log.d(TAG, "onErrorResumeNext: " + throwable.toString());
                return Observable.just(3, 4);
            }
        })
        .subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(Integer value) {
                Log.d(TAG, "onNext: " + value);
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
onNext: 1
onNext: 2
onErrorResumeNext: 发生错误
onNext: 3
onNext: 4
onComplete: 
```

###### onExceptionResumeNext()：拦截Exception，否则将错误传递给观察者的onError()

```java
Observable.create(new ObservableOnSubscribe<Integer>() {
    @Override
    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
        e.onNext(1);
        e.onNext(2);
        e.onError(new Exception("发生错误"));
    }
})
        .onExceptionResumeNext(new Observable<Integer>() {
            @Override
            protected void subscribeActual(Observer<? super Integer> observer) {
                observer.onNext(3);
                observer.onNext(4);
                observer.onComplete();
            }
        })
        .subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(Integer value) {
                Log.d(TAG, "onNext: " + value);
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

```java
onSubscribe: 
onNext: 1
onNext: 2
onNext: 3
onNext: 4
onComplete: 
```

###### retry()



###### retryUntil()



###### retryWhen



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

|  组合多个被观察者  |       合并多个事件        | 发送事件前追加发送事件 | 统计发送事件数量 |
| :----------------: | :-----------------------: | :--------------------: | :--------------: |
|      concat()      |           zip()           |      startWith()       |     count()      |
|   concatArray()    |      combineLatest()      |    startWithArray()    |                  |
|      merge()       | combineLatestDelayError() |                        |                  |
|    mergeArray()    |         reduce()          |                        |                  |
| concatDelayError() |         collect()         |                        |                  |
| mergeDelayError()  |                           |                        |                  |

###### concat() / concatArray()：组合多个观察者，合并后按顺序发送事件，concatArray观察者数量可大于4

```java
Observable.concat(
        Observable.just(1, 2, 3),
        Observable.just(4, 5, 6),
        Observable.just(7, 8, 9),
        Observable.just(10, 11, 12))
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

```
onSubscribe: 
onNext: 1
onNext: 2
onNext: 3
onNext: 4
onNext: 5
onNext: 6
onNext: 7
onNext: 8
onNext: 9
onNext: 10
onNext: 11
onNext: 12
onComplete: 
```

```java
Observable.concatArray(
        Observable.just(1, 2, 3),
        Observable.just(4, 5, 6),
        Observable.just(7, 8, 9),
        Observable.just(10, 11, 12),
        Observable.just(13, 14, 15))
        .subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(Integer integer) {
                Log.d(TAG, "onNext: ");
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

###### merge() / mergeArray()：组合多个被观察者发送数据，合并后按时间线并行执行，mergeArray观察者数量可大于4

```java
Observable.merge(
        Observable.intervalRange(0, 3, 1, 1, TimeUnit.SECONDS),
        Observable.intervalRange(2, 3, 1, 1, TimeUnit.SECONDS))
        .subscribe(new Observer<Long>() {
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
onNext: 2
onNext: 1
onNext: 3
onNext: 2
onNext: 4
```

###### concatDelayError / merge DelayError()

使用concat或merge时，若其中一个被观察者发出onError事件，则会马上终止被观察者继续发送事件。使用此方法，即可将onError事件推迟到事件发送结束后才触发。

```java
Observable.concat(
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onError(new NullPointerException());
                emitter.onComplete();
            }
        }),
        Observable.just(4, 5, 6))
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

```
onSubscribe: 
onNext: 1
onNext: 2
onNext: 3
onError: 
```

```java
Observable.concatArrayDelayError(
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onError(new NullPointerException());
                emitter.onComplete();
            }
        }),
        Observable.just(4, 5, 6))
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

```
onSubscribe: 
onNext: 1
onNext: 2
onNext: 3
onNext: 4
onNext: 5
onNext: 6
onError: 
```

###### zip()：将多个被观察者按照事件序列进行对位合并

```java
Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
    @Override
    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
        Log.d(TAG, "被观察者1发送了事件1");
        emitter.onNext(1);

        Log.d(TAG, "被观察者1发送了事件2");
        emitter.onNext(2);

        Log.d(TAG, "被观察者1发送了事件3");
        emitter.onNext(3);

        //emitter.onComplete();
    }
}).subscribeOn(Schedulers.io());

Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
    @Override
    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
        Log.d(TAG, "被观察者2发送了事件A");
        emitter.onNext("A");

        Log.d(TAG, "被观察者2发送了事件B");
        emitter.onNext("B");

        Log.d(TAG, "被观察者2发送了事件C");
        emitter.onNext("C");

        Log.d(TAG, "被观察者2发送了事件D");
        emitter.onNext("D");

        //emitter.onComplete();
    }
}).subscribeOn(Schedulers.newThread());
//如果不做线程控制，则两个被观察者会在同一线程中工作，即发送事件存在先后顺序，不是同时
```

```java
Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {
    @Override
    public String apply(Integer integer, String s) throws Exception {
        return integer + s;
    }
})
        .subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "onNext: " + s);
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
onSubscribe
被观察者1发送了事件1
被观察者2发送了事件A
onNext: 1A
被观察者1发送了事件2
被观察者2发送了事件B
onNext: 2B
被观察者1发送了事件3
被观察者2发送了事件C
onNext: 3C
被观察者2发送了事件D
```

注意：

1. 尽管被观察者2的事件D没有合并，但是还是会继续发送。
2. 如果被观察者1和2最后都发送onComplete事件，则被观察者2不会发送D事件，

```java
onSubscribe
被观察者1发送了事件1
被观察者2发送了事件A
onNext: 1A
被观察者1发送了事件2
被观察者2发送了事件B
onNext: 2B
被观察者1发送了事件3
被观察者2发送了事件C
onNext: 3C
onComplete：
```

###### combineLatest()： 当两个Observables中的任何一个发送了数据后，将先发送了数据的Observables 的最新（最后）一个数据 与 另外一个Observable发送的每个数据结合，最终基于该函数的结果发送数据 

```java
Observable.combineLatest(
        Observable.just(1L, 2L, 3L),
        Observable.intervalRange(0, 3, 1, 1, TimeUnit.SECONDS),
        new BiFunction<Long, Long, Long>() {
            @Override
            public Long apply(Long o1, Long o2) throws Exception {
                Log.e(TAG, "合并的数据：" + o1 + " " + o2);
                return o1 + o2;
            }
        })
        .subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long s) throws Exception {
                Log.e(TAG, "结果：" + s);
            }
        });
```

```
合并的数据：3 0
结果：3
合并的数据：3 1
结果：4
合并的数据：3 2
结果：5
```

###### combineLatestDelayError()

与concatDelayError类似，不再赘述。

###### reduce()：把被观察者发送的事件聚合成一个事件发送

```java
Observable.just(1, 2, 3, 4)
        .reduce(new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer integer, Integer integer2) throws Exception {
                Log.d(TAG, integer + " × " + integer2);
                return integer * integer2;
            }
        })
        .subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "accept: " + integer);
            }
        });
```

```
1 × 2
2 × 3   
6 × 4
accept: 24  
```

###### collect()： 将被观察者Observable发送的事件收集到一个数据结构里 

```java
Observable.just(1, 2, 3, 4, 5, 6)
        .collect(
                new Callable<ArrayList<Integer>>() {
                    @Override
                    public ArrayList<Integer> call() throws Exception {
                        return new ArrayList<>();
                    }
                }, new BiConsumer<ArrayList<Integer>, Integer>() {
                    @Override
                    public void accept(ArrayList<Integer> list, Integer integer)
                            throws Exception {
                        list.add(integer);
                    }
                })
        .subscribe(new Consumer<ArrayList<Integer>>() {
            @Override
            public void accept(ArrayList<Integer> s) throws Exception {
                Log.e(TAG, "accept：" + s);
            }
        });
```

```
accept：{1, 2, 3, 4, 5, 6}
```

###### startWith() / startWithArray()

```java
Observable.just(4, 5, 6)
        .startWithArray(1, 2, 3)
        .startWith(0)
        .subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "accept: " + integer);
            }
        });
```

```
accept: 0
accept: 1
accept: 2
accept: 3
accept: 4
accept: 5
accept: 6
```

###### count()： 统计被观察者发送事件的数量 

```java
Observable.just(1, 2, 3)
        .count()
        .subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                Log.d(TAG, "accept: " + aLong);
            }
        });
```

```
accept: 3
```

#### 5.变换操作符

- map()
- flatMap()
- concatMap()
- buffer

###### map()：别观察者发送的每一个事件都通过指定函数处理，从而变换成另外一种事件。

```java
Observable.just(1, 2, 3)
        .map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                return integer + "变为String类型";
            }
        })
        .subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "onNext: " + s);
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

###### FlatMap()： 

1. 被观察者发送的每个事件都创建一个Observable对象，并将每个事件转换后的新事件放入对应的Observable对象中。
2. 将每个Observable对象合并为一个新的Observable对象。
3. 新的Observable对象将新的事件序列发送给观察者。

**注意： 新合并生成的事件序列顺序是无序的，与旧序列发送事件的顺序无关。**

```java
Observable.just(1, 2, 3)
        .flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("事件" + integer + "拆分的子事件" + i);
                }
                return Observable.fromIterable(list);
            }
        })
        .subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "onNext: " + s);
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

###### concatMap()

与flatMap()类似，但是有序。

###### buffer()：定时从被观察者需要发送的事件中获取一定数量的事件放到缓存区，然后发送

```java
Observable.just(1, 2, 3, 4, 5)
        .buffer(3, 1)
        .subscribe(new Observer<List<Integer>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<Integer> integers) {
                Log.d(TAG, "size: " + integers.size());
                for (Integer integer : integers) {
                    Log.d(TAG, "" + integer);
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
```

```
size: 3
1
2
3
size: 3
2
3
4
size: 3
3
4
5
size: 2
4
5
size: 1
5
```

#### 6.条件/布尔操作符

- all()
- contains()
- isEmpty()
- amb()
- takeWhile()
- takeUntil()
- skipWhile()
- skipUntil()
- defaultIfEmpty()
- SequenceEqual()