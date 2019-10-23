# Java IO

### 一、字符和字节

Java中有输入、输出流，每种输入、输出流中分为字节流和字符流两大类。

#### 字节与字符

- Java采用unicode编码，2个字节来表示一个字符。
- C采用ASCII，1个字节通常占一个字节。

Java中使用`String(byte[] bytes, String encoding)`构造字符串时，encoding所指的是bytes中的数据是按照那种方式编码的，而不是最后产生的String是什么编码方式。

`String.getBytes(String charsetName)`使用指定的编码方式将此String编码为 byte 序列，并将结果存储到一个新的 byte 数组中。

### 二、File类

File是java.io包下代表与平台无关的文件和目录。

#### 1.构造函数

```java
//构造函数File(String pathname)
File f1 = new File("c:\\abc\\1.txt");

//File(String parent,String child)
File f2 = new File("c:\\abc","2.txt");

//File(File parent,String child)
File f3 = new File("c:" + File.separator + "abc");//separator 跨平台分隔符
File f4 = new File(f3,"3.txt");
```

路径分隔符：

- windows： "/" "\\" 都可以
- linux/unix： "/"

如果windows选择用"\\"做分割符的话,那么请记得替换成"\\\\",因为Java中"\\\\"代表转义字符，所以使用"/"，也可以直接使用代码`File.separator`，表示跨平台分隔符，更加通用一些。

路径：

- 相对路径：`./`表示当前路径，`../`表示上一级路径。
- 绝对路径：完整的路径名。

#### 2.创建和删除方法

```java
//如果文件存在返回false，否则返回true并且创建文件 
boolean createNewFile();

//创建一个File对象所对应的目录，成功返回true，否则false。且File对象必须为路径而不是文件。只会创建最后一级目录，如果上级目录不存在就抛异常。
boolean mkdir();

//创建一个File对象所对应的目录，成功返回true，否则false。且File对象必须为路径而不是文件。创建多级目录，创建路径中所有不存在的目录
boolean mkdirs();

//如果文件存在返回true并且删除文件，否则返回false
boolean delete();

//在虚拟机终止时，删除File对象所表示的文件或目录。
void deleteOnExit();
```

#### 3.判断方法

```java
boolean canExecute();//判断文件是否可执行
boolean canRead();//判断文件是否可读
boolean canWrite();//判断文件是否可写
boolean exists();//判断文件是否存在
boolean isDirectory();//判断是否是目录
boolean isFile();//判断是否是文件
boolean isHidden();//判断是否是隐藏文件或隐藏目录
boolean isAbsolute();//判断是否是绝对路径 文件不存在也能判断
```

#### 4.获取方法

```java
String getName();//返回文件或者是目录的名称
String getPath();//返回路径
String getAbsolutePath();//返回绝对路径
String getParent();//返回父目录，如果没有父目录则返回null
long lastModified();//返回最后一次修改的时间
long length();//返回文件的长度
File[] listRoots();// 列出所有的根目录（Window中就是所有系统的盘符）
String[] list() ;//返回一个字符串数组，给定路径下的文件或目录名称字符串
String[] list(FilenameFilter filter);//返回满足过滤器要求的一个字符串数组
File[]  listFiles();//返回一个文件对象数组，给定路径下文件或目录
File[] listFiles(FilenameFilter filter);//返回满足过滤器要求的一个文件对象数组
```

`FilenameFilter`，该接口是一个文件过滤器，包含一个`accept(File dir,String name)`方法，过滤条件为文件名后缀为jpg：

```java
// 文件过滤
File[] files = file.listFiles(new FilenameFilter() {
    @Override
    public boolean accept(File file, String filename) {
        return filename.endsWith(".jpg");
    }
});
```

### 三、IO流

- 输入流：只能读数据，不能写数据。
- 输出流：只能写数据，不能读数据。

- 字节流：能处理所有类型的数据。
- 字符流：处理纯文本数据。

- 节点流：想一个特定IO设备读写数据的流，也称为低级流。
- 处理流：对一个已存在的流进行连接或封装，封装后的流来实现数据读写功能，也称为高级流。

```java
//节点流，直接传入的参数是IO设备
FileInputStream fis = new FileInputStream("test.txt");

//处理流，直接传入的参数是流对象
BufferedInputStream bis = new BufferedInputStream(fis);
```

