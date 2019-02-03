# BroadcastReceiver全方位解析

### 一、定义

- BroadcastReceiver（广播接收器），属于Android四个组件之一。
- 在Android开发中，BroadcastReceiver的应用场景非常多，是一个全局的监听器。
- Android广播分为两个角色：广播发送者，广播接收者。

### 二、作用

- 用于监听/接收应用发出的广播消息，并作出响应。

应用场景：

1. 不同组件之间通信（包括应用内/不同应用之间）
2. 与Android系统在特定情况下进行通信。
3. 多线程通信

### 三、实现原理

- Android中的广播使用了设计模式中的观察者模式：基于消息的发布/订阅事件模型。

模型中有3个角色：

1. 消息订阅者（广播接受者）
2. 消息发布者（广播发布者）
3. 消息中心（AMS，即Activity Manager Service）

原理描述：

1. 广播接收者通过Binder机制在AMS注册。
2. 广播发送者通过Binder机制向AMS发送广播。
3. AMS根据广播发送者要求，在已注册列表中，寻找合适的广播接收者（寻找依据：IntentFilter/Permission）。
4. AMS将广播发送到合适的广播接收者相应的消息循环队列中。
5. 广播接收者通过消息循环拿到此广播，并回调onReceive()。

**注意：**

广播发送者和广播接收者的执行是异步的，发出去的广播不会关心是否有无接收者接收，也不确定接收者到底是何时才能接收到。

#### 四、具体使用

#### 4.1 自定义广播接收者BroadcastReceiver

- 继承自BroadcastReceiver基类
- 必须复写抽象方法onReceive()方法，默认情况下，广播接收者运行在UI线程，因此，onReceive方法中不能执行耗时操作，否则将导致ANR。

#### 4.2 广播接收器注册

- 静态注册
- 动态注册

##### 4.2.1 静态注册

```xaml
<receiver android:enable=["true"|"false"]
          android:exported=["true"|"false"]
          android:icon="drawable resource"
          android:label="string resource"
          android:name=""
          android:permission="string"
          //默认为app的进程，可以指定独立进程
          android:process="string">
</receiver>
```

##### 4.2.2 动态注册

通过Context.registerReceiver，Context.unregisterReceiver进行动态注册，销毁。

**注意：**

动态广播最好在Activity的onResume()，onPause()注销。

##### 4.2.3 两种注册方式的区别

| 注册方式               | 特点                                 | 应用场景             |
| ---------------------- | ------------------------------------ | -------------------- |
| 静态注册（常驻广播）   | 常驻，不受任何组件的生命周期影响     | 需要时刻监听广播     |
| 动态注册（非常驻广播） | 非常驻，灵活，跟随组件的生命周期变化 | 需要特定时刻监听广播 |

#### 4.3 广播发送者向AMS发送广播

##### 4.3.1 广播的发送

- 广播是用Intent（意图）标识。
- 定义广播的本质：定义广播所具备的意图。
- 广播发送：广播发送者将此

##### 4.3.2 广播的类型















