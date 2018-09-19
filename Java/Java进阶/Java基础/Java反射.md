# Java反射（一）

### 一、概述

Java反射机制是在运行状态中，对于任意一个类，都能知道这个类中的所有属性和方法；对于任意一个对象，都能够调用它的任意一个方法和属性；这种动态获取的信息以及动态调用对象的方法的功能叫做Java语言的反射机制。

Java反射机制的功能：

1. 在运行时判断任意一个对象所属的类。
2. 在运行时构造任意一个类的对象。
3. 在运行判断任意一个类所具有的的成员变量和方法。
4. 在运行时调用任意一个对象的方法。
5. 生成动态代理。

Java反射机制的应用场景：

1. 逆向代码，例如反编译。
2. 与注解相结合的框架，例如Retrofit.
3. 单纯的反射机制应用框架，例如EventBus。
4. 动态生成类框架，例如Gson。

### 二、通过Java反射查看类信息

获得Class对象的三种方式：

1. 对象 . getClass();
2. Class.forName("java.util.Date");
3. 类名 . class

#### 1.获取class对象的成员变量

```java
getDeclaredFields();//获取class对象的所有属性
getFields();//获取class对象的public属性
getDeclaredField("");//获取class指定属性
getFields("");//获取class指定的public属性
```

#### 2.获取class对象的方法

```java
getDeclaredMethods();//获取class对象的所有声明方法
getMethods();//获取class对象的所有public属性，包括父类的方法
getDeclaredMethod();//返回class对象对应类的、带指定形参列表的方法
getMethod();//返回class对象对应类的、带指定形参列表的public方法
```

#### 3.获取calss对象的构造函数

```java
getDeclaredConstructors();//获取class对象的所有声明构造函数
getConstructors();//获取class对象的所有public构造函数
getDeclaredConstructor();//获取指定声明构造函数
getConstructor();//获取指定声明的public构造函数
```

#### 4.其他方法

```java
getAnnotations();//获取class对象的所有注解
getAnnotation();//获取class对象指定注解
getGenericSuperclass();//获取class对象的直接超类Type
getGenericInnerclass();//获取class对象的所有接口的Type集合
```

```java
isPrimitive();//判断是否是基础类型
isArray();//判断是否是集合类
isAnnotation();//判断是否是注解类
isInterface();//判断是否是接口类
isEnum();//判断是否是枚举类
isAnonymousClass();//判断是否是匿名内部类
isAnnotationPresent();//判断是否被某个注解类修饰
getName();//获取class名字，包含包名路径
getPackage();//获取class的包信息
getSimpleName();//获取class类名
getModifiers();//获取class访问权限
getDeclaredClasses();//内部类
getDeclaringClass();//外部类
```

### 三、通过Java反射生成并操作对象

生成类的实例对象：

1. 使用Class对象的newInstance()方法来创建该Class对象对应类的实例。这种方式要求该Class对象的对应类有默认构造器，而执行newInstance()方法时实际上是利用默认构造器来创建该类的实例。
2. 先使用Class对象获取指定的Constructor对象，在调用Constructor对象的newInstance()方法来创建该Class对象对应类的实例。通过这种方法可以选择使用指定的构造器来创建实例。

# Java反射（二）

### 一、代理模式

定义：给某个对象提供一个代理对象，并由代理对象控制对于原对象的访问，即客户不直接操纵原对象，而是通过代理间接地操纵原对象。

#### 1.代理模式的理解

代理模式使用代理对象完成用户请求，屏蔽用户对真实对象的访问。现实世界的代理人被授权执行当事人的一些事宜，无需当事人出面，从第三方的角度看，似乎方式人并不存在，因为他只和代理人通信。而事实上代理人要有当事人的授权，并且在核心问题上还要请示当事人。

在软件设计中，使用代理模式的意图也很多，比如因为安全原因需要屏蔽客户端直接访问真实对象，或者在远程调用中需要使用代理类处理远程方法调用的技术细节，也可能是为了提升系统性能，对真实对象进行封装，从而达到延迟加载的目的。

#### 2.代理模式的参与者

代理模式的角色分四种：

- 主题接口（Subject）：委托对象和代理对象都共同实现的接口，即代理类的所实现。
- 目标对象（RealSubject）：原对象，也就是被代理的对象。
- 代理对象（Proxy）：用来封装真是主题类的代理类。
- 客户端：使用代理类和主题完成一些工作。

#### 3.代理模式的分类

- 静态代理：代理类是在编译时就实现好的。也就是说Java编译完成后代理类是一个实际的class文件。
- 动态代理：代理类是在运行时生成的。也就是说Java编译完之后并没有实际的class文件，而是在运行时动态生成的类字节码，并加载到JVM中。

