# Fragment详解

### 1. Fragment

可以将Fragment理解为显示在Activity中的Activity，它可以显示在Activity中，也拥有自己的生命周期，可以接受处理用户的事件，并且可以在Acitvity中动态的添加替换移除不同的Fragement。

### 2. 生命周期

#### 1.运行状态

当一个碎片是可见的，并且它所关联的活动正处于运行状态时，该碎片也处于运行状态。

#### 2.暂停状态

当一个活动进入暂停状态时（由于另一个未占满屏幕的活动被添加到了栈顶），与它相关联的可见碎片就会进入到暂停状态。

#### 3.停止状态

- 当一个活动进入停止状态时，与它相关联的碎片就会进入到停止状态。
- 通过调用FragmentTransaction的remove()、replace()方法将碎片从活动中移除，但如果在事务提交之前调用addToBackStack()方法，这时的碎片也会进入到停止状态。

进入停止状态的碎片对用户来说是完全不可见的，有可能会被系统回收。

#### 4.销毁状态

- 当活动被销毁时，与它相关联的碎片就会进入到销毁状态。
- 通过调用FragmentTransaction的remove()、replace()方法将碎片从活动中移除，但如果在事务提交之前并没有调用addToBackStack()方法，这时的碎片也会进入到销毁状态。

![Fragment生命周期](https://github.com/chenshuaiyu/Notes/blob/master/Android/Android基础/assets/Fragment生命周期.png)

```java
onAttach()//当Fragment与Activity建立关联的时候调用
onCreate()
onCreateView()//为Fragment创建视图的时候调用
onActivityCreated()//确保与Fragment相关联的活动一定已经创建完毕的时候调用（当Activity的onCreate方法返回时调用）
onStart()
onResume()
onPause()
onStop()
onDestroyView()//当与Fragment关联的视图被移除的时候调用
onDestroy()
onDatch()//当Fragment与Activity解除关联的时候调用
```

注意：除了onCreateView，其他的所有方法如果重写了，必须调用父类对该方法的实现。

### 3. Fragment使用方式

**静态使用Fragment**

1. 继承Fragment（V4），重写onCreateView方法，确定Fragment需要加载的布局。
2. 在Activity中声明此类，与普通的View对象一样。

```java
public class MyFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*
        * 参数1：布局文件的id
        * 参数2：容器
        * 参数3：是否将这个生成的View添加到这个容器中去
        * 作用是将布局文件封装在一个View对象中，并填充到此Fragment中
        * */
        View v = inflater.inflate(R.layout.item_fragment, container, false);
        return v;
    }
}
```

```xml
<fragment
	android:id="@+id/myfragment"
	android:name="com.example.fragment.MyFragment"
	android:layout_width="match_parent"
	android:layout_height="match_parent" />
```

**动态使用Fragment**

```java
FragmentManager manager = getSupportFragmentManager();
FragmentTransaction transaction = manager.beginTransaction();
transaction.add(R.id.myframelayout, fragment)
    .commit();
```

注意：调用`add`/`replace`/`hide`/`show`以后都要`commit()`其效果才会在屏幕上显示出来。

### 4. Fragment的回退栈

Fragment的回退栈是用来保存每一次Fragment事务发生的变化，如果将Fragment添加到回退栈，点击back后就会看到上一次保存的Fragment。

```java
FragmentTwo fTwo = new FragmentTwo();  
FragmentManager fm = getFragmentManager();  
FragmentTransaction tx = fm.beginTransaction();
//replace方法相当于remove和add的合体
tx.replace(R.id.id_content, fTwo, "TWO");  
tx.addToBackStack(null);  
tx.commit();  
```

虽然将当前事务添加到了回退栈，所以Fragment实例不会销毁，但是**视图层次会被销毁，即会调用onDestroyView和onCreateView**，

```java
FragmentThree fThree = new FragmentThree();
FragmentManager fm = getFragmentManager();
FragmentTransaction tx = fm.beginTransaction();
tx.hide(this);
tx.add(R.id.id_content , fThree, "THREE");
//tx.replace(R.id.id_content, fThree, "THREE");
tx.addToBackStack(null);
tx.commit();
```

如果不希望视图重绘，就应该使用hide方法。

### 5.Fragment与Activity之间的通信

- 如果Activity中包含Fragment的引用，可以通过引用直接访问public方法。
- 如果Activity未包含Fragment的引用，可以通过`getFragmentManager.findFragmentById`或`getFragmentManager.findFragmentByTag`获得对对应的Fragment实例，进行操作。
- Fragment中可以通过getActivity()得到当前绑定Activity的实例，然后进行操作。