注意：使用处理流并不会直接连接到实际的数据流，没有和实际的输入输出节点连接。

优点：使用相同的处理流可以访问不同的数据源，处理不同的需求。

Java使用处理流来包装节点流属于**装饰器模式**，使用处理流包装不同的节点流，既可以消除不同节点流的实现差异，也可以提供更方便的方法来完成输入/输出功能。

### 四、IO流四大基类

![IO流四大基类](https://github.com/chenshuaiyu/Notes/blob/master/Java/Java/Java进阶/assets/IO流四大基类.png)

#### 1.InputStream

```java
//读取一个字节并以整数的形式返回(0~255),如果返回-1已到输入流的末尾。
int read();

//读取一系列字节并存储到一个数组buffer，返回实际读取的字节数，如果读取前已到输入流的末尾返回-1。 
int read(byte[] buffer);

//读取length个字节并存储到一个字节数组buffer，从off位置开始存,最多len， 返回实际读取的字节数，如果读取前以到输入流的末尾返回-1。 
int read(byte[] buffer, int off, int len);
```

#### 2.Reader

```java
//读取一个字符并以整数的形式返回(0~255),如果返回-1已到输入流的末尾。 
int read();

//读取一系列字符并存储到一个数组buffer，返回实际读取的字符数，如果读取前已到输入流的末尾返回-1。 
int read(char[] cbuf); 

//读取length个字符,并存储到一个数组buffer，从off位置开始存,最多读取len，返回实际读取的字符数，如果读取前以到输入流的末尾返回-1。 
int read(char[] cbuf, int off, int len);
```

两个基类的功能基本一样的，只不过**读取的数据单元不同**。

注意：

**在执行完流操作后，要调用**`close()`**方法来关闭输入流，因为程序里打开的IO资源不属于内存资源，垃圾回收机制无法回收该资源，所以应该显式关闭文件IO资源。**

两个基类还可以移动流中的指针位置：

```java
//在此输入流中标记当前的位置
//readlimit - 在标记位置失效前可以读取字节的最大限制。
void mark(int readlimit);
// 测试此输入流是否支持 mark 方法
boolean markSupported();
// 跳过和丢弃此输入流中数据的 n 个字节/字符
long skip(long n);
//将此流重新定位到最后一次对此输入流调用 mark 方法时的位置
void reset();
```

#### 3.OutputStream

```java
//向输出流中写入一个字节数据,该字节数据为参数b的低8位。 
void write(int b);
//将一个字节类型的数组中的数据写入输出流。 
void write(byte[] b);
//将一个字节类型的数组中的从指定位置（off）开始的,len个字节写入到输出流。 
void write(byte[] b, int off, int len);
//将输出流中缓冲的数据全部写出到目的地。 
void flush();
```

#### 4.Writer

```java
//向输出流中写入一个字符数据,该字节数据为参数b的低16位。 
void write(int c);
//将一个字符类型的数组中的数据写入输出流， 
void write(char[] cbuf);
//将一个字符类型的数组中的从指定位置（offset）开始的,length个字符写入到输出流。 
void write(char[] cbuf, int offset, int length);
//将一个字符串中的字符写入到输出流。 
void write(String string);
//将一个字符串从offset开始的length个字符写入到输出流。 
void write(String string, int offset, int length);
//将输出流中缓冲的数据全部写出到目的地。 
void flush();
```

关闭输出流的作用：

1. 保证流的物理资源被回收.
2. 将输出流缓冲区的数据flush到物理节点中（因为在执行close()方法之前，自动执行输出流的flush()方法）。

# Java NIO

Java NIO(New IO)是一个可以替代标准Java IO API的IO API(从Java1.4开始)，Java NIO提供了与标准IO不同的IO工作方式。

标准的IO基于字节流和字符流进行操作的，而NIO是基于通道(Channel)和缓冲区(Buffer)进行操作，数据总是从通道读取到缓冲区中，或者从缓冲区写入通道也类似。

核心部分：

- Buffer
- Channel
- Selector

传统的IO操作面向数据流，意味着每次从流中读一个或多个字节，直至完成，数据没有被缓存在任何地方。NIO操作面向缓冲区，数据从Channel读取到Buffer缓冲区，随后在Buffer中处理数据。