#### 4.代理模式的实现思路

1. 代理对象和目标对象均实现用一个行为接口。
2. 代理类和目标类分别具体实现接口逻辑。
3. 在代理类的构造函数中实例化一个目标对象。
4. 在代理类中调用目标对象的行为接口。
5. 客户想要调用目标对象的行为接口，只能通过代理类来操作。

#### 5.静态代理的简单实现

```java
public static void main(String[] args) {
    RealSubject subject = new RealSubject();
    Proxy p = new Proxy(subject);
    p.request();
}

interface Subject {
    void request();
}

static class RealSubject implements Subject {

    @Override
    public void request() {
        System.out.println("request");
    }
}

static class Proxy implements Subject {
    private Subject subject;

    public Proxy(Subject subject) {
        this.subject = subject;
    }

    @Override
    public void request() {
        System.out.println("PreProcess");
        subject.request();
        System.out.println("PostProcess");
    }
}
```

### 二、Java反射机制与动态代理

#### 1.动态代理介绍

动态代理是指在运行时动态生成代理类。即，代理类的字节码将在运行时并载入当前代理的ClassLoader。与静态处理类相比，动态类有诸多好处。

#### 2.动态代理涉及的主要类

主要涉及两个类，这两个类都是java.lang.reflect包下的类，内部主要通过反射来实现的。

- java.lang.reflect.Proxy：这是生成代理类的主类，通过Proxy类生成的代理类都继承了Proxy类。
- java.lang.reflect.InvocationHandle：这里称它为调用处理器，它是一个接口。当动态处理类中的方法时，将会直接转接到执行自定义的InvocationHandle中的invoke()方法。即我们动态生成的代理类需要完成的具体内容需要自己定义一个类，而这个类必须实现InvocationHandle接口，通过重写invoke()方法来执行具体内容。

Proxy提供了两个方法来创建动态代理类和动态代理实例。

```java
public static Class<?> getProxyClass(ClassLoader loader,
                                     Class<?>... interfaces)
```

返回代理类的java.lang.class对象。第一个参数是类加载器对象（即哪个类加载器这个代理类到JVM的方法区），第二个参数是接口（表明你这个代理类需要实现哪些接口），第三个参数是调用处理器类实例（指定代理类中具体要干什么），该代理类将实现interfaces所指定的所有接口，执行处理对象的每个方法时都会被替代执行InvocationHandle对象的invoke方法。

```java
public static Object newProxyInstance(ClassLoader loader, Class<?>[] interfaces, InvocationHandler h)
```

返回代理类实例，参数与上述方法一致。



# Java反射（三）

### 一、泛型和Class类

JDK1.5后，Java中引入了泛型机制，Class类也增加了泛型功能，从而允许使用泛型来限制Class类，例如：String.class的类型上是Class\<String>。如果Class对应的类暂时未知，则使用CLass<?>。通过反射中使用泛型，可以避免反射生成的对象需要强制类型转换。

泛型的好处：避免类型转换，防止出现ClassCastException。

### 二、使用反射来获取泛型信息

```java
//获取 Field 对象 f 的类型
Class<?> a = f.getType();
```

这种方式只对普通类型的Field有效。如果该Field的类型是有泛型限制的类型， 如`Map<String, Integer>`类型，则不能准确地得到该Field的泛型参数。

```java
Type type = f.getGenericType();
```

然后将Type对象强制类型转换为ParameterizedType对象，ParameterizedType代表被参数化的类型，也就是增加了泛型限制的类型。ParameterizedType类提供了如下两个方法：

- getRawType()：返回没有泛型信息的原始类型。
- getActualTypeArguments()：返回泛型参数的类型。

```java
public class GenericTest {

    private Map<String, Integer> score;

    public static void main(String[] args) throws Exception {

        Class<GenericTest> clazz = GenericTest.class;
        Field f = clazz.getDeclaredField("score");
        //直接使用getType()取出Field类型只对普通类型的Field有效
        Class<?> a = f.getType();
        System.out.println("score的类型是：" + a);
        Type gType = f.getGenericType();
        if (gType instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) gType;
            Type rType = pType.getRawType();
            System.out.println("原始类型是：" + rType);

            Type[] tArgs = pType.getActualTypeArguments();
            System.out.println("泛型参数是：");
            for (int i = 0; i < tArgs.length; i++) {
                System.out.println("第 " + i + " 个泛型类型参数 " + tArgs[i]);
            }
        } else {
            System.out.println("获取泛型类型出错！");
        }
    }
}
```

