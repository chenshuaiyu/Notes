# EventBus

## 一、简介

EventBus是一个Android端优化的publish/subscribe消息总线，简化了应用程序内各组件间、组件与后台线程间的通信。

- Event事件：可以是任何类型的对象。通过事件的发布者将事件进行传递。
- Subscriber事件订阅者：接收特定的事件。
- Publisher事件发布者：用于通知Subscriber有事件发生。可以在任何线程任意位置发送事件。

## 二、使用

### 1.添加依赖

```
implementation 'org.greenrobot:eventbus:3.1.1'
```

### 2.构建Event

```java
public class MessageEvent {
    private String message;

    public MessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
```

### 3.注册/解除事件订阅

```java
EventBus.getDefault().register(this);
```

```java
@Subscribe(threadMode = ThreadMode.MAIN)
public void onHandleMessage(MessageEvent messageEvent) {
    mTextView.setText(messageEvent.getMessage());
}
```

```java
@Override
protected void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
}
```

### 4.发送消息

```java
EventBus.getDefault().post(new MessageEvent("消息"));
```

### 5.线程模式ThreadMode

- PostThread：事件的处理和事件的发送在相同的线程，所以事件处理时间不应太长，不然影响事件的发送线程。
- MainThread：事件的处理会在UI线程中执行。事件处理时间不能太长，否则会出现ANR。
- BackgroundThread：如果事件是在UI线程发布出来的，那么事件处理就会在子线程中运行，如果时间本来就是子线程中发布出来的，那么事件处理直接在该子线程中执行。所有待处理事件会被添加到一个队列中，由对应线程依次处理这些事件，如果某个事件处理时间太长，会阻塞后面事件的派发和处理。
- Async：事件处理会在单独的线程中执行，主要用于在后台线程中执行耗时操作，每个事件会开启一个线程。

### 6.priority事件优先级

事件的优先级类似广播的优先级，优先级越高优先获得消息，

```java
@Subscribe(priority = 100)
public void onHandleMessage(MessageEvent messageEvent) {
    mTextView.setText(messageEvent.getMessage());
}
```

当多个订阅者对同一种事件类型进行订阅时，即对应的事件处理方法中接收的事件类型一致，优先级越高优先获得消息。

EventBus也可以终止对时间继续传递的功能：

```java
@Subscribe(priority = 100)
public void onHandleMessage(MessageEvent messageEvent) {
    mTextView.setText(messageEvent.getMessage());
    //比100低的优先级的订阅者就会接收不到该事件。
    EventBus.getDefault().cancelEventDelivery(event);
}
```

### 7.EventBus黏性事件

订阅在发布事件之后，同样可以收到事件。

```java
@Subscribe(priority = 100, sticky = true)
public void onHandleMessage(MessageEvent messageEvent) {
    mTextView.setText(messageEvent.getMessage());
}
```

```java
//发布黏性事件
EventBus.getDefault().postSticky(new MessageEvent("黏性事件"));
//移除黏性事件
EventBus.getDefault().removeStickyEvent(MessageEvent.class);
//移除所有黏性事件
EventBus.getDefault().removeAllStickyEvents(MessageEvent.class);
```

### 8.EventBus配置

```java
EventBus eventBus = EventBus.builder()
    .logNoSubscriberMessages(false)
    .sendNoSubscriberEvent(false)
    .build();
```

当一个事件没有订阅者时，不会输出log信息，不会发布一条默认信息。

## 三、源码分析

```java
//注册订阅
EventBus.getDefault().register(this);
```

```java
//事件处理
@Subscribe(threadMode = ThreadMode.MainThread)
public void onNewsEvent(NewsEvent event) {
    String message = event.getMessage();
    mTextView.setText(message);
}
```

```java
//发布事件
EventBus.getDefault().post(new NewsEvent("消息"));
```

### getDefault()

```java
static volatile EventBus defaultInstance;

public static EventBus getDefault() {
    if (defaultInstance == null) {
        synchronized (EventBus.class) {
            if (defaultInstance == null) {
                defaultInstance = new EventBus();
            }
        }
    }
    return defaultInstance;
}
```

### register()

