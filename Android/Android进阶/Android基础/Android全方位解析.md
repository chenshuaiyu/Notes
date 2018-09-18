# Android全方位解析

### 一、Activity的生命周期

#### 1.典型的生命周期的了解

![Activity生命周期](E:\Github仓库\Notes\Android\Android进阶\assets\Activity生命周期.png)

个人记忆法：CSRPSD + R

注意：调用finish方法后，回调如下：onDestory()（以在onCreate方法中调用为例，不同方法中回调不同，通常都是在onCreate()方法中调用）

#### 2.特殊情况下的生命周期

##### 1.横竖屏切换

在横竖屏切换的过程中，会发生Activity被销毁并重建的过程。

两个回调：

- onSaveInstanceState
- onRestoreInstanceState

在Activity由于异常情况下终止时，系统会调用onSaveInstanceState来保存当前Activity的状态。这个方法是在onStop之前，但它和onPause没有既定的时序关系，该方法只在Activity被异常终止的情况下调用。当异常终止的Activity被重建以后，系统会调用onRestoreInstanceState，并且把



