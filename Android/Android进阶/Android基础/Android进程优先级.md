# Android进程优先级

Android系统中，当系统内存不足时，Android系统将根据进程的优先级选择杀死一些不太重要的进程。

进程优先级从高到低：

1. 前台进程
2. 可视进程
3. 服务进程
4. 后台进程
5. 空进程

### 1. 前台进程

- 处于正在交互的Activity
- 与前台Activity绑定的service
- 调用了startForeground（）方法的service
- 正在执行onCreate()，onStart()，onDestroy()方法的service
- 进程中包含正在执行的onReceive()方法的BroadcastReceiver

一般前台进程都不会因为内存不足被杀死。

### 2. 可视进程

- 处于前台，但仍然可见的Activity（调用了onPause()而没调用onStop()的Activity）。
- 可见Activity绑定的service。

### 3. 服务进程

- 已经启动的服务

### 4. 后台进程

- 不可见的activity（调用onStop之后的Activity）。

### 5. 空进程

- 任何没有活动的进程