# ANR

## 目录

1. 什么是ANR。
2. 为什么会产生ANR。
3. 如何避免ANR。
4. ANR分析。
5. ANR的处理。
6. 如何避免ANR。
7. 源码分析

------

### 1.什么是ANR。

Application Not Responding，应用无响应，当操作在一段时间内系统无法处理时，系统层面会弹出ANR对话框。

### 2.为什么会产生ANR。

在Android里，App的响应能力是由Activity Manager和Window Manager系统服务来监控的。

通常两种情况下会弹出ANR对话框：

1. 5s内无法响应用户输入事件（例如键盘输入，触摸屏幕）。
2. BroadcastReceiver在10s内无法结束。

造成以上两种情况的首要原因是：**在主线程中做了太多的阻塞耗时操作**，例如文件读写，数据库读写，网络查询等。

### 3.如何避免ANR。

**不要在主线程中做繁重的操作。**

### 4.ANR分析。

1. 普通阻塞导致的ANR
2. CPU满负荷
3. 内存原因

### 5.ANR的处理。

1. 主线程阻塞：开辟单独的子线程来处理耗时阻塞事务。
2. CPU满负荷，IO阻塞：IO阻塞一般来说就是文件读写或者数据库执行在主线程了，也可以通过开辟子线程的方式异步执行。
3. 内存不够：增大VM内存，使用largeHeap属性，排查内存泄露。

### 6.如何避免ANR。

#### 6.1 哪些地方是执行在主线程的

1. Activity的所有生命周期回调都是执行在主线程的。
2. Service默认执行在主线程。
3. BroadcastReceiver的onReceive回调是执行在主线程的。
4. 没有使用子线程的Looper的Handler的handlerMessage，post(Runnable)是执行在主线程中的。
5. AsyncTask的回调中除了doInBackground，其余都是执行在主线程中的。
6. View.post(Runnable)是执行在主线程的。

#### 6.2 使用子线程的方式

1. 继承Thread、实现Runnable
2. 使用AsyncTask
3. HandlerThread
4. IntentService

注意：

使用Thread和HandlerThread时，为了使效果更好，建议设置Thread的优先级偏低一点：

```java
Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND);
```

因为如果没有做任何优先级设置的话，创建的Thread默认和UI Thread是具有同样的优先级的，同样的优先级的Thread，CPU调度上还是可能会阻塞掉你的UI Thread，导致ANR的。

### 7.源码分析

```java
//ActivityManagerService
static final int SHOW_NOT_RESPONDING_UI_MSG = 2;

//弹出ANR对话框的条件就是AMS中的UiHandler收到了what为SHOW_NOT_RESPONDING_UI_MSG的消息
case SHOW_NOT_RESPONDING_UI_MSG: {
    //创建一个AppNotRespondingDialog(系统的自定义Dialog)，最终会以TYPE_SYSTEM_ERROR的方式弹出
    mAppErrors.handleShowAnrUi(msg);
}

//发送请求弹出ANR对话框
mService.mUiHandler.sendMessage(msg);
//调用此代码的四个地方：
//	1.ActiveServices的serviceTimeout方法：后台服务超时
//	2.ActiveServices的serviceForegroundTimeout方法：前台服务超时
//	3.ActivityManagerService的appNotRespondingViaProvider方法：ContentProvider造成
//	4.ActivityManagerService的inputDispatchingTimedOut方法：input事件分派的时候超时(处理事件时被阻塞)所发出的，input事件，有两种，一种是KeyEvent(按键)，另一种是MotionEvent(触摸)。

//在input事件分派超时的时候，有两种情况不会弹框，1.处于debug时，2.来自子进程，这种情况下回直接kill子进程
```

