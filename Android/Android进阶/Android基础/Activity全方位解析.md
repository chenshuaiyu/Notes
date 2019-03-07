# Activity全方位解析

### 一、Activity的生命周期

#### 1.典型的生命周期的了解

![Activity生命周期](https://github.com/chenshuaiyu/Notes/blob/master/Android/Android进阶/assets/Activity生命周期.png)

个人记忆法：CSRPSD + R

注意：调用finish方法后，回调如下：onDestory()（以在onCreate方法中调用为例，不同方法中回调不同，通常都是在onCreate()方法中调用）

#### 2.特殊情况下的生命周期

##### 1.横竖屏切换

在横竖屏切换的过程中，会发生Activity被销毁并重建的过程。

两个回调：

- onSaveInstanceState
- onRestoreInstanceState

在Activity由于异常情况下终止时，系统会调用onSaveInstanceState来保存当前Activity的状态。这个方法是**在onStop之前**，但它和onPause没有既定的时序关系，该方法只在Activity被异常终止的情况下调用。当异常终止的Activity被重建以后，系统会调用onRestoreInstanceState，并且把Activity销毁时onSaveInstanceState方法保存的Bundle对象参数同时传递给onRestoreInstanceState方法来恢复Activity的状态，该方法的调用时机是**在onStart之后**。

**onCreate和onRestoreInstanceState方法来恢复Activity状态的区别：**

- onRestoreInstanceState表示Bundle对象非空，不必加非空判断
- onCreate需要非空判断。

建议使用onRestoreInstanceState。

**1.横竖屏切换的生命周期：**

```
onPause()
onSaveInstanceState()
onStop()
onDestroy()
onCreate()
onStart()
onRestoreInstanceState
onResume()
```

可以通过在Manifest文件的Activity属性中指定如下属性：

```
android:configChanges = "orientation | screenSize"
```

来避免横竖屏切换，Activity的销毁和重建，而是调用了下面的方法：

```java
@Override
public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
}
```

**2.资源内存不足导致优先级低的Activity被杀死**

1. 前台Activity：正在和用户交互的Activity，优先级最高。
2. 可见但非前台Activity：比如Activity中弹出了一个对话框，导致Activity可见但是位于后台无法与用户交互。
3. 后台Activity：已经被暂停的Activity，比如执行了onStop，优先级最低。

### 二、Activity的启动模式

#### 1.启动模式的类别

- 标准模式（standard）
- 栈顶复用模式（singleTop）
- 栈内复用模式（singleTask）
- 单例模式（singleInstance）

#### 2.启动模式的结构——栈

Activity的管理是采用任务栈的形式，采用后进先出的栈结构。

##### 1.标准模式（standard）

每启动一次Activity，就会创建一个新的Activity实例并置于栈顶。谁启动了这个Activity，这个Activity就会在启动它的Activity所在的栈中。

**特殊情况：**

如果在Service或Application中启动一个新的Activity，并没有所谓的任务栈，可以使用标记为FLAG解决。为待启动的Activity指定`FLAG_ACTIVITY_NEW_TASK`标记位，创建一个新栈。

##### 2.栈顶复用模式（singleTop）

如果需要新建的Activity位于任务栈栈顶，那么此Activity的实例就不会创建，而是用栈顶的实例。并回调如下方法：

```java
@Override
protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
}
```

##### 3.栈内复用模式（singleTask）

就是一种单例模式，即一个栈内只有一个该Activity实例。可以通过AndroidManifest文件的Activity中指定该Activity需要加载到那个栈中，singleTask和taskAffinity配合使用，指定开启的Activity加入到哪个栈中。

```xml
<activity android:name = ".Activity1"
          android:launchMode = "singleTask"
          android:taskAffinity = "com.lvr.task"
          android:label = "#string/app_name">
</activity>
```

taskAffinity：

每个Activity都有taskAffinity属性，这个属性指出想去的Task。如果一个Activity没有显式的指明该Activity的taskAffinity，那么属性就等于Application指明的taskAffinity，如果Application没有指明taskAffinity，那么就等于包名。

执行逻辑：

在这种模式下，如果该Activity的指定栈不存在，则创建一个栈，并把创建的Activity压入栈内。如果指定栈存在，如果没有该实例，则会创建Activity并压入栈顶，如果其中有该Activity实例，则把该Activity实例之上的Activity杀死清除出栈，重用该Activity实例处在栈顶，然后调用onNewIntent()方法。

##### 4.单例模式（singleInstance）

栈内复用模式的加强版，直接创建一个新的任务栈，并创建一个Activity实例放入栈中。一旦该模式的Activity实例已经存在于某个栈中，任何应用再激活该Activity时都会重用该栈中的实例。

### 3.特殊情况

加入目前有两个任务栈，前台任务栈为AB，后台任务栈为CD，这里假设CD的启动模式均为singleTask，现在请求启动D，这个后台的任务栈都会切换到前台，这时候整个后退列表就成了ABCD。用户按back返回时，一一出栈。

如果是请求C，D销毁，C进入前台栈。

调用singleTask模式的后台任务栈，会把整个Activity压入当前栈。同时会具有clearTop特性，把之上的栈内Activity清除。

### 4.Activity的Flags

可以在启动Activity时，通过Intent.addFlags()方法设置。

1. FLAG_ACTIVITY_NEW_TASK：效果与指定singleTask模式一致。
2. FLAG_ACTIVITY_SINGLE_TOP：效果与指定singleTop模式一致。
3. FLAG_ACTIVITY_CLEAR_TOP：当它启动时，在同一个任务栈中所有位于它上面的Activity都要出栈。1.与singleTask模式配合使用，**与singleTask效果一样**。2.与singleTop模式配合使用，**则清除之上的Activity，并调用该Activity的onNewIntent方法**。3.与standard模式配合使用，**该Activity连同之上的所有Activity出栈，然后创建新的Activity实例并压入栈中**。
