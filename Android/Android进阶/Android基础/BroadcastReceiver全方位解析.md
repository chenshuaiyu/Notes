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
5. 广播接收者通过消息循环拿到此广播，并回调onReceive()。

**注意：**

广播发送者和广播接收者的执行是**异步**的，发出去的广播不会关心是否有无接收者接收，也不确定接收者到底是何时才能接收到。

#### 四、具体使用

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
    //是否接受其他APP发出的广播，默认值是由receiver中有无intent-filter决定的：如果有intent-filter，默认值为true，否则为false
	android:exported=["true"|"false"]
	android:icon="drawable resource"
	android:label="string resource"
	android:name=""
    //具有相应权限的广播发送者发送的广播才能被此BroadcastReceiver所接收
	android:permission="string"
	//默认为app的进程，可以指定独立进程，Android四大基本组件都可以通过此属性指定自己的独立进程
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
- 广播发送：广播发送者将此广播的意图通过sendBroadcasr()方法发送出去。

##### 4.3.2 广播的类型

- 普通广播
- 系统广播
- 有序广播
- APP应用内广播
- 粘性广播

###### 1.普通广播

- action匹配就会接受广播
- 发送者与接受必须具有相对应的权限

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

- sendOrderedBroadcast(intent)
- 按照广播接收者的优先级（priority）按照顺序接收。
- 先接受广播的接收者可以对广播就行截断（abort）和修改。

###### 4.应用内广播（本地广播）

```java
localBroadcastManager = LocalBroadcastManager.getInstance(this);
//通过LocalBroadcastManager对象进行注册，销毁，发送广播
```

###### 5.粘性广播

在Android5.0中已经失效，所以不建议使用。