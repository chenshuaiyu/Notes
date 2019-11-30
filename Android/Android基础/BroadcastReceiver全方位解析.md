# BroadcastReceiver全方位解析

### 一、定义

- BroadcastReceiver（广播接收器），属于Android四大组件之一。
- 在Android开发中，BroadcastReceiver的应用场景非常多，是一个全局的监听器。
- Android广播分为两个角色：广播发送者，广播接收者。

### 二、作用

- 用于监听/接收应用发出的广播消息，并作出响应。

应用场景：

1. 不同组件之间通信（包括应用内/不同应用之间）
2. 与Android系统在特定情况下进行通信（例如当电话呼入时，网络可用时）。
3. 多线程通信

### 三、实现原理

- Android中的广播使用了设计模式中的**观察者模式**：基于消息的发布/订阅事件模型。

模型中有3个角色：

1. 消息订阅者（广播接受者）
2. 消息发布者（广播发布者）
3. 消息中心（AMS，即Activity Manager Service）

原理描述：

1. 广播接收者通过Binder机制在AMS注册。
2. 广播发送者通过Binder机制向AMS发送广播。
3. AMS根据广播发送者要求，在已注册列表中，寻找合适的广播接收者（寻找依据：IntentFilter/Permission）。
4. AMS将广播发送到合适的广播接收者相应的消息循环队列中。
5. 广播接收者通过消息循环拿到此广播，并回调`onReceive()`方法。

**注意：**

广播发送者和广播接收者的执行是**异步**的，发出去的广播不会关心是否有无接收者接收，也不确定接收者到底是何时才能接收到。

### 四、具体使用

#### 4.1 自定义广播接收者BroadcastReceiver

- 继承自BroadcastReceiver基类
- 必须复写抽象方法onReceive()方法，默认情况下，广播接收者运行在UI线程，因此，onReceive方法中不能执行耗时操作，否则将导致ANR。

#### 4.2 广播接收器注册

- 静态注册
- 动态注册

##### 4.2.1 静态注册

```xaml
<receiver
	//是否启用此广播接收器
	android:enable=["true"|"false"]
    //是否接收其他APP发出的广播，默认值是由receiver中有无intent-filter决定的：如果有intent-filter，默认值为true，否则为false
	android:exported=["true"|"false"]
	android:icon="drawable resource"
	android:label="string resource"
	android:name=""
    //具有相应权限的广播发送者发送的广播才能被此BroadcastReceiver所接收
	android:permission="string"
	//默认为app的进程，可以指定独立进程，Android四大基本组件都可以通过此属性指定自己的独立进程
	android:process="string">
    <intent-filter>
    	<action android:name="android.net.conn.CONNECTIVITY_CHANGE" />
    </intent-filter>
</receiver>
```

##### 4.2.2 动态注册

通过`Context.registerReceiver()`，`Context.unregisterReceiver()`进行动态注册，销毁。

**注意：**

1. 动态广播最好在Activity的onResume()注册，onPause()注销。
2. 对于动态广播，有注册必然得有注销，否则会导致内存泄露。
3. 重复注册，重复注销也不允许。

**解释为什么最好在onResume()注册，onPause()注销：**