```java
//Map<订阅事件，订阅该事件的订阅者集合>
private final Map<Class<?>, CopyOnWriteArrayList<Subscription>> subscriptionsByEventType;

//Map<订阅者，订阅事件集合>
private final Map<Object, List<Class<?>>> typesBySubscriber;

//Map<订阅事件类类型，订阅事件实例对象>
private final Map<Class<?>, Object> stickyEvents;
```

1. 获取订阅者的类类型
2. SubscriberMethodFinder解析订阅者类，获取所有的响应函数集合
3. 遍历订阅函数，执行subscribe()方法

```java
public void register(Object subscriber) {
    Class<?> subscriberClass = subscriber.getClass();
    List<SubscriberMethod> subscriberMethods = subscriberMethodFinder.findSubscriberMethods(subscriberClass);
    synchronized (this) {
        for (SubscriberMethod subscriberMethod : subscriberMethods) {
            subscribe(subscriber, subscriberMethod);
        }
    }
}
```

### subscribe()

1. 获得该事件类型的所有订阅者队列，根据优先级将订阅者信息插入到订阅者队列
2. 得到当前订阅者的所有事件队列，将此事件保存到队列中，用于后续取消队列
3. 检查Sticky事件，从Sticky事件队列中取出该事件类型最后一个事件发送给当前订阅者

```java
private void subscribe(Object subscriber, SubscriberMethod subscriberMethod) {
    //第一步
    
    //获取订阅事件类型
    Class<?> eventType = subscriberMethod.eventType;
    //把通过register()订阅的订阅者包装成Subscription对象
    Subscription newSubscription = new Subscription(subscriber, subscriberMethod);
    //获取该订阅事件的订阅者集合
    CopyOnWriteArrayList<Subscription> subscriptions = subscriptionsByEventType.get(eventType);
    //如果订阅者集合为空，创建新的集合，并把 newSubscription 加入
    if (subscriptions == null) {
        subscriptions = new CopyOnWriteArrayList<>();
        subscriptionsByEventType.put(eventType, subscriptions);
    } else {
        //订阅者集合中已有该订阅者，抛出异常。不能重复订阅
        if (subscriptions.contains(newSubscription)) {
            throw new EventBusException("Subscriber " + subscriber.getClass() + " already registered to event "
                                        + eventType);
        }
    }

    //把新的订阅者按照优先级加入到订阅者集合中
    int size = subscriptions.size();
    for (int i = 0; i <= size; i++) {
        if (i == size || subscriberMethod.priority > subscriptions.get(i).subscriberMethod.priority) {
            subscriptions.add(i, newSubscription);
            break;
        }
    }

    //第二步
    
    //根据订阅者，获得该订阅者订阅的事件类型集合
    List<Class<?>> subscribedEvents = typesBySubscriber.get(subscriber);
    
    //如果事件类型集合为空，创建新的集合，并加入新订阅的事件类型
    if (subscribedEvents == null) {
        subscribedEvents = new ArrayList<>();
        typesBySubscriber.put(subscriber, subscribedEvents);
    }
    subscribedEvents.add(eventType);
    
    //第三步

    //该事件为粘性是事件
    if (subscriberMethod.sticky) {
        //响应订阅事件的父类事件，默认为true
        if (eventInheritance) {
            Set<Map.Entry<Class<?>, Object>> entries = stickyEvents.entrySet();
            //循环获得每个stickyEvent事件
            for (Map.Entry<Class<?>, Object> entry : entries) {
                Class<?> candidateEventType = entry.getKey();
                //是该类的父类
                if (eventType.isAssignableFrom(candidateEventType)) {
                    //该事件类型最新的时间发送给当前订阅者
                    Object stickyEvent = entry.getValue();
                    checkPostStickyEventToSubscription(newSubscription, stickyEvent);
                }
            }
        } else {
            Object stickyEvent = stickyEvents.get(eventType);
            checkPostStickyEventToSubscription(newSubscription, stickyEvent);
        }
    }
}
```

### unregister()

