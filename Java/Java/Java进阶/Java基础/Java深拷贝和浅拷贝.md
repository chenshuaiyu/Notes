# Java深拷贝和浅拷贝

### 一、引言

对象拷贝（Object Copy）就是将一个对象的属性拷贝到另一个有着相同类类型的对象中去。

Java中三种类型的对象拷贝：

1. 浅拷贝（Shallow Copy）
2. 深拷贝（Deep Copy）
3. 延迟拷贝（Lazy Copy）

### 二、浅拷贝

浅拷贝是按位拷贝对象，它会创建一个新对象，这个对象有着原始对象属性值的的一份精确拷贝，如果属性是基本类型，拷贝的就是基本数据的值；如果属性是内存地址（引用类型），拷贝的就是内存地址，因此如果其中一个对象改变了这个地址，就会影响到另一个对象。

String为浅拷贝。

```java
public class Subject {
    private String name;

    public Subject(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

```java
public class Student implements Cloneable {
    private Subject subject;
    private String name;
    private int age;

    public Student(String subject, String name, int age) {
        this.subject = new Subject(subject);
        this.name = name;
        this.age = age;
    }

    public Subject getSubject() {
        return subject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
```

```java
public class Main {
    public static void main(String[] args) {
        try {
            Student s1 = new Student("English", "Chen", 20);
            Student s2 = (Student) s1.clone();

            System.out.println(s1.getSubject() == s2.getSubject());
            System.out.println(s1.getName() == s2.getName());
            System.out.println(s1.getAge() == s2.getAge());

            s1.getSubject().setName("Chinese");
            s1.setName("Chen1");
            s1.setAge(18);

            System.out.println(s1.getSubject() == s2.getSubject());
            System.out.println(s1.getName() == s2.getName());
            System.out.println(s1.getAge() == s2.getAge());

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }
}
```

```
true
true
true
true
false
false
```

### 三、深拷贝

### 通过序列化实现深拷贝

通过序列化进行深拷贝时，必须通过对象中的所有类都是可序列化的。

```java
public class ColoredCircle implements Serializable {
    private int x;
    private int y;

    public ColoredCircle(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "x=" + x + ", y=" + y;
    }
}
```

```java
public class Main {
    public static void main(String[] args) {
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            ColoredCircle c1 = new ColoredCircle(100, 100);
            System.out.println("Original = " + c1);
            ColoredCircle c2 = null;

            //通过序列化实现深拷贝
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            //序列化以及传递这个对象
            oos.writeObject(c1);
            oos.flush();
            ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());
            ois = new ObjectInputStream(bin);
            //返回新的对象
            c2 = (ColoredCircle) ois.readObject();

            System.out.println("Copied = " + c2);
            c1.setX(200);
            c1.setY(200);
            System.out.println("Original = " + c1);
            System.out.println("Copied = " + c2);
        } catch (Exception e) {
            System.out.println("Exception in main" + e);
        } finally {
            try {
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
```

```
Original = x=100, y=100
Copied = x=100, y=100
Original = x=200, y=200
Copied = x=100, y=100
```

- 确保对象图中的所有类都是可序列化的
- 创建输入输出流
- 使用这个输入输出流来创建对象输入和对象输出流
- 将想要拷贝的对象传递给对象输出流
- 从对象输出流中读取新的对象并且转换回你所发送的对象的类

### 四、延迟拷贝

延迟拷贝是浅拷贝和深拷贝的一个组合，实际上很少会使用，最开始拷贝一个对象时，会使用速度较快的浅拷贝，还会使用一个计数器来记录有多少个对象共享这个数据。当程序想要修改原始的对象时，它会决定数据是否被共享（通过检查计数器）并根据需要进行深拷贝。

### 五、如何选择

如果对象的属性全是基本类型的，那么可以使用浅拷贝，但是如果对象有引用属性，那么就要基于具体的需求来选择浅拷贝还是深拷贝。