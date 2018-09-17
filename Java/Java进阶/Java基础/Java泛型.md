# Java泛型

### 一、泛型简介

#### 1.引入泛型的目的

语法糖（Syntactic Sugar）：也称糖衣语法，指在计算机语言中添加的某种语法，这种语法对语言的功能并没有影响，但是更方便程序员使用。Java中最常用的语法糖主要有泛型、变长参数、条件编译、自动拆装箱、内部类等。虚拟机并不支持这些语法，它们在编译阶段就被还原回了简单的基础语法结构，这个过程叫做解语法糖。

泛型的目的：Java泛型就是一种语法糖，通过泛型使得在编译阶段完成一些类型转换的工作，避免在运行时强制转换ClassCastException，即类型转换异常。

#### 2.泛型初探

JDK 1.5时才增加了泛型，并在很大程度上都是方便集合的使用，使其能够记住其元素的数据类型。

#### 3.泛型的好处

1. 类型安全。类型错误现在在编译期间就被捕获到了，而不是运行时当做ClassCastException展现出来，将类型检查从运行时挪到编译时有助于开发者很容易找到错误，并提高程序的可靠性。
2. 消除了代码中许多的强制类型转换，增强了代码的可读性。
3. 为较大的优化带来了可能。

### 二、泛型的使用

#### 1.泛型类和泛型接口

泛型类派生子类：

当创建了带泛型声明的接口、父类之后，可以为该接口创建实现类或派生子类，需要注意：使用这些接口，父类派生子类时不能再包含类型形参，需要传入具体的类型。

```java
//错误方式
public class A extends Container<K, V>{}
//正确方式
public class A extends Container<Integer, String>{}
//也可以不指定具体的类型，将K,V形参当成Object类型处理
public class A extends Container{}
```

#### 2.泛型的方法

```java
public static <T> void out(T t){
    System.out.println(t);
}
```

#### 3.泛型构造器

```java
public class Person{
    public <T> Person(T t){
        System.out.println(t);
    }
}

public static void main(String[] args){
    //隐式
    new Person(22);
    //显式
    new<String> Person("hello"); 
}
```

### 三、类型通配符

顾名思义就是匹配任意类型的类型实参。

```java
public void test(List<?> c){
    for(int i = 0;i<c.size();i++){
        System.out.println(c.get(i));
    }
}
```

可以传入任何类型的List来调用test方法。

```java
List<?> c = new ArrayList<String>();
//报错
c.add(new Object());
```

无法确定c集合中类型，不能向其添加对象。

#### 带限通配符

##### 1.上限通配符

使用extends关键字指定此类型必须继承某个类或接口，也可以是这个类或接口本身。

##### 2.下限通配符

使用super关键字指定此类型必须是某个类的父类或接口的父接口，也可以是这个类或接口本身。

### 四、类型擦除

```java
Class c1 = new ArrayLsit<Integer>().getClass();
Class c2 = new ArrayLsit<String>().getClass();
System.out.println(c1==c2);
```

输出true。