```java
public synchronized void unregister(Object subscriber) {
    //获取该订阅者所有的订阅事件类类型集合
    List<Class<?>> subscribedTypes = typesBySubscriber.get(subscriber);
    if (subscribedTypes != null) {
        for (Class<?> eventType : subscribedTypes) {
            unsubscribeByEventType(subscriber, eventType);
        }
        //删除<订阅者对象，订阅事件类类型集合>
        typesBySubscriber.remove(subscriber);
    } else {
        logger.log(Level.WARNING, "Subscriber to unregister was not registered before: " + subscriber.getClass());
    }
}
```

### unsubscribeByEventType()

```java
private void unsubscribeByEventType(Object subscriber, Class<?> eventType) {
    //获取订阅事件对应的订阅者集合
    List<Subscription> subscriptions = subscriptionsByEventType.get(eventType);
    if (subscriptions != null) {
        int size = subscriptions.size();
        for (int i = 0; i < size; i++) {
            Subscription subscription = subscriptions.get(i);
            //从订阅者集合中删除特定的订阅者
            if (subscription.subscriber == subscriber) {
                subscription.active = false;
                subscriptions.remove(i);
                i--;
                size--;
            }
        }
    }
}
```

### post()

```java
public void post(Object event) {
    //获取当前线程的Posting状态
    PostingThreadState postingState = currentPostingThreadState.get();
    //获取当前线程的事件队列
    List<Object> eventQueue = postingState.eventQueue;
    //将当前事件添加到其事件队列
    eventQueue.add(event);

    //判断新加入的事件是否在分发中
    if (!postingState.isPosting) {
        postingState.isMainThread = isMainThread();
        postingState.isPosting = true;
        if (postingState.canceled) {
            throw new EventBusException("Internal error. Abort state was not reset");
        }
        try {
            //循环处理当前线程队列中的每一个event对象
            while (!eventQueue.isEmpty()) {
                postSingleEvent(eventQueue.remove(0), postingState);
            }
        } finally {
            //处理完重置postingState的一些标识信息
            postingState.isPosting = false;
            postingState.isMainThread = false;
        }
    }
}
```

### postSingleEvent()

```java
private void postSingleEvent(Object event, PostingThreadState postingState) throws Error {
    //分发事件的类型
    Class<?> eventClass = event.getClass();
    boolean subscriptionFound = false;
    //响应订阅事件的父类事件
    if (eventInheritance) {
        //找出当前订阅事件类类型的所有父类的类类型和其实现的接口的类类型
        List<Class<?>> eventTypes = lookupAllEventTypes(eventClass);
        int countTypes = eventTypes.size();
        for (int h = 0; h < countTypes; h++) {
            Class<?> clazz = eventTypes.get(h);
            //发布每个事件到每个订阅者
            subscriptionFound |= postSingleEventForEventType(event, postingState, clazz);
        }
    } else {
        subscriptionFound = postSingleEventForEventType(event, postingState, eventClass);
    }
    if (!subscriptionFound) {
        if (logNoSubscriberMessages) {
            logger.log(Level.FINE, "No subscribers registered for event " + eventClass);
        }
        if (sendNoSubscriberEvent && eventClass != NoSubscriberEvent.class &&
            eventClass != SubscriberExceptionEvent.class) {
            post(new NoSubscriberEvent(this, event));
        }
    }
}
```

### postSingleEventForEventType()

```java
private boolean postSingleEventForEventType(Object event, PostingThreadState postingState, Class<?> eventClass) {
    CopyOnWriteArrayList<Subscription> subscriptions;
    synchronized (this) {
        //获取订阅事件类类型对应的订阅者信息集合（register构建的集合）
        subscriptions = subscriptionsByEventType.get(eventClass);
    }
    if (subscriptions != null && !subscriptions.isEmpty()) {
        for (Subscription subscription : subscriptions) {
            postingState.event = event;
            postingState.subscription = subscription;
            boolean aborted = false;
            try {
                //发布订阅事件给订阅函数
                postToSubscription(subscription, event, postingState.isMainThread);
                aborted = postingState.canceled;
            } finally {
                postingState.event = null;
                postingState.subscription = null;
                postingState.canceled = false;
            }
            if (aborted) {
                break;
            }
        }
        return true;
    }
    return false;
}
```