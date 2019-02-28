# Java基础

### 数据类型

- 基本数据类型：byte，short，int，long，float，double，char，boolean
- 引用数据类型：class，interface，数组

基本数据类型存储在栈上的一块内存。

引用数据类型的引用存储在在栈上，并指向堆中对象的地址。

| 基本类型 | 字节 | 范围                       | 默认值 | 包装类    |
| -------- | ---- | -------------------------- | ------ | --------- |
| byte     | 1    | [-2^7,+2^7-1]              | 0      | Byte      |
| short    | 2    | [-2^15,+2^15-1]            | 0      | Short     |
| int      | 4    | [-2^31,+2^31-1]            | 0      | Integer   |
| long     | 8    | [-2^63,+2^63-1]            | 0L     | Long      |
| float    | 4    | IEEE754                    | 0.0f   | Float     |
| double   | 8    | IEEE754                    | 0.0d   | Double    |
| char     | 2    | [Unicode 0,Unicode 2^16-1] | ''     | Character |
| boolean  | -    | -                          | false  | Boolean   |

### 基本数据类型与包装类

```java
Integer integer1 = new Integer(1); //手动装箱
Integer integer2 = 1; //自动装箱
int i1 = integer1.intValue(); //手动拆箱
int i2 = integer2; //自动拆箱
```

```java
Integer i1 = 10;
Integer i2 = 10;
System.out.println(i1 == i2); //true

Integer i3 = 132;
Integer i4 = 132;
System.out.println(i3 == i4); //false
```

JVM自动维护八种基本类型的常量池，int常量池中初始化-128~127的范围，所以当为`Integer i = 127;`时，在自动装箱过程中是取自常量池中的数值，而当Integer i=128时，128不在常量池范围内，所以在自动装箱过程中需new，所以地址不一样。

```java
Integer i1 = new Integer(10);
Integer i2 = new Integer(10);
System.out.println(i1 == i2); //false

Integer i3 = new Integer(132);
Integer i4 = new Integer(132);
System.out.println(i3 == i4); //false
//i1,i2,i3,i4是对象,故为false
```

```java
Integer i1 = new Integer("123");
Integer i2 = new Integer(123);
System.out.println(i1 == i2); //false

Integer i = new Integer(1);
i = i + 2;
System.out.println(i); //3
//i先自动拆箱，再自动装箱
```

自动类型转换：

byte,short,char—> int —> long—> float —> double

### 标识符

26个英文字母大小写，0~9（数字不能开头），_（下划线），$

### 支持switch的类型

byte，short，int，char，String（Java 7 新特性）

### 位运算符

&，|，^，~，<<，>>（带符号右移，补高位数字），>>>（无符号右移，补0）

# 面向对象

### 权限修饰符

|  作用域   | 当前类 | 同一包内 | 子孙类 | 其他包内 |
| :-------: | :----: | :------: | :----: | :------: |
|  public   |   √    |    √     |   √    |    √     |
| protected |   √    |    √     |   √    |    ×     |
|  default  |   √    |    √     |   ×    |    ×     |
|  private  |   √    |    ×     |   ×    |    ×     |

### 局部变量

形参，方法中变量，代码块中的变量。

### 成员变量与局部变量的区别

- 成员变量（位于堆内存）：只定义未赋值时，系统会自动赋值。
- 局部变量（位于栈内存）：只定义未赋值时，内存未开辟，会出错。

### 构造函数作用

（默认构造函数的权限是随着类权限的改变而改变的）

1. 当一个类无构造函数时，系统会自动添加一个空参数的构造函数。
2. 当定义构造函数后，默认的就没有了。

### 构造代码块作用

1. 优先于构造函数而执行。
2. 对所有对象进行初始化。
3. 随着对象的加载而加载。

### this()作用

1. 用于构造函数间的互相调用。
2. 只能定义在构造函数第一行，初始化要先执行。

### static

用于修饰成员，优先于对象存在，直接通过类名调用（NULL对象可以访问它所属类的静态成员，底层是通过类名调用）。

1. 静态方法只能访问静态成员，
2. 静态方法中不可以使用this，super（静态优先于对象存在）。
3. 主函数是静态的。
4. 非静态方法可以访问静态和非静态成员。

### static代码块

随着类的加载而加载，只执行一次，用于给类进行初始化的。

### final特点

可修饰类（不能被继承），方法（不能被复写，可以重载），变量（只能赋值一次）。

注意：内部类定义在类的局部位置上，只能访问该局部被final修饰的局部变量。

### 静态变量和类变量的初始化位置

| 变量     | 位置                                 |
| -------- | ------------------------------------ |
| 静态变量 | 定义时赋值，静态代码块中初始化       |
| 类变量   | 定义时赋值，代码块，构造函数中初始化 |

### abstract抽象类

1. 抽象方法一定在抽象类中（抽象类中可以有非抽象方法）。
2. 子类必须复写抽象类的所有抽象方法后，才能建立子类对象。

### interfacve接口（特殊的抽象类，全是abstract）

java8 允许接口中定义默认方法。

- 常量默认权限：`public static final`
- 方法默认权限：`public abstract`