# Fragment详解

### 1. Fragment

可以将Fragment理解为显示在Activity中的Activity，它可以显示在Activity中，也拥有自己的生命周期，可以接受处理用户的事件，并且可以在Acitvity中动态的添加替换移除不同的Fragement。

### 2. 生命周期

```java
onAttach()//当Fragment与Activity发生关联的时候调用
onCreate()
onCreateView()//创建该Fragement的视图
onActivityCreated()//当Activity的onCreate方法返回时调用
onStart()
onResume()
onPause()
onStop()
onDestroyView()//当该Fragment的视图被移除时调用
onDestroy()
onDatch()//当Fragment与Activity取消关联的时候调用
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
transaction.add(R.id.myframelayout, fragment1).commit();
```

注意：调用add/replace/hide/show以后都要commit其效果才会在屏幕上显示出来。

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
