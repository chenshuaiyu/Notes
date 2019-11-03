# RandomAccessFile

### 1.简介

- 可以读取文件内容，也可以输出文件内容，同时，支持随机访问，即直接跳转到文件的任意地方来读写数据。
- 可以自由访问文件的任意位置，所以如果需要访问文件的部分内容，并不是把文件从头读到尾。
- 与OutputStream、Writer等输出流不同的是，RandomAccessFile允许自由定义文件记录指针，可以从不同的地方开始输出。
- 局限性：只能读写文件，不能操作其他IO节点。
- 使用场景：网络请求中的多线程下载及断点续传。

### 2.构造方法

```java
public RandomAccessFile(String name, String mode);
public RandomAccessFile(File file, String mode)
```

- 第一个参数：指定文件名的形式不同
- 第二个参数：指定访问模式，一共有4种。
  - r：以只读方式打开，调用任何write方法都将抛出IOException。
  - rw：打开以便读取和写入。
  - rws：打开以便读取和写入。与rw的区别，对“文件内容”和“元数据”的每个更新都同步写入到基础存储设备。
  - rwd：打开以便读取和写入。与rw的区别，对“文件内容”的每个更新都同步写入到基础存储设备。

### 3.重要方法

```java
// 返回文件记录指针的当前位置
public native long getFilePointer();
//将文件指针定位到pos位置
public void seek(long pos);
```

### 4.使用

利用RandomAccessFile实现文件的多线程下载，即将文件分为几块，每块用不同的线程下载。

```java
public class Test {  
    public static void main(String[] args) throws Exception {  
        // 预分配文件所占的磁盘空间，磁盘中会创建一个指定大小的文件  
        RandomAccessFile raf = new RandomAccessFile("D://abc.txt", "rw");  
        raf.setLength(1024*1024); // 预分配 1M 的文件空间  
        raf.close();  

        // 所要写入的文件内容  
        String s1 = "第一个字符串";  
        String s2 = "第二个字符串";  
        String s3 = "第三个字符串";  
        String s4 = "第四个字符串";  
        String s5 = "第五个字符串";  

        // 利用多线程同时写入一个文件  
        new FileWriteThread(1024*1,s1.getBytes()).start(); // 从文件的1024字节之后开始写入数据  
        new FileWriteThread(1024*2,s2.getBytes()).start(); // 从文件的2048字节之后开始写入数据  
        new FileWriteThread(1024*3,s3.getBytes()).start(); // 从文件的3072字节之后开始写入数据  
        new FileWriteThread(1024*4,s4.getBytes()).start(); // 从文件的4096字节之后开始写入数据  
        new FileWriteThread(1024*5,s5.getBytes()).start(); // 从文件的5120字节之后开始写入数据  
    }  

    // 利用线程在文件的指定位置写入指定数据  
    static class FileWriteThread extends Thread{  
        private int skip;  
        private byte[] content;  

        public FileWriteThread(int skip,byte[] content){  
            this.skip = skip;  
            this.content = content;  
        }  

        public void run(){  
            RandomAccessFile raf = null;  
            try {  
                raf = new RandomAccessFile("D://abc.txt", "rw");  
                raf.seek(skip);  
                raf.write(content);  
            } catch (FileNotFoundException e) {  
                e.printStackTrace();  
            } catch (IOException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            } finally {  
                try {  
                    raf.close();  
                } catch (Exception e) {  
                }  
            }  
        }  
    }  
}
```

RandomAccessFile向指定文件中插入内容时，将会覆盖掉原有内容，如果不想覆盖掉，则需要将原有内容先读取出来，然后先把插入内容插入后再把原有内容追加到插入内容后。