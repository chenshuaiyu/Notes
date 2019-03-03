# Java注解

### 一、元数据

#### 1.元数据概念

元数据：关于数据的数据。在编程语言上下文中，元数据是添加到程序元素如方法、字段、类和包上的额外信息。对数据进行说明描述的数据。

#### 2.元数据的作用

1. 编写文档：通过代码里标识的元数据生成文档。
2. 代码分析：通过代码里标识的元数据对代码进行分析。
3. 编译检查：通过代码里标识的让编译器能实现基本的编译检查。

#### 3.Java平台元数据

### 二、注解（Annotation）

#### 1.注解（Annotation）的概念

注解是在JDK1.5之后添加的新特性。

#### 2.内建注解

Java提供了多种内建的注解，常用的注解：@Override，@Deprecated，@SuppressWarning以及@FunctionalInterface这四个注解。

内建注解只要实现了元数据的第二个作用：编译检查。

##### @Override

复写超类的当前方法。

##### @Deprecated

用于告知编译器，某一程序元素不建议使用（即过时了）。

##### @SuppressWarning

用于告知编译器忽略特定的警告信息，例在泛型中使用原生数据类型，编译器会发生警告，当使用注解后，则不会发生警告。

当注解有方法value()，可支持多个字符串参数，用户指定忽略哪种警告，

```java
@SuppressWarning(value={"uncheck","deprecation"})
```

| 参数        | 含义                                            |
| ----------- | ----------------------------------------------- |
| deprecation | 使用了过时的类或方法时的警告                    |
| unchecked   | 执行了未检查的转换时的警告                      |
| fallthrough | 当Switch程序块进入到下一个case而没有break的警告 |
| path        | 在类路径、源文件路径等有不存在路径时的警告      |
| serial      | 当可序列化的类缺少serialVersionUID定义时的警告  |
| finally     | 任意finally子句不能正常完成时的警告             |
| all         | 以上所有情况的警告                              |

##### @FunctionalInterface

用户告知编译器，检查这个接口，保证该接口是函数式接口，即只能包含一个抽象方法，否则就会编译出错。

#### 3.元Annotation

##### @Documented

用户指定该元Annotation修饰的Annotation将会被javadoc工具提取成文档。

##### @Inherited

被它修饰的Annotation将具有继承性——如果类使用了@Xxx注解（定义该Annotation时使用了@Inherited修饰）修饰，则其子类将自动被@Xxx修饰。

##### @Retention

表示该注解类型的注解保留的时长。当注解类型声明中没有@Retention元注解，则默认保留策略为RetentionPolicy.CLASS。关于保留策略（RetentionPolicy）是枚举类型，共定义3种保留策略，

|         |                                                              |
| ------- | ------------------------------------------------------------ |
| SOURCE  | 仅存在于Java源文件，经过编译后便丢弃相应的注解               |
| CLASS   | 存在Java源文件，以及经编译器后生成的Class字节码文件，但在运行时VM不再保留注释 |
| RUNTIME | 存在源文件，编译生成的Class字节码文件，以及保留在运行时VM中，可通过反射性地读取注解 |

##### @Target

表示该注解所适用的程序元素类型。当注解类型声明中没有@Target元注解，则默认为可适用所有的程序元素。如果存在指定的@Target元注解，则编译器强制实施相应的使用限制。关于程序元素（ElementType）是枚举类型，共定义8种程序元素，

| ElementType     | 含义                               |
| --------------- | ---------------------------------- |
| ANNOTATION_TYPE | 注解类型声明                       |
| CONSTRUCTOR     | 构造方法声明                       |
| FIELD           | 字段声明（包括枚举常量）           |
| LOCAL_VARIABLE  | 局部变量声明                       |
| METHOD          | 方法声明                           |
| PACKAGE         | 包声明                             |
| PARAMETER       | 参数声明                           |
| TYPE            | 类、接口（包括注解类型）或枚举声明 |

### 三、自定义注解（Annotation）

创建自定义注解，与创建接口有几分相似，但注解需要以@开头。

```java
@Documented
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface MyAnnotation{
    String name();
    String website() default "hello";
    int revision() default 1;
}
```

自定义注解中定义成员变量的规则：

其定义是以无形参的方法形式来声明的。即：

注解方法不带参数，比如name()，website()

注解方法返回值类型：基本类型、String、Enums、Annotation以及前面这些类型的数组类型

注解方法可以默认值，比如default "hello"，default website="hello"

当然注解中也可以不存在成员变量，在使用解析注解进行操作时，仅以是否包含该注解来进行操作。当注解中有成员变量时，若没有默认值，需要在使用注解时，指定成员变量的值。

```java
public class AnnotationDemo{
    @MyAnnotataion(name="lvr", website="hello", revision=1)
    public static void main(String[] args){
        System.out.println("I am main method");
    }
    
    @SuppressWarning({"unchecked", "deprecation"})
    @MyAnnotataion(name="lvr", website="hello", revision=2)
    public void demo{
        System.out.println("I am demo method");
    }
}
```

由于该注解的保留策略是RetentionPolicy.RUNTIME，故可在运行期通过反射机制来使用，否则无法通过反射机制来获取。这时候注解实现的就是元数据的第二个作用：代码分析。

### 四、注解分析

通过反射技术来解析自定义注解。关于反射类位于包java.lang.reflect，其中有一个接口AnnotationElement，该接口主要有如下几个实现类：Class，Constructor，Field，Method，Package。除此之外，该接口定义了注释相关的几个核心方法，

因此，当获取了某个类的Class对象，然后获取其Field，Method等对象，通过上述4个方法提取其中的注解，然后获得注解的详细信息。

```java
public class AnnotationParser {
    public static void main(String[] args) throws SecurityException, ClassNotFoundException {
        String clazz = "com.lvr.annotation.AnnotationDemo";
        Method[]  demoMethod = AnnotationParser.class
                .getClassLoader().loadClass(clazz).getMethods();

        for (Method method : demoMethod) {
            if (method.isAnnotationPresent(MyAnnotataion.class)) {
                 MyAnnotataion annotationInfo = method.getAnnotation(MyAnnotataion.class);
                 System.out.println("method: "+ method);
                 System.out.println("name= "+ annotationInfo.name() +
                         " , website= "+ annotationInfo.website()
                        + " , revision= "+annotationInfo.revision());
            }
        }
    }
}
```