![Activity生命周期](https://github.com/chenshuaiyu/Notes/blob/master/Android/Android基础/assets/Activity生命周期.png)

Activity生命周期的方法是成对出现的：

- `onCreate()`和`onDestroy()`
- `onStart()`和`onStop()`
- `onResume()`和`onPause()`

**在onResume()注册，onPause()注销是因为onPause()在App死亡前一定会被执行，从而保证广播在App死亡前一定被注销，从而防止内存泄露。**

1. 不在`onCreate()`和`onDestroy()`或`onStart()`和`onStop()`注册、注销是因为：当系统因为内存不足（优先级更高的应用需要内存），要回收Activity占用的资源时，Activity在执行完onPause()方法后就会被销毁，有些生命周期方法onStop()和onDestroy()就不会被执行。当再回到此Activity时，是从onCreate方法开始执行。
2. 假设将广播的注销放在onStop()，onDestroy()方法里的话，有可能在Activity被销毁后还未执行onStop，onDestroy()方法，即广播仍还未注销，从而导致内存泄露。
3. 但是，onPause()一定会被执行，从而保证了广播在App死亡前一定会被注销，从而防止内存泄露。

##### 4.2.3 两种注册方式的区别

| 注册方式               | 特点                                 | 应用场景             |
| ---------------------- | ------------------------------------ | -------------------- |
| 静态注册（常驻广播）   | 常驻，不受任何组件的生命周期影响     | 需要时刻监听广播     |
| 动态注册（非常驻广播） | 非常驻，灵活，跟随组件的生命周期变化 | 需要特定时刻监听广播 |

#### 4.3 广播发送者向AMS发送广播

##### 4.3.1 广播的发送

- 广播是用Intent（意图）标识。
- 定义广播的本质：定义广播所具备的意图（Intent）。
- 广播发送：广播发送者将此广播的意图通过sendBroadcast()方法发送出去。

##### 4.3.2 广播的类型

- 普通广播（Normal Broadcast）
- 系统广播（System Broadcast）
- 有序广播（Ordered Broadcast）
- APP应用内广播（Local Broadcast）
- 粘性广播（Sticky Broadcast）

###### 1.普通广播

- action匹配就会接受广播
- 发送者与接受者必须具有相对应的权限

[有关权限](https://blog.csdn.net/mafei852213034/article/details/79934375)（引用自[丿奔跑的蜗牛](https://me.csdn.net/mafei852213034)）

###### 2.系统广播

| 系统操作                                                     | action                               |
| ------------------------------------------------------------ | ------------------------------------ |
| 监听网络变化                                                 | android.net.conn.CONNECTIVITY_CHANGE |
| 关闭或打开飞行模式                                           | Intent.ACTION_AIRPLANE_MODE_CHANGED  |
| 充电时或电量发生变化                                         | Intent.ACTION_BATTERY_CHANGED        |
| 电池电量低                                                   | Intent.ACTION_BATTERY_LOW            |
| 电池电量充足（即从电量低变化到饱满时会发出广播               | Intent.ACTION_BATTERY_OKAY           |
| 系统启动完成后(仅广播一次)                                   | Intent.ACTION_BOOT_COMPLETED         |
| 按下照相时的拍照按键(硬件按键)时                             | Intent.ACTION_CAMERA_BUTTON          |
| 屏幕锁屏                                                     | Intent.ACTION_CLOSE_SYSTEM_DIALOGS   |
| 设备当前设置被改变时(界面语言、设备方向等)                   | Intent.ACTION_CONFIGURATION_CHANGED  |
| 插入耳机时                                                   | Intent.ACTION_HEADSET_PLUG           |
| 未正确移除SD卡但已取出来时(正确移除方法:设置--SD卡和设备内存--卸载SD卡) | Intent.ACTION_MEDIA_BAD_REMOVAL      |
| 插入外部储存装置（如SD卡）                                   | Intent.ACTION_MEDIA_CHECKING         |
| 成功安装APK                                                  | Intent.ACTION_PACKAGE_ADDED          |
| 成功删除APK                                                  | Intent.ACTION_PACKAGE_REMOVED        |
| 重启设备                                                     | Intent.ACTION_REBOOT                 |
| 屏幕被关闭                                                   | Intent.ACTION_SCREEN_OFF             |
| 屏幕被打开                                                   | Intent.ACTION_SCREEN_ON              |
| 关闭系统时                                                   | Intent.ACTION_SHUTDOWN               |
| 重启设备                                                     | Intent.ACTION_REBOOT                 |

使用系统广播时，只需要在注册广播所接收者是定义相关的action即可，系统会自动进行发送广播。

###### 3.有序广播

- `sendOrderedBroadcast(intent)`
- 有序是对广播接受者而言的。
- 按照广播接收者的优先级（priority）按照顺序接收。
- 先接受广播的接收者可以对广播就行截断（abort）和修改。

###### 4.应用内广播（本地广播）

- 背景：Android中的广播可以跨App直接通信（exported对于有intent-filter情况下默认值为true）。
- 可能出现的问题：1.其他App针对性发出与当前App intent-filter相匹配的广播，由此导致当前App不断接收广播并处理。2.其他App注册与当前App一致的intent-filter用于接收广播，获取广播具体信息。即会出现安全性和效率问题。
- 解决方案：使用应用内广播。

具体使用：

1.将全局广播设置为局部广播

1. 注册广播时将exported属性设置为false，使得非本App内部发出的此广播不被接收。
2. 在广播发送和接收时，增设相应权限，用于权限验证。
3. 发送广播时指定该广播接收器所在的包名，此广播将只会发送到此包中的App内与之相匹配的有效广播接收器（通过`intent.setPackage(packageName)`指定包名）。

2.使用封装好的LocalBroadcastManager

- 通过LocalBroadcastManager发送的应用内广播，只能通过LocalBroadcastManager动态注册，不能静态注册。

```java
localBroadcastManager = LocalBroadcastManager.getInstance(this);
//通过LocalBroadcastManager对象进行注册，销毁，发送广播
```

###### 5.粘性广播

在Android5.0中已经失效，所以不建议使用。

### 五、特别注意

对于不同注册方式的广播接收器回调onReceive（Context context, Intent intent）中的context返回值是不一样的：

- 静态注册（全局+应用内广播）：返回context类型是ReceiverRestrictedContext。
- 全局广播的动态注册：返回context类型是Activity Context。
- 应用内广播的动态注册（LocalBroadcastManager方式）：返回context类型是Application Context。
- 应用内广播的动态注册（非LocalBroadcastManager方式）：返回context类型是：Activity Context。