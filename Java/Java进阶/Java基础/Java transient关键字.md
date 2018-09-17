# Java transient关键字

### 一、Java transient的作用及使用方法

```java
public static void main(String[] args) {
    User user = new User();
    user.setUsername("Alexia");
    user.setPasswd("123456");

    System.out.println("read before Serializable:");
    System.out.println("username: " + user.getUsername());
    System.out.println("paddword: " + user.getPasswd());

    try {
        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("F:/user.txt"));
        os.writeObject(user);
        os.flush();
        os.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
    try {
        ObjectInputStream is = new ObjectInputStream(new FileInputStream("F:/user.txt"));
        user = (User) is.readObject();
        is.close();

        System.out.println("\nread after Serializable:");
        System.out.println("username: " + user.getUsername());
        System.out.println("paddword: " + user.getPasswd());
    } catch (Exception e) {
        e.printStackTrace();
    }

}

static class User implements Serializable {
    private String username;
    private transient String passwd;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}
```

输出：

read before Serializable:
username: Alexia
paddword: 123456

read after Serializable:
username: Alexia
paddword: null

### 二、transient使用小结

1. 一旦变量被transient修饰，变量将不再是对象持久化的一部分，该变量内容在序列化后无法获得访问。
2. transient关键字只能修饰变量，而不能修饰方法和类。注意，本地变量是不能被transient关键字修饰的。变量如果是用户自定义类变量，则该类需要实现Serializable接口。
3. 被transient关键字修饰的变量是不能再被序列化，一个静态变量不管是否被transient修饰，均不能被序列化。

对于第三点，将username属性加上static属性，程序运行结果不变。原因是反序列化类中static型变量username的值为当前JVM中对应static变量的值，这个值是JVM中的不是反序列化得出的。

```
public static void main(String[] args) {
    User user = new User();
    user.setUsername("Alexia");
    user.setPasswd("123456");

    System.out.println("read before Serializable:");
    System.out.println("username: " + user.getUsername());
    System.out.println("paddword: " + user.getPasswd());

    try {
        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("F:/user.txt"));
        os.writeObject(user);
        os.flush();
        os.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
    try {
        user.setUsername("Chen");

        ObjectInputStream is = new ObjectInputStream(new FileInputStream("F:/user.txt"));
        user = (User) is.readObject();
        is.close();

        System.out.println("\nread after Serializable:");
        System.out.println("username: " + user.getUsername());
        System.out.println("paddword: " + user.getPasswd());
    } catch (Exception e) {
        e.printStackTrace();
    }

}

static class User implements Serializable {

    public static final long serialVersionUID = 8294180014912103005L;

    private static String username;
    private transient String passwd;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}
```

输出：

read before Serializable:
username: Alexia
paddword: 123456

read after Serializable:
username: Chen
paddword: null

### 三、transient使用细节——被transient关键字修饰的变量真的变量真的不能被序列化吗？

```java
public class ExternalizableTest implements Externalizable {

    private transient String content = "序列化";

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(content);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        content = (String) in.readObject();
    }

    public static void main(String[] args) throws Exception {
        ExternalizableTest et = new ExternalizableTest();
        ObjectOutput out = new ObjectOutputStream(new FileOutputStream(new File("test")));
        out.writeObject(et);
        ObjectInput in = new ObjectInputStream(new FileInputStream(new File("test")));
        et = (ExternalizableTest) in.readObject();
        System.out.println(et.content);

        out.close();
        in.close();
    }
}
```

输出：

序列化